package edu.nyu.cs9223.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvetor {
    public static String convert(Date originalDate) {
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat time = new SimpleDateFormat("HH:mm:ss");
        return  date.format(originalDate)+"T"+time.format(originalDate);
    }

    public static String currentDateTime() {
        return convert(new Date());
    }
}
