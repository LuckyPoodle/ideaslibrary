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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jui.ideaslibrary.AddIdeaActivity;
import com.jui.ideaslibrary.MainActivity;
import com.jui.ideaslibrary.R;
import com.jui.ideaslibrary.model.IdeaEntry;
import com.jui.ideaslibrary.util.Util;
import com.jui.ideaslibrary.view.IdeaListActivity;
import com.jui.ideaslibrary.viewmodel.IdeaViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeaViewHolder> implements Filterable {

    private Context context;

    private List<IdeaEntry> fullIdeasList;
    private List<IdeaEntry> forSearchList;
    private IdeaViewModel ivm;

    private ListItemLongClickListener listener;



    public interface ListItemLongClickListener {
        void onListLongItemClick(int clickedItemIndex);
    }
    public void setItemLongClickListener(ListItemLongClickListener listener){
        this.listener=listener;
    }



    public IdeasAdapter(Context cxt,List<IdeaEntry> ideaEntries){   //take arraylist to store
        this.context=cxt;
        this.forSearchList=ideaEntries;
        ivm= ViewModelProviders.of((IdeaListActivity) context).get(IdeaViewModel.class);

        fullIdeasList =new ArrayList<>(forSearchList);

    }


    //a method to update this list
    public void updateIdeasList(List<IdeaEntry> newIdeas){
        forSearchList.clear();
        forSearchList.addAll(newIdeas);
        fullIdeasList =new ArrayList<>(forSearchList);
        notifyDataSetChanged();

    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<IdeaEntry> filteredList = new ArrayList<IdeaEntry>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullIdeasList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (IdeaEntry item : fullIdeasList) {
                    if (item.problemStatement.toLowerCase().contains(filterPattern) || item.thoughts.toLowerCase().contains(filterPattern)) {
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
            forSearchList.clear();
            forSearchList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };





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

        problem.setText(forSearchList.get(position).problemStatement);
        thought.setText(forSearchList.get(position).thoughts);
        location.setText(forSearchList.get(position).location);
        ImageView star=holder.itemView.findViewById(R.id.favStar);
        int isideafavourite=forSearchList.get(position).isFavourite;
        if (isideafavourite ==1){
            star.setImageResource(R.drawable.favstar);
        }



        timestamp.setText(forSearchList.get(position).timestamp.toString());
        if (forSearchList.get(position).imageUrl!=null){
            image.setVisibility(View.VISIBLE);
            Util.loadImage(image,forSearchList.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));
        }else{

        }

    }

    @Override
    public int getItemCount() {
        return forSearchList.size();
    }

    public void deleteSelectedIds(List<Integer> ids){
        ivm.deleteSelectedIdea(ids);
    }



    class IdeaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{

        public View itemView;
        ImageView ideaimage;
        TextView problem;
        ImageView starbutton;
        ImageView editbutton;
        ImageView sharebutton;
        CardView item;



        public IdeaViewHolder(@NonNull View itemView) {  //allow us to instantiate class n call its supertype
            super(itemView);
            this.itemView=itemView; //store view element inside viewholder
            item=itemView.findViewById(R.id.ideaitemLayout);
            ideaimage=itemView.findViewById(R.id.ideaImage);
            ideaimage.setOnClickListener(this);
            problem=itemView.findViewById(R.id.problem);
            starbutton=itemView.findViewById(R.id.favStar);
            starbutton.setOnClickListener(this);
            editbutton=itemView.findViewById(R.id.editicon);
            editbutton.setOnClickListener(this);
            sharebutton=itemView.findViewById(R.id.share);
            sharebutton.setOnClickListener(this);


            item.setOnClickListener(this);

            item.setOnLongClickListener(this);



        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ideaitemLayout:
                    int selected=forSearchList.get(getAdapterPosition()).IdeaUid;


                    if (item.getAlpha()==0.7f){
                        item.setAlpha(1.0f);
                        for (int i=0;i<IdeaListActivity.idsToDelete.size();i++){
                            if (IdeaListActivity.idsToDelete.get(i)==selected){
                                IdeaListActivity.idsToDelete.remove(i);
                            }
                        }

                    }

                    break;

                case R.id.ideaImage:
                    if (ideaimage.getScaleType()!= ImageView.ScaleType.FIT_XY){
                        ideaimage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        ideaimage.setScaleType(ImageView.ScaleType.FIT_XY);
                    }else{
                        int pixels = (int) (200 * context.getResources().getDisplayMetrics().density);
                        ideaimage.getLayoutParams().height = pixels;
                        ideaimage.setScaleType(ImageView.ScaleType.CENTER);
                    }

                    break;
                case R.id.favStar:

                    int ideaid=forSearchList.get(getAdapterPosition()).IdeaUid;
                    if (forSearchList.get(getAdapterPosition()).getIsFavourite()==0){
                        starbutton.setImageResource(R.drawable.favstar);
                        ivm.updateFavIdea(1,ideaid);



                    }else{
                        starbutton.setImageResource(R.drawable.blankstar);
                        ivm.updateFavIdea(0,ideaid);

                    }

                    break;
                case R.id.editicon:
                    Intent intent=new Intent(context, AddIdeaActivity.class);
                    intent.putExtra("IDEA_ID",forSearchList.get(getAdapterPosition()).IdeaUid);

                    intent.putExtra("problem",forSearchList.get(getAdapterPosition()).problemStatement);
                    intent.putExtra("idea",forSearchList.get(getAdapterPosition()).thoughts);
                    intent.putExtra("location",forSearchList.get(getAdapterPosition()).location);
                    intent.putExtra("image",forSearchList.get(getAdapterPosition()).imageUrl);
                    intent.putExtra("isFavourite",forSearchList.get(getAdapterPosition()).isFavourite);
                    context.startActivity(intent);
                    break;

                case R.id.share:
                    IdeaEntry shareIdea=forSearchList.get(getAdapterPosition());
                    if (shareIdea.imageUrl==null){

                        Intent shareintent = new Intent(Intent.ACTION_SEND);
                        shareintent.setType("text/plain");
                        shareintent.putExtra(Intent.EXTRA_SUBJECT, "Share My Idea");
                        shareintent.putExtra(Intent.EXTRA_TEXT, "The Issue is "+shareIdea.problemStatement + "\n My thoughts are "+shareIdea.thoughts +"\n"+"Date: "+shareIdea.timestamp+ "\n"+"Location: "+shareIdea.location);
                        context.startActivity(Intent.createChooser(shareintent, "Share with"));
                    }else{
                        Toast.makeText(context,"share image",Toast.LENGTH_LONG).show();

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        Uri imageToShare=prepareImagetoShare(shareIdea.imageUrl);
                        share.putExtra(Intent.EXTRA_TEXT, "The Issue is "+shareIdea.problemStatement + "\n My thoughts are "+shareIdea.thoughts +"\n"+"Date: "+shareIdea.timestamp+ "\n"+"Location: "+shareIdea.location);
                        share.putExtra(Intent.EXTRA_STREAM, imageToShare);
                        context.startActivity(Intent.createChooser(share, "Share with"));
                    }





                    break;


            }


            return;

        }

        private Uri prepareImagetoShare(String imageURL){

            Uri imageToShare = Uri.parse(imageURL); //MainActivity.this is the context in my app.
            return imageToShare;
        }

        @Override
        public boolean onLongClick(View v) {

            if (listener!=null) {

                item.setAlpha(0.7f);
                listener.onListLongItemClick(forSearchList.get(getAdapterPosition()).IdeaUid);
            }
            return true;
        }
    }




}
