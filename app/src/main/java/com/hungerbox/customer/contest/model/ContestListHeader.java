package com.hungerbox.customer.contest.model;

public class ContestListHeader {

    String header;

    public ContestListHeader(String header){
        if(header == null)
            this.header = "";
        else
            this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
