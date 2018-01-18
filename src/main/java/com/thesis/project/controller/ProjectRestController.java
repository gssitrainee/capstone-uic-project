package com.thesis.project.controller;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.thesis.project.dao.*;
import com.thesis.project.model.Topic;
import com.thesis.project.model.TopicQuiz;
import com.thesis.project.model.User;
import com.thesis.project.model.Video;
import com.thesis.project.util.FreeMarkerTemplateEngine;
import com.thesis.project.util.ResourceUtilities;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;
import spark.ModelAndView;

import java.io.IOException;
import java.util.*;

import static com.thesis.project.util.JsonUtil.json;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.redirect;

public class ProjectRestController implements Mapper {

    private final CourseDAO courseDAO;
    private final CourseEnrollmentDAO courseEnrollmentDAO;
    private final TopicDAO topicDAO;
    private final TopicQuizDAO topicQuizDAO;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;
    private final VideoDAO videoDAO;

    public ProjectRestController(String mongoURIString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final MongoDatabase projectDatabase = mongoClient.getDatabase("db_capstone");

        courseDAO = new CourseDAO(projectDatabase);
        courseEnrollmentDAO = new CourseEnrollmentDAO(projectDatabase);
        topicDAO = new TopicDAO(projectDatabase);
        topicQuizDAO = new TopicQuizDAO(projectDatabase);
        userDAO = new UserDAO(projectDatabase);
        sessionDAO = new SessionDAO(projectDatabase);
        videoDAO = new VideoDAO(projectDatabase);

        setupEndPoints();
    }

