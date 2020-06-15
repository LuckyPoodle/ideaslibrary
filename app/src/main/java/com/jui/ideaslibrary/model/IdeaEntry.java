package com.jui.ideaslibrary.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ideaentry")
public class IdeaEntry {

    @PrimaryKey(autoGenerate = true)
    public int IdeaUid;


    @ColumnInfo(name="timestamp")
    public String timestamp;

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

    public IdeaEntry(String timestamp, String problemStatement, String location, String thoughts, String imageUrl) {
        this.timestamp = timestamp;
        this.problemStatement = problemStatement;
        this.location = location;
        this.thoughts = thoughts;
        this.imageUrl = imageUrl;
    }
}
