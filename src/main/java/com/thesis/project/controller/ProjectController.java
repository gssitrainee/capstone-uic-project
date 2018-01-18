package com.thesis.project.controller;


import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.thesis.project.dao.*;
import com.thesis.project.model.User;
import com.thesis.project.util.FreeMarkerTemplateEngine;
import com.thesis.project.util.ResourceUtilities;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;
import spark.ModelAndView;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thesis.project.util.JsonUtil.json;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;


public class ProjectController implements Mapper{

    private final CourseDAO courseDAO;
    private final CourseEnrollmentDAO courseEnrollmentDAO;
    private final TopicDAO topicDAO;
    private final TopicQuizDAO topicQuizDAO;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;
    private final VideoDAO videoDAO;

    private final static String NOT_AVAILABLE = "N/A";
    private final static String BLANK_VALUE = "";

    public ProjectController(String mongoURIString) throws IOException {
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

        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);
            attributes.put("showSignUp", true);

            String statusMsg = ResourceUtilities.getFlashMessage(request, "status_msg");

            if(null!=statusMsg && !"".equals(statusMsg))
                attributes.put("statusMsg", statusMsg);

            if(null!=username){
                attributes.put("username", username);
                user = userDAO.getUserInfo(username);
                if(null!=user){
                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("displayName", displayName);

                    List<String> classes = new ArrayList<>();
                    List<Document> lstUserClasses = new ArrayList<>();
                    ArrayList<Document> userClassCodes =  (ArrayList) user.get("classes");
                    if(null!=userClassCodes){
                        //System.out.println("has classes data");
                        for(Object o : userClassCodes){
                            String classCode = o.toString();
                            //System.out.println("[Class]: " + classCode);
                            classes.add(classCode);
                            lstUserClasses.add(courseDAO.findById(classCode));
                        }
                    }
                    else {
                        System.out.println("there are no class data");
                    }

                    attributes.put("userClasses", lstUserClasses);

                    if("T".equals(user.getString("userType"))){
                        List<Document> forApproval = courseEnrollmentDAO.getCourseRegistrationListForTeacher(username);
                        if(null!=forApproval && 0 < forApproval.size()) attributes.put("forApproval", forApproval);
                    }
                    else if("S".equals(user.getString("userType"))){
                        List<Document> listTopicQuiz = topicQuizDAO.findByStudentDateDescending(username);
                        String[] arClasses = classes.toArray(new String[0]);
                        List<Document> topics = topicDAO.findByClassesLatest(arClasses);
                        if(null!=topics){
                            for(Document docTopics : topics){
                                String id = docTopics.getString("_id");
                                Document topicQuiz = getStudentTakenTopicQuiz(listTopicQuiz, id);
                                if(null!=topicQuiz){
                                    ArrayList<Document> questions =  (ArrayList) topicQuiz.get("questions");
                                    int items = null!=questions ? questions.size() : 0;
                                    double scores = (double) topicQuiz.getInteger("topicScore");
                                    double percentage = 100 * (scores/items);

                                    System.out.println("[items]: " + items);
                                    System.out.println("[scores]: " + scores);
                                    System.out.println("[percentage]: " + percentage);

                                    docTopics.append("taken", true);
                                    docTopics.append("items", items);
                                    docTopics.append("scores", scores);
                                    docTopics.append("percentage", percentage);
                                }
                            }
                        }

                        attributes.put("topics", topics);
                        //attributes.put("topics", displayableTopics);
                    }
                }
            }

