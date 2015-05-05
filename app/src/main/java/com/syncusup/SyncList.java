package com.syncusup;

/**
 * Created by Justin on 4/30/2015.
 */

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.UUID;

@ParseClassName("list")
public class SyncList extends ParseObject {
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }
    public boolean isDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean isDraft) {
        put("isDraft", isDraft);
    }
    public ParseUser getCreator(){
        return getParseUser("creator");
    }
    public void setCreator(ParseUser currentUser){
        put("creator", currentUser);
    }
    public static ParseQuery<SyncList> getQuery() {
        return ParseQuery.getQuery(SyncList.class);
    }
    public String getUuidString() {
        return getString("uuid");
    }
    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }

}