package com.chatapp.commons.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class TimestampUtil {

    public final static String datetimeFormat(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp.getTime());
        return sdf.format(date);
    }
    public final static String getTimeInDate(Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        return new SimpleDateFormat("hh:mm").format(date);
    }
    public final static Timestamp getCurrentTime() {
        return new Timestamp(new Date().getTime());
    }

    public final static String convertToString(Timestamp timestamp) {
        Timestamp currTime = getCurrentTime();
        Duration diff = Duration.between(timestamp.toInstant(), currTime.toInstant());

        long days = diff.toDays();
        int weeks = (int) days / 7;
        long hours = diff.toHours();
        long minus = diff.toMinutes();
        long seconds = diff.toSeconds();

        if (weeks != 0) return weeks + " week";
        if (days != 0) return days + " ngày";
        if (hours != 0) return hours + " giờ";
        if (minus != 0) return minus + " phút";
        if (seconds != 0) return seconds + " giây";

        return "Vừa xong";
    }
}