    @Override
    public void setupEndPoints() {
        get("/users", (request, response) -> userDAO.getTeachers(), json());

        get("/teachersList", (request, response) -> userDAO.getTeacherAccounts(), json());
        get("/studentsList", (request, response) -> userDAO.getStudents(), json());

        get("/searchCourse", (request, response) -> {
            String searchKey = request.queryParams("searchKey");
            return courseDAO.findBySearchKey(searchKey);
        }, json());

        get("/courseRegistrations", (request, response) -> {
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String usr = sessionDAO.findUserNameBySessionId(sessionId);

            String username = request.queryParams("su");
            String classCode = request.queryParams("cc");

            if(null!=username && null!=classCode)
                return courseEnrollmentDAO.getCourseRegistrationListForTeacherAndClass(username, classCode);
            else if(null!=classCode)
                return courseEnrollmentDAO.getCourseRegistrationList(classCode);
            else {
                return courseEnrollmentDAO.getCourseRegistrationListForTeacher(usr);
            }
        }, json());

        post("/enrollClass", (request, response) -> {
            String statusMsg = "";

            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);

            if(null!=username) {
                Document user = userDAO.getUserInfo(username);

                if(null!=user){
                    String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("cc"));
                    String studentName = user.getString("firstName") + " " + user.getString("lastName");
                    boolean allowClassEnrollment = true;
                    ArrayList enrolledClasses =  (ArrayList) user.get("classes");
                    if(null!=enrolledClasses){
                        System.out.println("there are enrolled classes");
                        for(Object o : enrolledClasses){
                            System.out.println("[Class]: " + o.toString());
                            if(o.toString().equalsIgnoreCase(classCode)){
                                allowClassEnrollment = false;
                                break;
                            }
                        }
                    }

                    if(allowClassEnrollment){
                        //TODO: Check if student is currently enrolled in selected class, if not ENROLL to class. Otherwise, return error "Currently Enrolled to Class".

                        System.out.println("[class-code]: " + classCode);
                        Document course = courseDAO.findById(classCode);
                        String teacher = "n/a";

                        if(null!=course) teacher = course.getString("teacher");

                        courseEnrollmentDAO.addCourseEnrollment(username, studentName, classCode, course.getString("className"), teacher, course.getString("instructor"));

                        statusMsg = "Class registration submitted. Please wait for teacher s approval.";
                    }
                    else {
                        statusMsg = "Student is already enrolled to the selected class.";
                    }
                }
            }
            else
                response.redirect("/login");

            return statusMsg;
        }, json());

        get("/studentSearch", (request, response) -> {
            String searchKey = StringEscapeUtils.escapeHtml4(request.queryParams("searchKey"));
            return this.userDAO.findBySearchKey(searchKey);
        }, json());

        post("/studentClassRegistration", (request, response) -> {
            String student = StringEscapeUtils.escapeHtml4(request.queryParams("su"));
            String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("cc"));
            if(null!=student && null!=classCode) {
                try{
                    userDAO.addUserClasses(student, classCode);
                    return "Successfully registered student to class.";
                }
                catch (Exception e){
                    return e.getMessage();
                }
            }
            return "Unable to process student registration.";
        }, json());

        post("/approveEnrollment", (request, response) -> {
            String student = StringEscapeUtils.escapeHtml4(request.queryParams("su"));
            String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("cc"));
            if(null!=student && null!=classCode) {
                try{
                    userDAO.addUserClasses(student, classCode);
                    courseEnrollmentDAO.removeCourseEnrollment(classCode, student);
                    return "Student Class Registration Approved.";
                }
                catch (Exception e){
                    return e.getMessage();
                }
            }
            return "Unable to process student registration.";
        }, json());

        post("/denyEnrollment", (request, response) -> {
            String student = StringEscapeUtils.escapeHtml4(request.queryParams("su"));
            String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("cc"));

            boolean success = false;
            if(null!=student && null!=classCode){
                //Remove student class registration
                success = courseEnrollmentDAO.removeCourseEnrollment(classCode, student);
            }

            if(success)
                return "Student Class Registration Denied.";
            else
                return "Unable to process student registration.";

        }, json());

        post("/dropStudent", (request, response) -> {
            String student = StringEscapeUtils.escapeHtml4(request.queryParams("su"));
            String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("cc"));

            boolean success = false;
            if(null!=student && null!=classCode){
                success = this.userDAO.removeUserCourse(student, classCode);
            }

            if(success)
                return "Successfully dropped student to class.";
            else
                return "Unable to drop student from the class.";
        }, json());

        post("/saveCourseDetails", (request, response) -> {
            String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("classCode"));
            String className = StringEscapeUtils.escapeHtml4(request.queryParams("className"));
            String classDescription = StringEscapeUtils.escapeHtml4(request.queryParams("classDescription"));

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);

            if(null!=username) {
                user = userDAO.getUserInfo(username);
                if(null!=user){
                    if(courseDAO.saveClass(username, classCode, className, classDescription, user.getString("firstName") + " " + user.getString("lastName"))) {
                        try{
                            userDAO.addUserClasses(username, classCode);
                            return "Successfully saved class(course).";
                        }
                        catch (Exception e){
                            return e.getMessage();
                        }
                    }
                }
            }

            return "Problem saving class(course).";
        }, json());


        post("/addVideo", (request, response) -> {
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);

            boolean success = false;

            if(null!=username){
                Gson gson = new Gson();
                Video video = gson.fromJson(request.body(), Video.class);

                if(null!=video){
                    video.setAuthor(username);
                    video.setCreationDate(new Date());
                    Document docVideo = Document.parse(video.toString());
                    success = this.videoDAO.addVideo(docVideo);
                }
            }

            if(success)
                return "Video successfully added!";
            else
                return "Unable to add video.";
        }, json());


        get("/getUserVideos", (request, response) -> {
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            return this.videoDAO.getVideos(username);
        }, json());

        get("/getClassVideos", (request, response) -> {
            String classCode = StringEscapeUtils.escapeHtml4(request.queryParams("c"));
            return this.videoDAO.getClassVideos(classCode);
        }, json());

        get("/getVideoDetails", (request, response) -> {
            String videoId = StringEscapeUtils.escapeHtml4(request.queryParams("v"));
            return this.videoDAO.findById(videoId);
        }, json());

        get("/allowTopicUpdate", (request, response) -> {
            String topicId = StringEscapeUtils.escapeHtml4(request.queryParams("tid"));

            boolean hasTakenQuiz = this.topicQuizDAO.hasStudentsTakenTopicQuiz(topicId);

            DisplayStatus status = new DisplayStatus();
            if(hasTakenQuiz){
                status.setStatusCode("ERROR");
                status.setStatusMessage("Update not possible to topics already taken by students!");
            }
            else {
                status.setStatusCode("OK");
                status.setStatusMessage("Redirecting to edit topic page.");
            }

            return status;
        }, json());

        post("/saveTopic", (request, response) -> {
            Gson gson = new Gson();
            Topic topic = gson.fromJson(request.body(), Topic.class);
            boolean success = false;

            if(null!=topic){
                topic.setCreationDate(new Date());
                Document docTopic = Document.parse(topic.toString());
                success = topicDAO.addTopic(docTopic);
            }

            if(success)
                return "Topic successfully posted!";
            else
                return "Unable to save topic.";
        }, json());

        post("/updateTopic", (request, response) -> {
            Gson gson = new Gson();
            Topic topic = gson.fromJson(request.body(), Topic.class);
            boolean success = false;

            if(null!=topic){
                topic.setCreationDate(new Date());
                Document docTopic = Document.parse(topic.toString());
                success = topicDAO.updateTopic(topic);
            }

            if(success)
                return "Topic successfully posted!";
            else
                return "Unable to save topic.";
        }, json());

        post("/submitTopicAnswers", (request, response) -> {
            Gson gson = new Gson();
            TopicQuiz topicQuiz = gson.fromJson(request.body(), TopicQuiz.class);
            boolean success = false;
            int score = 0;
            if(null!=topicQuiz.getTopicId()){
                Document docTopic = topicDAO.findById(topicQuiz.getTopicId());

                if(null!=topicQuiz.getQuestions()){
                    for(TopicQuiz.Question q : topicQuiz.getQuestions()){
                        List<String> answers = q.getAnswers();
                        List<String> solutions = getTopicItemAnswers(docTopic, q.getId());
                        q.setSolutions(solutions);
                        if((null!=answers && null!=solutions) && (answers.size() == solutions.size())){
                            boolean allEqual = true;
                            for(String s : answers){
                                if(!stringElementExists(solutions, s)){
                                    allEqual = false;
                                    break;
                                }
                            }
                            if(allEqual) score++;
                            q.setItemAnswerCorrect(allEqual);
                        }
                    }
                }

                topicQuiz.setTopicScore(score);

                String jsonString = topicQuiz.toString();
                if(null!=jsonString){
                    Document dTopic = Document.parse(jsonString);
                    if(null!=dTopic){

                        System.out.println("[course]: " + dTopic.getString("courseCode"));
                        System.out.println("[student]: " + dTopic.getString("student"));
                        System.out.println("[topicId]: " + dTopic.getString("topicId"));

                        Document existing = topicQuizDAO.findByTopicIdAndCourseAndStudent(dTopic.getString("topicId"), dTopic.getString("student"), dTopic.getString("courseCode"));
                        if(null!=existing)
                            return "Student already has taken the quiz.";
                        else {
                            success = topicQuizDAO.addTopicQuiz(dTopic);
                        }
                    }
                }
            }

            if(success)
                return "Successfully submitted topic answers!";

            else
                return "Unable to save topic answers!";

        }, json());

        /*
            after((request, response) -> {
                response.type("application/json");
            });
        */
    }

    private boolean stringElementExists(List<String> list, String element){
        if(null!=list && null!=element){
            for(String s : list)
                if(s.equalsIgnoreCase(element)) return true;
        }
        return false;
    }
    private List<String> getTopicItemAnswers(Document docTopic, String itemId){
        List<String> answers = new ArrayList<>();
        if(null!=docTopic && null!=itemId){
            ArrayList<Document> arDocQuestions =  (ArrayList) docTopic.get("items");
            if(null!=arDocQuestions){
                for(Document d : arDocQuestions){
                    String questionId = d.getString("id");
                    if(questionId.equals(itemId)){
                        ArrayList<Document> arDocAnswers =  (ArrayList) d.get("answers");
                        if(null!=arDocAnswers){
                            for(Object o : arDocAnswers)
                                answers.add(o.toString());

                            return answers;
                        }
                    }
                }
            }
        }

        return answers;
    }

    private static class DisplayStatus {
        private String statusCode;
        private String statusMessage;


        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }
    }
}
