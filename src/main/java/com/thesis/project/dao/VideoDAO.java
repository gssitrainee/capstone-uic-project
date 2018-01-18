package com.thesis.project.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class VideoDAO {

    private final MongoCollection<Document> videoCollection;

    public VideoDAO(final MongoDatabase mongoDatabase) {
        videoCollection = mongoDatabase.getCollection("videos");
    }

    public Document findById(String id) {
        Document video = videoCollection.find(eq("_id", id)).first();
        return video;
    }

    public List<Document> getClassVideos(String classCode) {
        return videoCollection.find(eq("classCode", classCode)).sort(Sorts.descending("creationDate")).into(new ArrayList<>());
    }


    public List<Document> getVideos(String author) {
        return videoCollection.find(eq("author", author)).sort(Sorts.descending("creationDate")).into(new ArrayList<>());
    }

    public boolean addVideo(Document video) {
        if(null!=video){
            try {
                videoCollection.insertOne(video);
                System.out.println("Successfully added a video!");
                return true;
            } catch (Exception e) {
                System.out.println("Error adding video");
            }
        }
        return false;
    }

    public boolean removeVideo(String id) {
        if(null!=id && !"".equals(id.trim())){
            try {
                videoCollection.deleteOne(eq("_id", id));
                System.out.println("Removing video('" + id + "'): ");
                return true;
            } catch (Exception e) {
                System.out.println("Error removing video('" + id + "')");
            }
        }
        return false;
    }

}
