package com.hungerbox.customer.contest.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestionOption {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("option")
    @Expose
    private String option;
    @SerializedName("logo")
    @Expose
    private String logo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getLogo() {
        if(logo==null)
            return "";
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof QuestionOption){
          QuestionOption copy = (QuestionOption) obj;
          return ((copy.id.equals(this.id)) && (copy.option.equalsIgnoreCase(this.option)));
        } else{
            return false;
        }
    }
}
