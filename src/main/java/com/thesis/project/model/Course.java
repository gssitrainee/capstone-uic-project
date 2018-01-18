package com.thesis.project.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public class Course {

/*
    {
        "_id" : ObjectId("59932c1d6f04075c65f7a425"),
            "teacher" : "ejsalipahmad",
            "classCode" : "CS56",
            "className" : "Software Engineering",
            "classDescription" : "Introduction to Software Engineering",
            "creationDate" : ISODate("2017-08-15T17:15:09.339Z"),
            "lastModifiedDate" : ISODate("2017-08-15T17:15:09.339Z")
    }
*/

    private ObjectId objId;
    private String teacher;
    private String classCode;
    private String className;
    private String classDescription;
    private Date creationDate;
    private Date lastModifiedDate;

    public Course(){}
    public Course(Document dCourse){
        if(null!=dCourse){
            if(null!=dCourse.getObjectId("_id"))            this.objId = dCourse.getObjectId("_id");
            if(null!=dCourse.getString("teacher"))          this.teacher = dCourse.getString("teacher");
            if(null!=dCourse.getString("classCode"))        this.classCode = dCourse.getString("classCode");
            if(null!=dCourse.getString("className"))        this.className = dCourse.getString("className");
            if(null!=dCourse.getString("classDescription")) this.classDescription = dCourse.getString("classDescription");
            if(null!=dCourse.getDate("creationDate"))       this.creationDate = dCourse.getDate("creationDate");
            if(null!=dCourse.getDate("lastModifiedDate"))   this.lastModifiedDate = dCourse.getDate("lastModifiedDate");
        }
    }

    public Document toDocument(){
        Document doc = new Document();

        if(null!=objId) doc.append("_id", objId);
        if(null!=teacher) doc.append("teacher", teacher);
        if(null!=classCode) doc.append("classCode", classCode);
        if(null!=className) doc.append("className", className);
        if(null!=classDescription) doc.append("classDescription", classDescription);
        if(null!=creationDate) doc.append("creationDate", creationDate);
        if(null!=lastModifiedDate) doc.append("lastModifiedDate", lastModifiedDate);

        return doc;
    }

    public ObjectId getObjId() {
        return objId;
    }

    public void setObjId(ObjectId objId) {
        this.objId = objId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
