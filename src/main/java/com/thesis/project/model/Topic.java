package com.thesis.project.model;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

public class Topic {

    public Topic(){}

    private String _id;
    private String classCode;
    private String topic;
    private String videoLink;
    private String summary;
    private List<Item> items;
    private Date creationDate;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }



    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    public class Item {

        private String id;
        private String question;
        private String answerType;
        private List<String> choices;
        private List<String> answers;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswerType() {
            return answerType;
        }

        public void setAnswerType(String answerType) {
            this.answerType = answerType;
        }

        public List<String> getChoices() {
            return choices;
        }

        public void setChoices(List<String> choices) {
            this.choices = choices;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public void setAnswers(List<String> answers) {
            this.answers = answers;
        }
    }
}
