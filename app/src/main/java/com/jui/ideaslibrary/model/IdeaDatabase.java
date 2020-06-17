package com.jui.ideaslibrary.model;

import android.content.Context;

import com.jui.ideaslibrary.util.TimestampConverter;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {IdeaEntry.class},version = 2)  //entities related to this particular database
@TypeConverters(TimestampConverter.class)
public abstract class IdeaDatabase extends RoomDatabase {

    //dogdatabase class be a singleton so we don create in multiple part of code, so all parts of code access the same object
    private static IdeaDatabase instance;

    static Migration migration =new Migration(1,2){

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'ideaentry' ADD COLUMN 'datetime' DEFAULT NULL ");
        }
    };

    public static IdeaDatabase getInstance(Context context){
        if (instance==null){ //applicationcontext is maintained through life
            instance= Room.databaseBuilder(context.getApplicationContext(), IdeaDatabase.class,"ideadatabase")
                    .addMigrations(migration).build();

        }
        return instance;
    }

    public abstract IdeaDAO ideaDAO();  //provide interface access

    //NOW WE HAVE DATABASE, DAO, ENTITY ANNOTATED CLASS WITH DEFINED COLUMNS








}
