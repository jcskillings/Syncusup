package com.syncusup;

/**
 * Created by Justin on 5/3/2015.
 */
import android.util.Log;

import java.util.Date;
import java.util.UUID;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
@ParseClassName("list_permissions")
public class List_permissions extends ParseObject{
    public String getPermissionType() {
        return getString("permission_type");
    }

    public void setPermissionType(String permission_type) {
        put("permission_type", permission_type);
    }
    public String getShareStatus() {
        return getString("share_status");
    }

    public void setShareStatus(String shareStatus) {
        put("share_status", shareStatus);
    }

    public String getUserId(){
        return getString("user_id");
    }
    public void setUserId(String userId){
        put("user_id", userId);
    }
    public String getUserName(){
        return getString("username");
    }
    public void setUserName(String userName){
        put("username", userName);
    }
    public void setListPointer(SyncList synclist){
        put("listPointer", synclist);
    }
    public void setListId(String listId){
        put("list_id", listId);
    }

    public static ParseQuery<List_permissions> getQuery() {
        return ParseQuery.getQuery(List_permissions.class);
    }

}
