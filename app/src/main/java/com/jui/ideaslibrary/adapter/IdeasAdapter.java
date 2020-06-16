package com.jui.ideaslibrary.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jui.ideaslibrary.MainActivity;
import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeaViewHolder>{



    private ArrayList<IdeaEntry> ideaslist;  //we want to display this list


    private ListItemClickListener listener;


    public interface ListItemClickListener {
        void onListItemClick(IdeaEntry idea);
        void onLongItemClick(int clickedItemIndex);

        //Todo: Long click not working

    }

    public void setItemClickListener(ListItemClickListener listener){
        this.listener=listener;
    }


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

        LinearLayout layout=holder.itemView.findViewById(R.id.ideaitemLayout);
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return ideaslist.size();
    }



    class IdeaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{

        public View itemView;



        public IdeaViewHolder(@NonNull View itemView) {  //allow us to instantiate class n call its supertype
            super(itemView);
            this.itemView=itemView; //store view element inside viewholder
            itemView.setOnLongClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int clickedposition=getAdapterPosition();


            if (listener!=null) {
                listener.onListItemClick(ideaslist.get(clickedposition));
            }
            return;

        }

        @Override
        public boolean onLongClick(View v) {
            int clickedposition=getAdapterPosition();
            if (listener!=null) {
                listener.onListItemClick(ideaslist.get(clickedposition));
                return true;
            }
            return false;
        }
    }


}
