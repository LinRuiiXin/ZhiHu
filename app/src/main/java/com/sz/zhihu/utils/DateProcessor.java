package com.sz.zhihu.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateProcessor {
    private static DateProcessor dateProcessor = new DateProcessor();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateWithHourFormat = new SimpleDateFormat("yy/MM/dd  HH:mm");
//    private SimpleDateFormat hourFormatter = new SimpleDateFormat("hh:mm");
    private DateProcessor() {
    }

    public static DateProcessor getInstance() {
        return dateProcessor;
    }

    public String processorDate(Date date) {
        String res = "";
        try {
            Date now = new Date();
            String todayString = dateFormatter.format(now);
            Date todayStart = dateFormatter.parse(todayString);

            long today = todayStart.getTime();
            long commentDate = date.getTime();
            if(commentDate-today >= 0){
                res = parseTimePeriod(commentDate-today);
            }else if(commentDate-(today-86400000)>=0){
                res = "昨天 "+ parseTimePeriod(commentDate-(today-86400000));
            }else if(commentDate-(today-172800000)>=0){
                res = "前天 " + parseTimePeriod(commentDate-(today-172800000));
            }else{
                res = dateWithHourFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String parseTimePeriod(long l) {
        String period = l<46800000 ? "上午 " : "下午 ";
        String hour =  parseHours(l);
        return period+hour;
    }

    private String parseHours(long l) {
        int hourFull = (int) (l/3600000);
        int hour = hourFull > 12 ? hourFull-12 : hourFull;
        long minMills = l % 3600000;
        int min = (int) (minMills/60000);
        return hour + ":" + min;
    }

}
