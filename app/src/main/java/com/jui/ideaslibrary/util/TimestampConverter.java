package com.jui.ideaslibrary.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.room.TypeConverter;


public class TimestampConverter {

       @TypeConverter
        public static Long toTimestamp(Date date){
           return date==null? null:date.getTime();
       }

       @TypeConverter
        public static Date toDate(Long timestamp){
           return timestamp==null? null:new Date(timestamp);
       }
}
