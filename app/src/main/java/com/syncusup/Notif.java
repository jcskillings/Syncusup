package com.syncusup;
import java.util.Date;
import java.util.UUID;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Notif")
public class Notif extends ParseObject {

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }
    public String getDescription() {
        return getString("description");
    }

    public ParseUser getWhoCompleted() {
        return getParseUser("whoCompleted");
    }

    public void setWhoCreated(ParseUser currentUser) {
        put("whoCreated", currentUser);
    }
    public ParseUser getWhoCreated() {
        return getParseUser("whoCreated");
    }
    public void setWhoEdit(ParseUser currentUser) {
        put("whoEdit", currentUser);
    }
    public ParseUser getWhoEdit() {
        return getParseUser("whoEdit");
    }
    public Date getLastEdit() {
        return getDate("lastEdit");
    }
    public boolean isHidden() {
        return getBoolean("hidden");
    }
    public String getCreatePermission(){
        return getString("createPermission");
    }

    public void setHidden(boolean hidden) {
        put("hidden", hidden);
    }
    public boolean isCompleted() {
        return getBoolean("completed");
    }

    public void setCompleted(boolean completed) {
        put("completed", completed);
    }

    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }
    public boolean isDraft() {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean isDraft) {
        put("isDraft", isDraft);
    }

    public String getUuidString() {
        return getString("uuid");
    }
    public ParseObject getParentList(){
        return getParseObject("parentList");
    }


    public static ParseQuery<Notif> getQuery() {
        return ParseQuery.getQuery(Notif.class);
    }
}