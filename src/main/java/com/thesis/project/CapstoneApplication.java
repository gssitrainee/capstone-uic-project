package com.thesis.project;

import com.thesis.project.controller.ProjectController;
import com.thesis.project.controller.ProjectRestController;
import java.io.IOException;

import static spark.Spark.*;

public class CapstoneApplication {

    private final static String DB_CONNSTR_ATLAST = "mongodb://borgymanotoy:P%40sudl%40k0_123@cluster0-shard-00-00-2xw8u.mongodb.net:27017,cluster0-shard-00-01-2xw8u.mongodb.net:27017,cluster0-shard-00-02-2xw8u.mongodb.net:27017/db_capstone?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin";
    private final static String DB_CONNSTR_LOCAL = "mongodb://localhost/db_capstone";

    public static void main(String[] args) throws IOException {
        port(getHerokuAssignedPort());
        staticFileLocation("/public");
        staticFiles.externalLocation("resources/");
        if (args.length == 0) {
            new ProjectController(DB_CONNSTR_ATLAST);
            new ProjectRestController(DB_CONNSTR_ATLAST);
        }
        else {
            new ProjectController(args[0]);
            new ProjectRestController(args[0]);
        }
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
