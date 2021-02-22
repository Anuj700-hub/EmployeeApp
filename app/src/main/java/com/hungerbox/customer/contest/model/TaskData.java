package com.hungerbox.customer.contest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaskData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("campaign_id")
    @Expose
    private Integer campaignId;

    public boolean getClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @SerializedName("clickable")
    @Expose
    private boolean clickable = false;

    @SerializedName("user_task_status")
    @Expose
    private String status;
    @SerializedName("task_status")
    @Expose
    private String taskStatus;
    @SerializedName("start_time")
    @Expose
    private Long startTime;
    @SerializedName("end_time")
    @Expose
    private Long endTime;
    @SerializedName("task_date")
    @Expose
    private String taskDate;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("options")
    @Expose
    private List<QuestionOption> options;
    @SerializedName("can_answer")
    @Expose
    private Boolean canAnswer;
    @SerializedName("submitted_answer")
    @Expose
    private QuestionOption submittedAnswer;
    @SerializedName("correct_answer")
    @Expose
    private QuestionOption correctAnswer;

    private int currentSelection = -1;

    private Boolean isFooter = false;

    private String footerText;

    private Boolean isFutureTask = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public Boolean getCanAnswer() {
        return canAnswer;
    }

    public void setCanAnswer(Boolean canAnswer) {
        this.canAnswer = canAnswer;
    }

    public QuestionOption getSubmittedAnswer() {
        return submittedAnswer;
    }

    public void setSubmittedAnswer(QuestionOption submittedAnswer) {
        this.submittedAnswer = submittedAnswer;
    }

    public QuestionOption getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(QuestionOption correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Boolean getFooter() {
        return isFooter;
    }

    public void setFooter(Boolean footer) {
        isFooter = footer;
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        this.currentSelection = currentSelection;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public Boolean getFutureTask() {
        return isFutureTask;
    }

    public void setFutureTask(Boolean futureTask) {
        isFutureTask = futureTask;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
