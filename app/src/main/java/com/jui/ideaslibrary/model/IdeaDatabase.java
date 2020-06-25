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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {IdeaEntry.class},version = 3)  //entities related to this particular database
public abstract class IdeaDatabase extends RoomDatabase {

    //dogdatabase class be a singleton so we don create in multiple part of code, so all parts of code access the same object
    private static IdeaDatabase instance;

    static Migration migration =new Migration(1,2){

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'ideaentry' ADD COLUMN 'isFavourite' INTEGER" );
        }
    };


    static Migration migration1 =new Migration(2,3){

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'ideaentry' ADD COLUMN 'notes' TEXT" );
        }
    };

    public static IdeaDatabase getInstance(Context context){
        if (instance==null){ //applicationcontext is maintained through life
            instance= Room.databaseBuilder(context.getApplicationContext(), IdeaDatabase.class,"ideadatabase")
                    .addMigrations(migration,migration1).build();

        }

//        if (instance==null){ //applicationcontext is maintained through life
//            instance= Room.databaseBuilder(context.getApplicationContext(), IdeaDatabase.class,"ideadatabase")
//                    .build();
//
//        }
        return instance;
    }

    public abstract IdeaDAO ideaDAO();  //provide interface access

    //NOW WE HAVE DATABASE, DAO, ENTITY ANNOTATED CLASS WITH DEFINED COLUMNS








}
