package com.jui.ideaslibrary.model;

import com.google.gson.annotations.SerializedName;
import com.jui.ideaslibrary.util.TimestampConverter;

import java.util.ArrayList;
import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "ideaentry")
public class IdeaEntry {

    @PrimaryKey(autoGenerate = true)
    public int IdeaUid;


    @ColumnInfo(name="timestamp")
    public String timestamp;

    @ColumnInfo(name="isFavourite")
    public Integer isFavourite;
    //0 is FALSE
    //1 is TRUE

    @ColumnInfo(name="notes")
    public String notes;


//    @ColumnInfo(name="datetime")
//    @TypeConverters({TimestampConverter.class})
//    public Date datetime;

    @ColumnInfo(name="problemStatement")
    public String problemStatement;


    @ColumnInfo(name="location")
    public String location;

    @ColumnInfo(name="thoughts")
    public String thoughts;
    @ColumnInfo(name="imageUrl")
    public String imageUrl;

    public IdeaEntry() {
    }

//    public IdeaEntry(String timestamp, Date datetime, String problemStatement, String location, String thoughts, String imageUrl) {
//        this.timestamp = timestamp;
//        this.datetime = datetime;
//        this.problemStatement = problemStatement;
//        this.location = location;
//        this.thoughts = thoughts;
//        this.imageUrl = imageUrl;
//    }

    public IdeaEntry(String timestamp,String problemStatement, String location, String thoughts, String imageUrl) {
        this.timestamp = timestamp;
        this.problemStatement = problemStatement;
        this.location = location;
        this.thoughts = thoughts;
        this.imageUrl = imageUrl;
    }
}
