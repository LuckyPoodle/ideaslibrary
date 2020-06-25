/*
 * Copyright 2020 Quek Rui. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jui.ideaslibrary.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public Integer getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Integer isFavourite) {
        this.isFavourite = isFavourite;
    }

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
