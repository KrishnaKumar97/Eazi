package com.nineleaps.eazipoc.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nineleaps.eazipoc.models.MessageDatabaseModel;

import java.lang.reflect.Type;
import java.util.List;

public class TypeConverterObject {
    /**
     * Function to convert incoming List object to String
     * @return String
     */
    @androidx.room.TypeConverter
    public String fromMessageDatabaseModelList(List<MessageDatabaseModel> messageDatabaseModelList) {
        if (messageDatabaseModelList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<MessageDatabaseModel>>() {
        }.getType();
        return gson.toJson(messageDatabaseModelList, type);
    }

    /**
     * Function to convert incoming String object to List of datamodels
     * @return List of datamodels
     */
    @androidx.room.TypeConverter
    public List<MessageDatabaseModel> toMessageDatabaseModelList(String messageDatabaseModelString) {
        if (messageDatabaseModelString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<MessageDatabaseModel>>() {
        }.getType();
        return gson.fromJson(messageDatabaseModelString, type);
    }
}

