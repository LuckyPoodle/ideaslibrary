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

package com.jui.ideaslibrary.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jui.ideaslibrary.MainActivity;
import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.util.Util;
import com.jui.ideaslibrary.view.IdeaListActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeaViewHolder> implements Filterable {

    private Context context;



    private ArrayList<IdeaEntry> ideaslist;  //we want to display this list




    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<IdeaEntry> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(ideaslist);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (IdeaEntry item : ideaslist) {
                    if (item.problemStatement.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ideaslist.clear();
            ideaslist.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };



    public IdeasAdapter(ArrayList<IdeaEntry> ideaEntries){   //take arraylist to store
        this.ideaslist=ideaEntries;
    }



    //a method to update this list
    public void updateIdeasList(List<IdeaEntry> newIdeas){
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
        ImageView image=holder.itemView.findViewById(R.id.ideaImage);
        TextView problem=holder.itemView.findViewById(R.id.problem);
        TextView thought=holder.itemView.findViewById(R.id.thought);
        TextView timestamp=holder.itemView.findViewById(R.id.timestamp);
        TextView location=holder.itemView.findViewById(R.id.locationText);

        problem.setText(ideaslist.get(position).problemStatement);
        thought.setText(ideaslist.get(position).thoughts);
        location.setText(ideaslist.get(position).location);



        timestamp.setText(ideaslist.get(position).timestamp.toString());
        if (ideaslist.get(position).imageUrl!=null){
            image.setVisibility(View.VISIBLE);
            Util.loadImage(image,ideaslist.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));
        }else{

        }






    }

    @Override
    public int getItemCount() {
        return ideaslist.size();
    }



    class IdeaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{

        public View itemView;
        ImageView ideaimage;
        TextView problem;
        ImageView starbutton;
        CardView item;



        public IdeaViewHolder(@NonNull View itemView) {  //allow us to instantiate class n call its supertype
            super(itemView);
            this.itemView=itemView; //store view element inside viewholder
            item=itemView.findViewById(R.id.ideaitemLayout);
            ideaimage=itemView.findViewById(R.id.ideaImage);
            ideaimage.setOnClickListener(this);
            problem=itemView.findViewById(R.id.problem);
            problem.setOnClickListener(this);
            starbutton=itemView.findViewById(R.id.favStar);
            starbutton.setOnClickListener(this);

            item.setOnLongClickListener(this);


        }

        @Override
        public void onClick(View v) {

            int clickedposition=getAdapterPosition();
            if (v.getId()==R.id.ideaImage){
                ideaimage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                ideaimage.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            if (v.getId()==R.id.problem){
                Log.d("IDEAS","*************************************************CLICKED ON "+ideaslist.get(getAdapterPosition()).problemStatement);
            }

            if (v.getId()==R.id.favStar){
                starbutton.setImageResource(R.drawable.favstar);
            }
//
//
//            if (listener!=null) {
//                listener.onListItemClick(ideaslist.get(clickedposition));
//            }

            return;

        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId()==R.id.ideaitemLayout){
                Log.d("IDEAS","+++++++++++++Long clicked on "+ideaslist.get(getAdapterPosition()).problemStatement);

            }
            return true;
        }
    }




}
