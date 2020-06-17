package com.jui.ideaslibrary.model;

import android.content.Context;

import com.jui.ideaslibrary.util.TimestampConverter;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {IdeaEntry.class},version = 2)  //entities related to this particular database
@TypeConverters(TimestampConverter.class)
public abstract class IdeaDatabase extends RoomDatabase {

    //dogdatabase class be a singleton so we don create in multiple part of code, so all parts of code access the same object
    private static IdeaDatabase instance;

    public static IdeaDatabase getInstance(Context context){
        if (instance==null){ //applicationcontext is maintained through life
            instance= Room.databaseBuilder(
                    context.getApplicationContext(),
                    IdeaDatabase.class,"ideadatabase").build();

        }
        return instance;
    }

    public abstract IdeaDAO ideaDAO();  //provide interface access

    //NOW WE HAVE DATABASE, DAO, ENTITY ANNOTATED CLASS WITH DEFINED COLUMNS








}
