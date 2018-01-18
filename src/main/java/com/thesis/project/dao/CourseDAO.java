package com.thesis.project.dao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;

public class CourseDAO {
    private final MongoCollection<Document> classCollection;

    public CourseDAO(final MongoDatabase projectDatabase) {
        classCollection = projectDatabase.getCollection("classes");
    }

    public Document findById(String classCode) {
        Document docClass = classCollection.find(eq("_id", classCode)).first();
        return docClass;
    }

    public List<Document> findBySearchKey(String searchKey) {
        if(null!=searchKey && !"".equals(searchKey)){
            //{ "$or": [{ "classCode" : { "$regex" : "ttl" , "$options" : "i"}}, { "className" : { "$regex" : "ttl" , "$options" : "i"}}] }
            //db.classes.find({ "$or": [{ "classCode" : { "$regex" : "ttl" , "$options" : "i"}}, { "className" : { "$regex" : "ttl" , "$options" : "i"}}] });
            return classCollection.find(or(regex("_id", searchKey, "i"), regex("className", searchKey, "i"))).into(new ArrayList<>());
        }

        return null;
    }

    public boolean saveClass(String username, String classCode, String className, String classDescription, String instructor){
        if(null!=username && null!=classCode && null!=className){
            Document docClass = new Document();
            docClass.append("teacher", username);
            docClass.append("className", className);

            if(null!=classDescription && !"".equals(classDescription))
                docClass.append("classDescription", classDescription);

            if(null!=instructor && !"".equals(instructor))
                docClass.append("instructor", instructor);

            docClass.append("lastModifiedDate", new Date());

            Document docUpdate = new Document("$set", docClass);

            try {
                UpdateOptions options = new UpdateOptions();
                options.upsert(true);

                classCollection.updateOne(eq("_id", classCode), docUpdate, options);

                //classCollection.insertOne(docClass);
                System.out.println("Saving class('" + classCode + "'): " + docClass.toJson());
                return true;
            } catch (Exception e) {
                System.out.println("Error saving class('" + classCode + "')");
                return false;
            }
        }

        return false;
    }

    //We will use classCode for searching the specific class to update. We will only allow class name and description as updatable fields.
    public boolean updateClass(String classCode, String className, String classDescription){
        if( (null!=classCode && !"".equals(classCode.trim())) && (null!=className && !"".equals(className.trim())) ){
            try {
                Document docClass = new Document("className", className);

                if(null!=classDescription && !"".equals(classDescription.trim()))
                    docClass.append("classDescription", classDescription);

                docClass.append("lastModifiedDate", new Date());

                classCollection.updateOne(eq("_id", classCode), docClass);
                System.out.println("Update class(course): " + classCode);
                return true;
            } catch (Exception e) {
                System.out.println("Error updating class(course)");
                return false;
            }
        }

        return false;
    }

    public boolean removeClass(String classCode){
        if(null!=classCode && !"".equals(classCode.trim())){
            try {
                classCollection.deleteOne(eq("_id", classCode));
                System.out.println("Unregistering class('" + classCode + "'): " + classCode);
                return true;
            } catch (Exception e) {
                System.out.println("Error unregistering class('" + classCode + "')");
                return false;
            }
        }
        return false;
    }


    public List<Document> getAllClassesByTeacher(String username){
        if(null!=username){
            return classCollection.find(eq("teacher", username))
                    .sort(Sorts.ascending("_id"))
                    .into(new ArrayList<>());
        }
        return null;
    }
}
