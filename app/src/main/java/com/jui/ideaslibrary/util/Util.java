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

package com.jui.ideaslibrary.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jui.ideaslibrary.R;


import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class Util {

    public static void loadImage(ImageView imageView, String url, CircularProgressDrawable progressDrawable){
        RequestOptions options=new RequestOptions()
                .placeholder(progressDrawable)
                .error(R.mipmap.ideaslibrary_launcher);
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(options)
                .load(url)
                .into(imageView);

    }

    public static CircularProgressDrawable getProgressDrawable(Context context){
        CircularProgressDrawable cpd=new CircularProgressDrawable(context);
        cpd.setStrokeWidth(10f);
        cpd.setCenterRadius(50f);
        cpd.start();
        return cpd;

    }
}
