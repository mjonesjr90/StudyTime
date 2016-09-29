package com.techexblog.studytime;

/**
 * Created by malcomjonesjr on 9/28/16.
 */

public class GroupDataObject {
    private String group;
    private String topic;
    private String date;

    GroupDataObject(String g, String t, String d){
        group = g;
        topic = t;
        date = d;
    }

    public String getGroup(){
        return group;
    }
    public String getTopic(){
        return topic;
    }
    public String getDate(){
        return date;
    }

    public void setGroup(String nG){
        this.group = nG;
    }
    public void setTopic(String nT){
        this.topic = nT;
    }
    public void setDate(String nD){
        this.date = nD;
    }
}
