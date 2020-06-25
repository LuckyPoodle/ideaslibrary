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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jui.ideaslibrary.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        NotificationCompat.Builder builder =new NotificationCompat.Builder(context, "ideaslibrary")
//                .setSmallIcon(R.drawable.ideaicon)
//                .setContentTitle("What ideas have you thought of today?")
//                .setContentText("Commit to thinking of new ideas everyday!")
//                .setPriority(NotificationCompat.PRIORITY_LOW);
//
//
//        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
//
//        notificationManagerCompat.notify(888,builder.build());





    }