            return new ModelAndView(attributes, "home.ftl");
        }, new FreeMarkerTemplateEngine());

        get("/profile", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);
            attributes.put("showSignUp", false);

            String statusMsg = ResourceUtilities.getFlashMessage(request, "status_msg");

            if(null!=statusMsg && !"".equals(statusMsg))
                attributes.put("statusMsg", statusMsg);

            if (null == username) {
                System.out.println("welcome() can't identify the user, redirecting to signup");
                response.redirect("/signup");
            }
            else {
                attributes.put("username", username);
                user = userDAO.getUserInfo(username);
                if(null!=user){
                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("displayName", displayName);
                    attributes.put("firstName", user.getString("firstName"));
                    attributes.put("lastName", user.getString("lastName"));
                    attributes.put("username", user.getString("_id"));
                    attributes.put("email", user.getString("email"));

                    String type = "N/A";
                    if(null!=user.getString("userType")){
                        if("T".equalsIgnoreCase(user.getString("userType")))
                            type = "Teacher";
                        else if("S".equalsIgnoreCase(user.getString("userType")))
                            type = "Student";
                    }
                    attributes.put("type", type);
                    attributes.put("isActive", user.getBoolean("isActive"));
                }
            }

            return new ModelAndView(attributes, "profile.ftl");
        }, new FreeMarkerTemplateEngine());

        post("/profile", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if(null!=username){
                attributes.put("username", username);
                Document user = userDAO.getUserInfo(username);
                if(null!=user) {
                    String email = request.queryParams("email");
                    String firstName = request.queryParams("firstName");
                    String lastName = request.queryParams("lastName");
                    String userType = user.getString("userType");
                    String pActive = request.queryParams("isActive");
                    boolean isActive = null!=pActive && "active".equalsIgnoreCase(pActive) ? true : false;

                    attributes.put("username", StringEscapeUtils.escapeHtml4(username));
                    attributes.put("email", StringEscapeUtils.escapeHtml4(email));
                    attributes.put("firstName", firstName);
                    attributes.put("lastName", lastName);
                    attributes.put("isActive", isActive);

                    String displayName = firstName + " " + lastName;
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                    attributes.put("userType", userType);
                    attributes.put("displayName", displayName);

                    String type = "N/A";
                    if(null!=userType){
                        if("T".equalsIgnoreCase(userType))
                            type = "Teacher";
                        else if("S".equalsIgnoreCase(userType))
                            type = "Student";
                    }
                    attributes.put("type", type);

                    if (ResourceUtilities.validateProfileUpdate(firstName, lastName, email, attributes)) {
                        if (!userDAO.updateUserProfile(username, firstName, lastName, email, isActive)) {
                            attributes.put("update_error", "Unable to update profile!");
                        }
                        else {
                            attributes.put("update_success", "Successfully updated profile!");
                        }
                    }
                    else {
                        attributes.put("update_error", "User profile update did not validate!");
                    }
                }
            }
            else {
                System.out.println("welcome() can't identify the user, redirecting to signup");
                response.redirect("/signup");
            }

            return new ModelAndView(attributes, "profile.ftl");
        }, new FreeMarkerTemplateEngine());

        get("signup", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            attributes.put("showSignUp", false);
            attributes.put("hdrLink", "login");
            attributes.put("hdrLabel", "Already a user?");

            // initialize values for the form.
            attributes.put("username", "");
            attributes.put("password", "");
            attributes.put("email", "");
            attributes.put("password_error", "");
            attributes.put("username_error", "");
            attributes.put("email_error", "");
            attributes.put("verify_error", "");
            attributes.put("userType_error", "");

            return new ModelAndView(attributes, "signup.ftl");
        }, new FreeMarkerTemplateEngine());


        post("/registerUser", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            boolean success = false;

            String email = request.queryParams("email");
            String username = request.queryParams("username");
            String password = request.queryParams("password");
            String firstName = request.queryParams("firstName");
            String lastName = request.queryParams("lastName");
            String userType = request.queryParams("userType");

            attributes.put("username", username);
            attributes.put("password", password);
            attributes.put("firstName", firstName);
            attributes.put("lastName", lastName);
            attributes.put("email", email);
            attributes.put("userType", userType);

            User user = new User();
            user.set_id(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            user.setEmail(email);
            user.setUserType(userType);
            user.setActive(true);

            if(null!=user && user.validateUser())
                success = this.userDAO.addUser(user);

            if(success) {
                String sessionID = sessionDAO.startSession(user.get_id());
                response.raw().addCookie(new Cookie("session", sessionID));
                response.redirect("/");
            }
            else
                attributes.put("signup_error", "Unable to register user!");

            return new ModelAndView(attributes, "signup.ftl");
        }, new FreeMarkerTemplateEngine());


        get("login", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            attributes.put("showSignUp", true);
            attributes.put("hdrLink", "signup");
            attributes.put("hdrLabel", "Need to Create an account?");

            attributes.put("showSignUp", true);
            attributes.put("username", "");
//            attributes.put("login_error", "");
            return new ModelAndView(attributes, "login.ftl");
        }, new FreeMarkerTemplateEngine());


        post("login", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            System.out.println("Login: User submitted: " + username + "  " + password);

            Document user = userDAO.validateLogin(username, password);

            if (user != null) {
                // valid user, let's log them in
                String sessionID = sessionDAO.startSession(user.get("_id").toString());

                if (sessionID == null) {
                    response.redirect("/internal_error");
                }
                else {
                    // set the cookie for the user's browser
                    response.raw().addCookie(new Cookie("session", sessionID));
                    response.redirect("/welcome");
                }
            }
            else {
                attributes.put("showSignUp", true);
                attributes.put("hdrLink", "signup");
                attributes.put("hdrLabel", "Need to Create an account?");

                attributes.put("username", StringEscapeUtils.escapeHtml4(username));
                attributes.put("password", "");
                attributes.put("login_error", "Unable to login. Please check the username/password.");
            }

            return new ModelAndView(attributes, "login.ftl");
        }, new FreeMarkerTemplateEngine());


        get("logout", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String sessionID = ResourceUtilities.getSessionCookie(request);

            if (sessionID == null) {
                // no session to end
                response.redirect("/login");
            }
            else {
                // deletes from session table
                sessionDAO.endSession(sessionID);

                // this should delete the cookie
                Cookie c = ResourceUtilities.getSessionCookieActual(request);
                c.setMaxAge(0);

                response.raw().addCookie(c);
                response.redirect("/login");
            }

            return new ModelAndView(attributes, "signup.ftl");
        }, new FreeMarkerTemplateEngine());


        get("welcome", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if (username == null) {
                System.out.println("welcome() can't identify the user, redirecting to signup");
                response.redirect("/signup");
            }
            else {
                Document user = userDAO.getUserInfo(username);
                attributes.put("username", username);
                if(null!=user) {
                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                    attributes.put("userType", user.getString("userType"));

                    List<String> classes = new ArrayList<>();
                    List<Document> lstUserClasses = new ArrayList<>();
                    ArrayList<Document> userClassCodes =  (ArrayList) user.get("classes");
                    if(null!=userClassCodes){
                        //System.out.println("has classes data");
                        for(Object o : userClassCodes){
                            String classCode = o.toString();
                            //System.out.println("[Class]: " + classCode);
                            classes.add(classCode);
                            lstUserClasses.add(courseDAO.findById(classCode));
                        }
                    }
                    else {
                        System.out.println("there are no class data");
                    }

                    attributes.put("userClasses", lstUserClasses);

                    if("T".equals(user.getString("userType"))){
                        List<Document> forApproval = courseEnrollmentDAO.getCourseRegistrationListForTeacher(username);
                        if(null!=forApproval && 0 < forApproval.size()) attributes.put("forApproval", forApproval);
                    }
                    else if("S".equals(user.getString("userType"))){
                        List<Document> displayableTopics = new ArrayList<>();
                        List<Document> listTopicQuiz = topicQuizDAO.findByStudentDateDescending(username);

                        String[] arClasses = classes.toArray(new String[0]);
                        List<Document> topics = topicDAO.findByClassesLatest(arClasses);
                        if(null!=topics){
                            for(Document docTopics : topics){
                                String id = docTopics.getString("_id");
                                Document topicQuiz = getStudentTakenTopicQuiz(listTopicQuiz, id);
                                if(null!=topicQuiz){
                                    ArrayList<Document> questions =  (ArrayList) topicQuiz.get("questions");
                                    int items = null!=questions ? questions.size() : 0;
                                    double scores = (double) topicQuiz.getInteger("topicScore");
                                    double percentage = 100 * (scores/items);
                                    docTopics.append("taken", true);
                                    docTopics.append("items", items);
                                    docTopics.append("scores", scores);
                                    docTopics.append("percentage", percentage);
                                }
                            }
                        }

                        attributes.put("topics", topics);
                    }
                }

            }
            return new ModelAndView(attributes, "welcome.ftl");
        }, new FreeMarkerTemplateEngine());


        get("/approvals", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if (username == null) {
                System.out.println("welcome() can't identify the user, redirecting to signup");
                response.redirect("/signup");
            }
            else {
                Document user = userDAO.getUserInfo(username);
                attributes.put("username", username);
                if(null!=user) {
                    if("T".equals(user.getString("userType"))){
                        String displayName = user.getString("firstName") + " " + user.getString("lastName");
                        attributes.put("hdrLink", "");
                        attributes.put("hdrLabel", "Welcome " + displayName);
                        attributes.put("userType", user.getString("userType"));

                        List<Document> forApproval = courseEnrollmentDAO.getCourseRegistrationListForTeacher(username);
                        if(null!=forApproval && 0 < forApproval.size()) attributes.put("forApproval", forApproval);
                    }
                }
            }

            return new ModelAndView(attributes, "approvals.ftl");
        }, new FreeMarkerTemplateEngine());

        // will present the form used to process creation of new class
        get("newCourse", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Document user = null;

            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if(null!=username)
                user = userDAO.getUserInfo(username);

            if (username == null) {
                // looks like a bad request. user is not logged in
                response.redirect("/login");
            }
            else if((null!=user && "S".equals(user.getString("userType")))){
                // Students should not be able to register a class
                response.redirect("/login?errors=Students are not allowed to register a class.");
            }
            else {
                String displayName = user.getString("firstName") + " " + user.getString("lastName");
                attributes.put("hdrLink", "");
                attributes.put("hdrLabel", "Welcome " + displayName);
                attributes.put("username", username);
                attributes.put("userType", user.getString("userType"));
            }

            return new ModelAndView(attributes, "course_template.ftl");
        }, new FreeMarkerTemplateEngine());


        // will present the form used to process get and display class using class code
        get("course", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String classCode = request.queryParams("code");

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if(null!=username)
                user = userDAO.getUserInfo(username);

            if(null!=user){
                String displayName = user.getString("firstName") + " " + user.getString("lastName");
                attributes.put("userType", user.getString("userType"));
                attributes.put("hdrLink", "");
                attributes.put("hdrLabel", "Welcome " + displayName);
            }

            if(null!=classCode){
                Document docClass = courseDAO.findById(classCode);
                if(null!=docClass){
                    String className = docClass.getString("className");
                    String classDescription = docClass.getString("classDescription");

                    attributes.put("classCode", classCode);
                    attributes.put("className", className);
                    attributes.put("classDescription", classDescription);
                    attributes.put("username", username);

                    List<Document> forApproval = courseEnrollmentDAO.getCourseRegistrationList(classCode);
                    if(null!=forApproval && 0 < forApproval.size()) attributes.put("forApproval", forApproval);

                    int topicsCount = topicDAO.getCourseTopicsCount(classCode);
                    //System.out.println("\n[topicsCount]: " + topicsCount);

                    List<Document> listClassTopics = this.topicDAO.getClassTopics(classCode);
                    List<Document> listTopicQuiz = topicQuizDAO.findByCourseCodeDateDescending(classCode);
                    List<Document> classList = userDAO.getClassStudents(classCode);
                    if(null!=classList && 0 < classList.size()){
                        for(Document s : classList){
                            String student = s.getString("_id");
                            //System.out.println("\n[student]: " + student);

                            double totalScore = 0.0;
                            for(Document tq : listTopicQuiz){
                                if(tq.getString("student").equalsIgnoreCase(student)){
                                    //System.out.println("[topic-id]: " + tq.getString("topicId"));
                                    double score = tq.getInteger("topicScore");
                                    double itemCount = 0.0;
                                    //System.out.println("[score]: " + score);
                                    if(null!=tq.get("questions")){
                                        ArrayList<Document> questions =  (ArrayList) tq.get("questions");
                                        itemCount = questions.size();
                                    }

                                    double topicAverage = score/itemCount * 100;
                                    //System.out.println("[topicAverage]: " + topicAverage);
                                    totalScore+=topicAverage;
                                }
                            }

                            double totalAverage = totalScore/topicsCount;
                            s.append("totalScore", totalScore).append("totalAverage", totalAverage);
                            //System.out.println("[totalScore]: " + totalScore);
                            //System.out.println("[totalAverage]: " + totalAverage);
                        }
                        attributes.put("classTopics", listClassTopics);
                        attributes.put("classList", classList);
                    }

                }
                else {
                    attributes.put("errors", "Class not found!");
                }
            }
            else
                attributes.put("errors", "Class code is blank!");

            return new ModelAndView(attributes, "course_template.ftl");
        }, new FreeMarkerTemplateEngine());


        get("/classList", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);
            attributes.put("showSignUp", true);

            String statusMsg = ResourceUtilities.getFlashMessage(request, "status_msg");

            if(null!=statusMsg && !"".equals(statusMsg))
                attributes.put("statusMsg", statusMsg);

            if(null!=username) {
                attributes.put("username", username);
                user = userDAO.getUserInfo(username);
                if (null != user) {
                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("displayName", displayName);

                    String classCode = request.queryParams("c");
                    System.out.println("[course-code]: " + classCode);

                    if(null!=classCode) {
                        Document docClass = courseDAO.findById(classCode);
                        if (null != docClass) {
                            String className = docClass.getString("className");
                            String classDescription = docClass.getString("classDescription");

                            attributes.put("classCode", classCode);
                            attributes.put("className", className);
                            attributes.put("classDescription", classDescription);

                            List<Document> classList = this.userDAO.getClassStudents(classCode);
                            List<Document> listTopicQuiz = topicQuizDAO.findByCourseCodeDateDescending(classCode);
                            if(null!=classList && 0 < classList.size()){
                                int topicsCount = topicDAO.getCourseTopicsCount(classCode);
                                for(Document docStudent : classList){
                                    String student = docStudent.getString("_id");
                                    double totalScore = 0.0;
                                    for(Document tq : listTopicQuiz){
                                        if(tq.getString("student").equalsIgnoreCase(student)){
                                            double score = tq.getInteger("topicScore");
                                            double itemCount = 0.0;
                                            if(null!=tq.get("questions")){
                                                ArrayList<Document> questions =  (ArrayList) tq.get("questions");
                                                itemCount = questions.size();
                                            }
                                            double topicAverage = 100 * (score/itemCount);
                                            totalScore+=topicAverage;
                                        }
                                    }
                                    double totalAverage = totalScore/topicsCount;
                                    docStudent.append("totalScore", totalScore).append("totalAverage", totalAverage);
                                }
                                attributes.put("classList", classList);
                            }
                        }
                    }
                }
            }


            return new ModelAndView(attributes, "class_list.ftl");
        }, new FreeMarkerTemplateEngine());


        get("classSearch", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if(null!=username)
                user = userDAO.getUserInfo(username);

            if(null!=user){
                String displayName = user.getString("firstName") + " " + user.getString("lastName");
                attributes.put("userType", user.getString("userType"));
                attributes.put("hdrLink", "");
                attributes.put("hdrLabel", "Welcome " + displayName);
            }

            if (username == null) {
                // looks like a bad request. user is not logged in
                response.redirect("/login");
            }
            else {
                attributes.put("username", username);
            }

            return new ModelAndView(attributes, "search_class.ftl");
        }, new FreeMarkerTemplateEngine());

        get("/studentSearchPage", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String classCode = request.queryParams("c");
            System.out.println("[classCode]: " + classCode);

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);
            attributes.put("username", username);
            attributes.put("classCode", classCode);

            if(null!=username){
                user = userDAO.getUserInfo(username);

                if(null!=user){
                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                }

                if(null!=classCode) {
                    Document docClass = courseDAO.findById(classCode);
                    if (null != docClass) {
                        String className = docClass.getString("className");
                        String classDescription = docClass.getString("classDescription");
                        attributes.put("className", className);
                        attributes.put("classDescription", classDescription);
                    }
                }
            }
            else {
                response.redirect("/login");
            }

            return new ModelAndView(attributes, "search_student.ftl");
        }, new FreeMarkerTemplateEngine());


        get("searchClass",(request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            String searchKey = StringEscapeUtils.escapeHtml4(request.queryParams("searchKey"));
            List<Document> classes = courseDAO.findBySearchKey(searchKey);
            attributes.put("classes", classes);

            if(null!=username) {
                attributes.put("username", username);
                user = userDAO.getUserInfo(username);
                if(null!=user){
                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                }
            }
            else {
                response.redirect("/login");
            }

            return new ModelAndView(attributes, "search_class.ftl");
        }, new FreeMarkerTemplateEngine());


        get("/newVideo", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Document user = null;

            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if(null!=username){
                attributes.put("username", username);
                user = userDAO.getUserInfo(username);
                if(null!=user){
                    ArrayList<String> userClasses = new ArrayList<>();
                    ArrayList<Document> listUserClasses = new ArrayList<>();
                    ArrayList<String> classCodes =  (ArrayList) user.get("classes");
                    if(null!=classCodes){
                        for(String cc : classCodes){
                            userClasses.add(cc);
                            listUserClasses.add(courseDAO.findById(cc));
                        }
                    }

                    String displayName = user.getString("firstName") + " " + user.getString("lastName");
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + displayName);
                    attributes.put("userClasses", listUserClasses);
                }
            }
            else {
                response.redirect("/login");
            }

            return new ModelAndView(attributes, "videos.ftl");
        }, new FreeMarkerTemplateEngine());


        // will present the form used to process new quiz posting
        get("postTopic", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if (username == null) {
                // looks like a bad request. user is not logged in
                response.redirect("/login");
            }
            else {
                attributes.put("username", username);

                user = userDAO.getUserInfo(username);
                if(null!=user){
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + (user.getString("firstName") + " " + user.getString("lastName")));
                }

                List<Document> userClasses = courseDAO.getAllClassesByTeacher(username);
                attributes.put("userClasses", userClasses);

                List<Document> videos = this.videoDAO.getVideos(username);
                attributes.put("videos", videos);
            }

            return new ModelAndView(attributes, "new_topic_template.ftl");
        }, new FreeMarkerTemplateEngine());


        get("/viewTopic", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if (username == null)
                response.redirect("/login");
            else {
                attributes.put("username", username);

                user = userDAO.getUserInfo(username);
                if(null!=user){
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + (user.getString("firstName") + " " + user.getString("lastName")));
                    attributes.put("student", username);
                    attributes.put("studentName", (user.getString("firstName") + " " + user.getString("lastName")));
                }

                String topicId = StringEscapeUtils.escapeHtml4(request.queryParams("tid"));

                boolean allowTopicSubmit = false;
                if(null!=topicId){
                    Document docTopic = topicDAO.findById(topicId);
                    if(null!=docTopic){
                        attributes.put("topicId", docTopic.getString("_id"));

                        String classCode = docTopic.getString("classCode");
                        if(null!=classCode){
                            Document docCourse = courseDAO.findById(classCode);
                            if(null!=docCourse){
                                String course = docCourse.getString("className");
                                attributes.put("courseCode", classCode);
                                attributes.put("course", course);
                            }
                        }

                        attributes.put("topic", docTopic.getString("topic"));
                        attributes.put("summary", docTopic.getString("summary"));
                        attributes.put("videoLink", docTopic.getString("videoLink"));
                        attributes.put("showVideoPlayer", true);

                        ArrayList<Document> items =  (ArrayList) docTopic.get("items");

                        attributes.put("items", items);

                        //If student has taken the topic quiz, it should show the list of top student scores for this topic
                        if(null!=topicQuizDAO.findByTopicIdAndCourseAndStudent(topicId, username, classCode)){
                            List<Document> listTopScores = topicQuizDAO.findByTopicIdOrderByScoreAndDateDescending(topicId);
                            attributes.put("listTopScores", listTopScores);
                        }
                        else
                            allowTopicSubmit = true;

                    }
                    else {
                        System.out.println("Topic not found!");

                        attributes.put("topicId", NOT_AVAILABLE);
                        attributes.put("courseCode", BLANK_VALUE);
                        attributes.put("course", BLANK_VALUE);

                        attributes.put("topic", NOT_AVAILABLE);
                        attributes.put("summary", NOT_AVAILABLE);
                        attributes.put("videoLink", BLANK_VALUE);
                        attributes.put("showVideoPlayer", false);
                    }
                }

                attributes.put("allowTopicSubmit", allowTopicSubmit);
            }

            return new ModelAndView(attributes, "view_topic_template.ftl");
        }, new FreeMarkerTemplateEngine());


        get("/editTopic", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Gson gson = new Gson();
            Document user = null;
            String sessionId = ResourceUtilities.getSessionCookie(request);
            String username = sessionDAO.findUserNameBySessionId(sessionId);
            attributes.put("sessionId", sessionId);

            if (username == null)
                response.redirect("/login");
            else {
                attributes.put("username", username);

                user = userDAO.getUserInfo(username);
                if(null!=user){
                    attributes.put("userType", user.getString("userType"));
                    attributes.put("hdrLink", "");
                    attributes.put("hdrLabel", "Welcome " + (user.getString("firstName") + " " + user.getString("lastName")));
                }

                String topicId = StringEscapeUtils.escapeHtml4(request.queryParams("tid"));

                boolean allowTopicSubmit = false;
                if(null!=topicId){
                    Document docTopic = topicDAO.findById(topicId);
                    if(null!=docTopic){
                        attributes.put("topicId", docTopic.getString("_id"));

                        String classCode = docTopic.getString("classCode");
                        if(null!=classCode){
                            Document docCourse = courseDAO.findById(classCode);
                            if(null!=docCourse){
                                String course = docCourse.getString("className");
                                attributes.put("courseCode", classCode);
                                attributes.put("course", course);
                            }

                            List<Document> videos = this.videoDAO.getClassVideos(classCode);
                            attributes.put("videos", videos);
                        }

                        attributes.put("topic", docTopic.getString("topic"));
                        attributes.put("summary", docTopic.getString("summary"));
                        attributes.put("videoLink", docTopic.getString("videoLink"));
                        attributes.put("showVideoPlayer", true);

                        ArrayList<Document> items =  (ArrayList) docTopic.get("items");

                        attributes.put("items", items);
                        attributes.put("jsonItems", gson.toJson(items));
                        System.out.println("[json-items]: " + gson.toJson(items));

                        if(null!=topicQuizDAO.findByTopicIdAndCourseAndStudent(topicId, username, classCode)){
                            List<Document> listTopScores = topicQuizDAO.findByTopicIdOrderByScoreAndDateDescending(topicId);
                            attributes.put("listTopScores", listTopScores);
                        }
                        else
                            allowTopicSubmit = true;
                    }
                    else {
                        attributes.put("topicId", NOT_AVAILABLE);
                        attributes.put("courseCode", BLANK_VALUE);
                        attributes.put("course", BLANK_VALUE);

                        attributes.put("topic", NOT_AVAILABLE);
                        attributes.put("summary", NOT_AVAILABLE);
                        attributes.put("videoLink", BLANK_VALUE);
                        attributes.put("showVideoPlayer", false);
                    }
                }
                attributes.put("allowTopicSubmit", allowTopicSubmit);
            }
            return new ModelAndView(attributes, "edit_topic_template.ftl");
        }, new FreeMarkerTemplateEngine());






        // tells the user that the URL is dead
        get("post_not_found", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "post_not_found.ftl");
        }, new FreeMarkerTemplateEngine());


        // used to process internal errors
        get("internal_error", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("error", "System has encountered an error.");
            return new ModelAndView(attributes, "error_template.ftl");
        }, new FreeMarkerTemplateEngine());
    }

    public Document getStudentTakenTopicQuiz(List<Document> list, String id){
        if(null!=list && null!=id){
            for(Document d : list){
                String topicId = d.getString("topicId");
                if(null!=topicId && id.equalsIgnoreCase(topicId)) return d;
            }
        }
        return null;
    }
}
