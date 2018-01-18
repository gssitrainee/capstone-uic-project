/*
 * Copyright 2012-2016 MongDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.thesis.project.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.thesis.project.model.User;
import org.bson.Document;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.regex;

public class UserDAO {
    private final MongoCollection<Document> usersCollection;
    private Random random = new SecureRandom();

    public UserDAO(final MongoDatabase projectDatabase) {
        usersCollection = projectDatabase.getCollection("users");
    }

    public List<Document> findBySearchKey(String searchKey) {
        if(null!=searchKey && !"".equals(searchKey))
            return usersCollection.find(and(eq("userType", "S"), or(regex("_id", searchKey, "i"), regex("firstName", searchKey, "i"), regex("lastName", searchKey, "i"), regex("email", searchKey, "i")))).into(new ArrayList<>());

        return null;
    }

    public boolean addUser(User user){
        if(null!=user){
            String passwordHash = makePasswordHash(user.getPassword(), Integer.toString(random.nextInt()));
            user.setPassword(passwordHash);
            user.setLastModifiedDate(new Date());
            Document docUser = Document.parse(user.toString());
            try {
                this.usersCollection.insertOne(docUser);
                System.out.println("Successfully registered user!");
                return true;
            } catch (Exception e) {
                System.out.println("Error registering user");
            }
        }
        return false;
    }

    public boolean updateUserProfile(String username, String firstName, String lastName, String email, boolean isActive){
        boolean status = false;
        if(null!=username){
            Document docClass = new Document();
            docClass.append("firstName", firstName);
            docClass.append("lastName", lastName);
            docClass.append("email", email);
            docClass.append("isActive", isActive);
            docClass.append("lastModifiedDate", new Date());

            Document docUpdate = new Document("$set", docClass);

            String updateQuery = docUpdate.toJson();
            System.out.println("\n[update-query]: " + updateQuery);

            usersCollection.updateOne(eq("_id", username), docUpdate);
            return true;
        }
        return status;
    }

    public boolean removeUserCourse(String username, String classCode){
        if(null!=username && null!=classCode){
            Document user = usersCollection.find(eq("_id", username)).first();
            if(null!=user){
                ArrayList<String> classes =  (ArrayList) user.get("classes");
                if(null!=classes){
                    for(int idx=0; idx < classes.size(); idx++){
                        String course = classes.get(idx);
                        System.out.println("[course]: " + course);

                        if(course.equalsIgnoreCase(classCode)){
                            classes.remove(idx);
                            break;
                        }
                    }
                }
                usersCollection.updateOne(eq("_id", username), new Document("$set", new Document("classes", classes)));
                return true;
            }
        }
        return false;
    }

    public boolean addUserClasses(String username, String classCode) throws Exception{
        if(null!=username && null!=classCode){
            //db.users.find({"_id": "borgymanotoy", "classes" : "CS56"});
            //Document userClass = usersCollection.find(and(eq("_id", username), eq("classes", classCode))).first();
            Document userClass = usersCollection.find(and(eq("_id", username), eq("classes", classCode))).first();
            if(null==userClass){
                Document updateQuery = new Document("$push", new Document("classes", classCode));
                usersCollection.updateOne(eq("_id", username), updateQuery);
                return true;
            }
            else
                throw new Exception("User-Class already exists. Cannot proceed adding user-class.");
        }
        return false;
    }


    public List<Document> getTeachers(){
        //db.users.find({userType: "T"}).pretty();
        return usersCollection.find(eq("userType", "T")).into(new ArrayList<>());
    }

    public List<Document> getStudents(){
        //db.users.find({userType: "T"}).pretty();
        return usersCollection.find(eq("userType", "S")).into(new ArrayList<>());
    }

    public List<Document> getClassStudents(String classCode){
        //db.users.find({"userType":"S","classes.code": "CS21"}).pretty();
        return usersCollection.find(and(eq("userType", "S"), eq("isActive", true), eq("classes", classCode))).into(new ArrayList<>());
    }


    public List<User> getTeacherAccounts(){
        //db.users.find({userType: "T"}).pretty();
        List<User> users = new ArrayList<>();
        List<Document> lstDocUsers = usersCollection.find(and(eq("userType", "T"), eq("isActive", true))).into(new ArrayList<>());
        for(Document d : lstDocUsers)
            users.add(new User(d));
        return users;
    }

    public List<User> getStudentAccounts(){
        //db.users.find({userType: "T"}).pretty();
        List<User> users = new ArrayList<>();
        List<Document> lstDocUsers = usersCollection.find(and(eq("userType", "S"), eq("isActive", true))).into(new ArrayList<>());
        for(Document d : lstDocUsers)
            users.add(new User(d));
        return users;
    }

    public Document getUserInfo(String username){
        if(null!=username){
            return usersCollection.find(eq("_id", username)).first();
        }

        return null;
    }

    public Document validateLogin(String username, String password) {
        Document user = usersCollection.find(or(eq("_id", username), eq("email", username))).first();
        if (user == null) return null;

        String hashedAndSalted = user.get("password").toString();
        String salt = hashedAndSalted.split(",")[1];
        String passwordHash = makePasswordHash(password, salt);
        boolean loginVerified = hashedAndSalted.equals(passwordHash);

        if (!loginVerified) return null;

        return user;
    }


    private String makePasswordHash(String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
            return encoder.encode(hashedBytes) + "," + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
        }
    }
}
