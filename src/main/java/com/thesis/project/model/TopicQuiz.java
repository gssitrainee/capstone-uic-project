package com.thesis.project.model;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

public class TopicQuiz {

    private String topicId;
    private String courseCode;
    private String courseName;
    private String student;
    private String studentName;
    private List<Question> questions;
    private int topicScore;
    private Date submissionDate;



    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getTopicScore() {
        return topicScore;
    }

    public void setTopicScore(int topicScore) {
        this.topicScore = topicScore;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }


    public static class Question {
        private String id;
        private List<String> answers;
        private List<String> solutions;
        private boolean itemAnswerCorrect;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public void setAnswers(List<String> answers) {
            this.answers = answers;
        }

        public List<String> getSolutions() {
            return solutions;
        }

        public void setSolutions(List<String> solutions) {
            this.solutions = solutions;
        }

        public boolean isItemAnswerCorrect() {
            return itemAnswerCorrect;
        }

        public void setItemAnswerCorrect(boolean itemAnswerCorrect) {
            this.itemAnswerCorrect = itemAnswerCorrect;
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
