package com.syncusup;

/**
 * Created by Justin on 5/4/2015.
 */
import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Events")
public class Event extends ParseObject{
    public String getEventName(){
        return getString("name");
    }
    public void setEventName(String name){
        put("name", name);
    }
    public String getCreator(){
        return getString("creator");
    }
    public void setCreator(String creator){
        put("creator", creator);
    }
    public String getRepeat(){
        return getString("repeat");
    }
    public void setRepeat(String repeat){
        put("repeat", repeat);
    }
    public Boolean isEveryone(){
        return getBoolean("everyone");
    }
    public void setEveryone(Boolean everyone){
        put("everyone", everyone);
    }
    public Boolean isFamily(){
        return getBoolean("family");
    }
    public void setFamily(Boolean family){
        put("family", family);
    }
    public Boolean isFriend(){
        return getBoolean("friend");
    }
    public void setFriend(Boolean friend){
        put("friend", friend);
    }
    public Boolean isPersonal(){
        return getBoolean("personal");
    }
    public void setPersonal(Boolean personal){
        put("personal", personal);
    }
    public Boolean isSchool(){
        return getBoolean("school");
    }
    public void setSchool(Boolean school){
        put("school", school);
    }
    public Boolean isWork(){
        return getBoolean("work");
    }
    public void setWork(Boolean work){
        put("work", work);
    }
    public Boolean isPrivate(){
        return getBoolean("private");
    }
    public void setPrivate(Boolean privateBox){
        put("private", privateBox);
    }
    public String getDescription(){
        return getString("description");
    }
    public void setDescription(String description){
        put("description", description);
    }
    public Integer getStartDay(){
        return getInt("startDay");
    }
    public void setStartDay(Integer startDay){
        put("startDay", startDay);
    }
    public Integer getEndDay(){
        return getInt("endDay");
    }
    public void setEndDay(Integer endDay){
        put("endDay", endDay);
    }
    public Integer getEndMonth(){
        return getInt("endMonth");
    }
    public void setEndMonth(Integer endMonth){
        put("endMonth", endMonth);
    }
    public Integer getStartMonth(){
        return getInt("startMonth");
    }
    public void setStartMonth(Integer startMonth){
        put("startMonth", startMonth);
    }
    public Integer getEndYear(){
        return getInt("endYear");
    }
    public void setEndYear(Integer endYear){
        put("endYear", endYear);
    }
    public Integer getStartYear(){
        return getInt("startYear");
    }
    public void setStartYear(Integer startYear){
        put("startYear", startYear);
    }
    public String getStartTime(){
        return getString("startTime");
    }
    public void setStartTime(String startTime){
        put("startTime", startTime);
    }
    public String getEndTime(){
        return getString("endTime");
    }
    public void setEndTime(String endTime){
        put("endTime", endTime);
    }
    public static ParseQuery<Event> getQuery(){
        return ParseQuery.getQuery(Event.class);
    }
}
