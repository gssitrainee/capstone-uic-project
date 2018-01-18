package com.thesis.project.util;

import org.bson.Document;
import spark.Request;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Map;

public abstract class ResourceUtilities {

    // validates that the registration form has been filled out right and username conforms
    public static boolean validateSignup(String username, String password, String verify, String firstName, String lastName, String email, String userType, Map<String, Object> errors) {
        String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
        String PASS_RE = "^.{3,20}$";
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";
        String TYPE_RE = "^[TS]*$";

        errors.put("username_error", "");
        errors.put("password_error", "");
        errors.put("verify_error", "");
        errors.put("email_error", "");
        errors.put("userType_error", "");

        if (!username.matches(USER_RE)) {
            errors.put("username_error", "invalid username. try just letters and numbers");
            return false;
        }

        if (!password.matches(PASS_RE)) {
            errors.put("password_error", "invalid password.");
            return false;
        }

        if (!password.equals(verify)) {
            errors.put("verify_error", "password must match");
            return false;
        }

        if(null==firstName || firstName.equals("")){
            errors.put("firstName_error", "First name must not be blank");
            return false;
        }

        if(null==lastName || lastName.equals("")){
            errors.put("lastName_error", "Last name must not be blank");
            return false;
        }

        if (!email.equals("")) {
            if (!email.matches(EMAIL_RE)) {
                errors.put("email_error", "Invalid Email Address");
                return false;
            }
        }

        if(null==userType || (!userType.equals("") && !userType.matches(TYPE_RE))) {
            errors.put("userType_error", "Please select user type");
            return false;
        }

        return true;
    }

    public static boolean validateProfileUpdate(String firstName, String lastName, String email, Map<String, Object> errors) {
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

        errors.put("email_error", "");

        if(null==firstName || firstName.equals("")){
            errors.put("firstName_error", "First name must not be blank");
            return false;
        }

        if(null==lastName || lastName.equals("")){
            errors.put("lastName_error", "Last name must not be blank");
            return false;
        }

        if (!email.equals("")) {
            if (!email.matches(EMAIL_RE)) {
                errors.put("email_error", "Invalid Email Address");
                return false;
            }
        }

        return true;
    }

    // helper function to get session cookie as string
    public static String getSessionCookie(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // helper function to get session cookie as string
    public static Cookie getSessionCookieActual(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie;
            }
        }
        return null;
    }

    public static String getFlashMessage(Request request, String attribute) {
        String message = request.session().attribute(attribute);
        request.session().removeAttribute(attribute);
        return message;
    }

    // tags the tags string and put it into an array
    public static ArrayList<String> extractTags(String tags) {

        // probably more efficent ways to do this.
        //
        // whitespace = re.compile('\s')

        tags = tags.replaceAll("\\s", "");
        String tagArray[] = tags.split(",");

        // let's clean it up, removing the empty string and removing dups
        ArrayList<String> cleaned = new ArrayList<>();
        for (String tag : tagArray) {
            if (!tag.equals("") && !cleaned.contains(tag)) {
                cleaned.add(tag);
            }
        }

        return cleaned;
    }
}
