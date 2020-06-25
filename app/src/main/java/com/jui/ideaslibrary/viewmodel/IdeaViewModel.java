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

package com.jui.ideaslibrary.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jui.ideaslibrary.AddIdeaActivity;
import com.jui.ideaslibrary.model.IdeaDatabase;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.view.IdeaListActivity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class IdeaViewModel  extends AndroidViewModel {
    public MutableLiveData <List<IdeaEntry>> ideasListVM =new MutableLiveData<List<IdeaEntry>>();
    //public MutableLiveData <List<IdeaEntry>> favouriteIdeas =new MutableLiveData<List<IdeaEntry>>();
    //we want to specify the type the mutablelivedata handle
    public MutableLiveData<Boolean> loadingerror=new MutableLiveData<Boolean>();
    //info if info is being loaded in background
    public MutableLiveData<Boolean> loading=new MutableLiveData<Boolean>();
    public MutableLiveData<Integer> count=new MutableLiveData<Integer>();


    private AsyncTask<Void,Void,List<IdeaEntry>>RetrieveIdeasTask;
    private AsyncTask<Void,Void,Integer> GetCountTask;
    private AsyncTask<Void,Void,List<IdeaEntry>>RetrieveFavouriteIdeasTask;

    public void getFavourites(){
        RetrieveFavouriteIdeasTask =new RetrieveFavouriteIdeasTask();
        RetrieveFavouriteIdeasTask.execute();
    }


    public void refresh(){
        fetchFromDatabase();
    }

    private void fetchFromDatabase(){
        loading.setValue(true);
        RetrieveIdeasTask=new RetrieveIdeasTask();
        RetrieveIdeasTask.execute();

    }

    public void getCount(){
        GetCountTask=new GetCountTask();
        GetCountTask.execute();
    }

    @Override
    protected void onCleared(){
        super.onCleared();

        if (RetrieveIdeasTask !=null){
            RetrieveIdeasTask.cancel(true);
            RetrieveIdeasTask=null; //for memory
        }

    }


    public IdeaViewModel(@NonNull Application application) {
        super(application);
    }
    private void ideasRetrieved(List<IdeaEntry> idealist) {
        //this is called when we have info in database locally ,
        // performing any operation in database is NOT allowed on main thread, we hav to create
        // a asynctask , a simple way to create background thread

        ideasListVM.setValue(idealist);

        loadingerror.setValue(false);
        loading.setValue(false);

    }



    //1st parameter is void cos dunnid entry parameter, 2nd parameter related to progress we dunnid, 3rd parameter is we need to provide the list to main thread to call this
    private class RetrieveIdeasTask extends AsyncTask<Void,Void, List<IdeaEntry>> {

        @Override
        protected List<IdeaEntry> doInBackground(Void... voids) {
            return IdeaDatabase.getInstance(getApplication()).ideaDAO().getAllIdeas();

        }

        //foreground thread
        @Override
        protected void onPostExecute(List<IdeaEntry> ideas){

            ideasRetrieved(ideas);
            Toast.makeText(getApplication(),"ideas retrieved from DATABASE",Toast.LENGTH_SHORT).show();

        }
    }

    private class RetrieveFavouriteIdeasTask extends AsyncTask<Void,Void, List<IdeaEntry>> {

        @Override
        protected List<IdeaEntry> doInBackground(Void... voids) {
            return IdeaDatabase.getInstance(getApplication()).ideaDAO().getFavIdeas();

        }

        //foreground thread
        @Override
        protected void onPostExecute(List<IdeaEntry> ideas){

            ideasRetrieved(ideas);
            Toast.makeText(getApplication(),"favourite ideas retrieved from DATABASE",Toast.LENGTH_SHORT).show();

        }
    }


    private class GetCountTask extends AsyncTask<Void,Void,Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return IdeaDatabase.getInstance(getApplication()).ideaDAO().getRowCount();


        }

        //foreground thread
        @Override
        protected void onPostExecute(Integer ideacount){
            Log.d("IDEAS","************************************count is "+count+"************************************");
            count.setValue(ideacount);



        }
    }

    public void updateFavIdea(int fav, int id) {
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                IdeaDatabase.getInstance(getApplication()).ideaDAO().updateFav(fav,id);
                System.out.println(IdeaDatabase.getInstance(getApplication()).ideaDAO().getFavIdeas());
            }
        });
    }

    public void deleteSelectedIdea(final List<Integer> ids) {
        Executor myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                IdeaDatabase.getInstance(getApplication()).ideaDAO().deleteSelected(ids);
            }
        });
    }




}
