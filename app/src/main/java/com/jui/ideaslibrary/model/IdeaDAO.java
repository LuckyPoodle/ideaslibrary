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


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface IdeaDAO {

    @Query("SELECT * FROM ideaentry")
    List<IdeaEntry> getAllIdeas();

    @Query("SELECT * FROM ideaentry WHERE isFavourite==1")
    List<IdeaEntry> getFavIdeas();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertIdea(IdeaEntry m);

    @Update
    void updateIdea(IdeaEntry m);

//    @Delete
//    void deleteIdea (IdeaEntry m);

    @Query("DELETE FROM ideaentry WHERE IdeaUid= :id")
    void deleteIdea(int id);

    @Query("SELECT COUNT(IdeaUid) FROM ideaentry")
    int getRowCount();

    @Query("UPDATE ideaentry SET isFavourite=:fav WHERE IdeaUid= :id")
    void updateFav(int fav, int id);



    @Query("SELECT * FROM ideaentry WHERE problemStatement LIKE '%'||:search||'%' OR thoughts LIKE '%'||:search||'%'")
    List<IdeaEntry> getSearchedIdeas(String search);







}
