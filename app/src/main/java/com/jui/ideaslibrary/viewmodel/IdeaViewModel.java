package com.jui.ideaslibrary.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jui.ideaslibrary.model.IdeaDatabase;
import com.jui.ideaslibrary.model.IdeaEntry;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class IdeaViewModel  extends AndroidViewModel {
    public MutableLiveData <List<IdeaEntry>> ideasListVM =new MutableLiveData<List<IdeaEntry>>();
    //we want to specify the type the mutablelivedata handle
    public MutableLiveData<Boolean> loadingerror=new MutableLiveData<Boolean>();
    //info if info is being loaded in background
    public MutableLiveData<Boolean> loading=new MutableLiveData<Boolean>();
    public MutableLiveData<Integer> count=new MutableLiveData<Integer>();


    private AsyncTask<Void,Void,List<IdeaEntry>>RetrieveIdeasTask;
    private AsyncTask<Void,Void,Integer> GetCountTask;




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





}
