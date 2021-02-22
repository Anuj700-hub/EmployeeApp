package com.hungerbox.customer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Help {

    @SerializedName("data")
    @Expose
    private Datum data = null;

    public Datum getData() {
        return data;
    }

    public void setData(Datum data) {
        this.data = data;
    }


    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("question_groups")
        @Expose
        private List<QuestionGroup> questionGroups = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<QuestionGroup> getQuestionGroups() {
            return questionGroups;
        }

        public void setQuestionGroups(List<QuestionGroup> questionGroups) {
            this.questionGroups = questionGroups;
        }

    }

    public class QuestionGroup {

        @SerializedName("auto_collapse")
        @Expose
        private Integer auto_collapse;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("questions")
        @Expose
        private List<Question> questions = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        public Integer getAuto_collapse() {
            return auto_collapse;
        }

        public void setAuto_collapse(Integer auto_collapse) {
            this.auto_collapse = auto_collapse;
        }
    }

    public class Question implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("answer")
        @Expose
        private String answer;
        @SerializedName("auto_collapse")
        @Expose
        private Integer autoCollapse;
        @SerializedName("action_type")
        @Expose
        private String actionType;

        public ActionData getActionData() {
            return actionData;
        }

        public void setActionData(ActionData actionData) {
            this.actionData = actionData;
        }

        @SerializedName("action_data")
        @Expose
        private ActionData actionData;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public Integer getAutoCollapse() {
            return autoCollapse;
        }

        public void setAutoCollapse(Integer autoCollapse) {
            this.autoCollapse = autoCollapse;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

    }


    public class ActionData implements Serializable {

        public String getActionUrl() {
            return actionUrl;
        }

        public void setActionUrl(String actionUrl) {
            this.actionUrl = actionUrl;
        }

        public String getActionLabel() {
            return actionLabel;
        }

        public void setActionLabel(String actionLabel) {
            this.actionLabel = actionLabel;
        }

        public String getActionHeader() {
            return actionHeader;
        }

        public void setActionHeader(String actionHeader) {
            this.actionHeader = actionHeader;
        }

        @SerializedName("actionUrl")
        @Expose
        private String actionUrl;

        @SerializedName("actionLabel")
        @Expose
        private String actionLabel;

        @SerializedName("actionHeader")
        @Expose
        private String actionHeader;
    }
}
