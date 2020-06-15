package com.jui.ideaslibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeaViewHolder>{

    private ArrayList<IdeaEntry> ideaslist;  //we want to display this list
    public IdeasAdapter(ArrayList<IdeaEntry> ideaEntries){   //take arraylist to store
        this.ideaslist=ideaEntries;
    }

    //a method to update this list
    public void updateDogsList(List<IdeaEntry> newIdeas){
        ideaslist.clear();
        ideaslist.addAll(newIdeas);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public IdeaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idea,parent,false);

        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeaViewHolder holder, int position) {
        //attach info from list to viewhlder
        ImageView image=holder.itemView.findViewById(R.id.imageView);
        TextView problem=holder.itemView.findViewById(R.id.problem);
        TextView thought=holder.itemView.findViewById(R.id.thought);

        problem.setText(ideaslist.get(position).problemStatement);
        thought.setText(ideaslist.get(position).thoughts);
        Util.loadImage(image,ideaslist.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));


    }

    @Override
    public int getItemCount() {
        return ideaslist.size();
    }



    class IdeaViewHolder extends RecyclerView.ViewHolder{

        public View itemView;



        public IdeaViewHolder(@NonNull View itemView) {  //allow us to instantiate class n call its supertype
            super(itemView);
            this.itemView=itemView; //store view element inside viewholder
        }
    }


}
