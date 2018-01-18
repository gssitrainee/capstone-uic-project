package com.thesis.project.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class TopicQuizDAO {
    private final MongoCollection<Document> topicQuizCollection;

    public TopicQuizDAO(final MongoDatabase blogDatabase){
        topicQuizCollection = blogDatabase.getCollection("topic_quiz");
    }

    public Document findByTopicId(String topicId) {
        Document topicQuiz = topicQuizCollection.find(eq("topicId", topicId)).first();
        return topicQuiz;
    }

    public Document findByTopicIdAndCourseAndStudent(String topicId, String student, String courseCode) {
        Document topicQuiz = topicQuizCollection.find(and(eq("topicId", topicId), eq("student", student), eq("courseCode", courseCode))).first();
        return topicQuiz;
    }

    public boolean addTopicQuiz(Document topicQuiz) {
        if(null!=topicQuiz){
            try {
                topicQuizCollection.insertOne(topicQuiz);
                System.out.println("Successfully recorded quiz submission.");
                return true;
            } catch (Exception e) {
                System.out.println("Error processing quiz submission.");
            }
        }

        return false;
    }

    public boolean removeTopicQuiz(String topicId) {
        if(null!=topicId && !"".equals(topicId.trim())){
            try {
                topicQuizCollection.deleteOne(eq("topicId", topicId));
                System.out.println("Removing topic quiz('" + topicId + "'): ");
                return true;
            } catch (Exception e) {
                System.out.println("Error removing topic quiz('" + topicId + "')");
            }
        }
        return false;
    }

    public List<Document> findByCourseCodeDateDescending(String courseCode) {
        return topicQuizCollection.find(eq("courseCode", courseCode))
                .sort(descending("submissionDate"))
                .into(new ArrayList<>());
    }

    public List<Document> findByStudentDateDescending(String student) {
        return topicQuizCollection.find(eq("student", student))
                .sort(descending("submissionDate"))
                .limit(20)
                .into(new ArrayList<>());
    }

    public boolean hasStudentsTakenTopicQuiz(String topicId) {
        long countTookQuiz = topicQuizCollection.count(eq("topicId", topicId));
        return 0 < countTookQuiz ? true : false;
    }

    public List<Document> findByTopicIdDateDescending(String topicId) {
        return topicQuizCollection.find(eq("topicId", topicId))
                .sort(descending("submissionDate"))
                .limit(20)
                .into(new ArrayList<>());
    }

    public List<Document> findByTopicIdOrderByScoreAndDateDescending(String topicId) {
        return topicQuizCollection.find(eq("topicId", topicId))
                .sort(and(descending("topicScore"), descending("submissionDate")))
                .limit(20)
                .into(new ArrayList<>());
    }

    public List<Document> findByDateDescending(int limit) {
        return topicQuizCollection.find()
                .sort(descending("submissionDate"))
                .limit(limit)
                .into(new ArrayList<>());
    }
}
