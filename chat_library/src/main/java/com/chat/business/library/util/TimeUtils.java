package com.chat.business.library.util;

import android.content.Context;

import com.chat.business.library.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sky on 2017/6/5.
 * 时间日期辅助
 */

public final class TimeUtils {

    public TimeUtils() {
    }

    /**
     * 2013-01-01 00:00:00
     */
    public static final String TIME_STYLE_DATE_TIME_1 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 2013-01-01 00:00
     */
    public static final String TIME_STYLE_DATE_TIME_2 = "yyyy-MM-dd HH:mm";
    /**
     * 00:00:00
     */
    public static final String TIME_STYLE_DATE_TIME_3 = "HH:mm:ss";
    /**
     * 00:00
     */
    public static final String TIME_STYLE_DATE_TIME_4 = "HH:mm";
    /**
     * 2013-01-01
     */
    public static final String TIME_STYLE_DATE_TIME_5 = "yyyy-MM-dd";
    /**
     * 2015
     */
//    public static final String TIME_STYLE_DATE_TIME_6 = "yyyy";
    /**
     * 01月01日 12:00
     */
//    public static final String TIME_STYLE_DATE_TIME_7 = "MM月dd日 HH:mm";
    /**
     * 2016年01月01日
     */
//    public static final String TIME_STYLE_DATE_TIME_8 = "yyyy年MM月dd日";
    /**
     * 2017年8月22日 17：22
     */
    public static final String TIME_STYLE_DATE_TIME_9 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 2016年01月
     */
//    public static final String TIME_STYLE_DATE_TIME_10 = "yyyy年MM月";


    private static SimpleDateFormat mSimpleDateFormat;

    public static final int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};// 星座用

    private static synchronized SimpleDateFormat getDataFormatter() {
        if (mSimpleDateFormat == null) {
            mSimpleDateFormat = new SimpleDateFormat();
        }

        return mSimpleDateFormat;
    }

    /**
     * 由给定过去的时间计算距离现在的时间
     *
     * @param date 给定的时间,必须是已过去的时间
     * @return
     */
    public static String distanceBeforeNow(Date date, Context context) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return distanceBeforeNow(cal, context);
    }

    /**
     * 由给定过去的时间计算距离现在的时间
     *
     * @param date 给定的时间,必须是已过去的时间
     * @return
     */
    public static String distanceBeforeNow(Calendar date, Context context) {

        Calendar now = Calendar.getInstance();

        int year = now.get(Calendar.YEAR) - date.get(Calendar.YEAR);
        if (year < 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else if (year > 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_5);

        int day = now.get(Calendar.DAY_OF_YEAR)
                - date.get(Calendar.DAY_OF_YEAR);
        if (day < 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else if (day > 1)
//            return formats(date.getTime(), " MM" + context.getResources().getString(R.string.chat_month) + "dd" + context.getString(R.string.chat_day) + "HH:mm");
            return formats(date.getTime(), "MM-dd HH:mm");
        else if (day == 1)
//            return formats(date.getTime(), context.getResources().getString(R.string.chat_yesterday) + "HH:mm");
            return formats(date.getTime(), "HH:mm");
        int hour = now.get(Calendar.HOUR_OF_DAY)
                - date.get(Calendar.HOUR_OF_DAY);
        if (hour < 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else if (hour > 0)/*&& hour <= 4)
            return hour + "小时前";
        else if (hour > 4)*/
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_4);

        int minute = now.get(Calendar.MINUTE) - date.get(Calendar.MINUTE);
        if (minute < 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else if (minute > 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_4);//minute + "分钟前";

        int second = now.get(Calendar.SECOND) - date.get(Calendar.SECOND);
        if (second < 0)
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else
            return formats(date.getTime(), TIME_STYLE_DATE_TIME_4);//"刚刚";

    }

    /**
     * 由给定过去的时间计算距离现在的时间
     *
     * @param date 给定的时间,必须是已过去的时间
     * @return
     */
    public static String evaluateDistanceBeforeNow(Calendar date, Context context) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) - date.get(Calendar.YEAR);
        if (year < 0)
            return format(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else if (year > 0)
            return format(date.getTime(), TIME_STYLE_DATE_TIME_5);
        int day = now.get(Calendar.DAY_OF_YEAR)
                - date.get(Calendar.DAY_OF_YEAR);
        if (day < 0)
            return format(date.getTime(), TIME_STYLE_DATE_TIME_1);
        else if (day > 1)
            return format(date.getTime(), "MM-dd HH:mm");
        else if (day == 1)
            return context.getResources().getString(R.string.chat_yesterday) + format(date.getTime(), " HH:mm");
        return format(date.getTime(), TIME_STYLE_DATE_TIME_4);
    }


    /**
     * 根据格式返回日期
     *
     * @param date
     * @param style
     * @return
     */
    public static synchronized String format(Date date, String style) {
        SimpleDateFormat simpleDateFormat = getDataFormatter();
        simpleDateFormat.applyPattern(style);
        return simpleDateFormat.format(date);
    }

    /**
     * 根据格式返回日期
     *
     * @param style
     * @return
     */
    public static synchronized String formats(Date date, String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(style,Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    /**
     * 由给定的（1970年1月1日0时起的秒数）计算距离现在的时间
     *
     * @param time
     * @return
     * @throws NumberFormatException
     */
    public static String distanceSecondStartAt1970BeforeNow(String time, Context context) throws NumberFormatException {
        return distanceBeforeNow(formatFromSecondStartAt1970(time), context);
    }

    /**
     * String 转化Calendar
     *
     * @param time
     * @return
     * @throws NumberFormatException
     */
    public static String distanceSecondStartBeforeNow(String time, Context context) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_STYLE_DATE_TIME_9);
        Date date = sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return evaluateDistanceBeforeNow(calendar, context);
    }

    /**
     * 格式化1970年1月1日0时起的秒数
     *
     * @param time
     * @return
     * @throws NumberFormatException 时间错误
     */
    public static Calendar formatFromSecondStartAt1970(String time) throws NumberFormatException {
        Long t = Long.valueOf(time);
        return formatFromSecondStartAt1970(t);
    }

    /**
     * 格式化1970年1月1日0时起的秒数
     *
     * @param time
     * @return
     */
    public static Calendar formatFromSecondStartAt1970(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time * 1000);
        return calendar;
    }



    /**
     * 根据日期获取星座
     *
     * @param constellationArr 星座数组
     * @param date             时间
     * @return
     */
    public static String getConstellation(Context context, List<String> constellationArr, Date date) {
        if (date == null) {
            return "";
        }
        if (constellationArr == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr.get(month);
        }
        // default to return 魔羯
        return constellationArr.get(11);
    }

    /**
     * 判断是闰年还是正常年份
     *
     * @param year 年
     * @return
     */
    public static boolean judgeYear(int year) {
        boolean yearleap = (year % 400 == 0) || (year % 4 == 0) && (year % 100 != 0);//采用布尔数据计算判断是否能整除
        return yearleap;
    }

    /**
     * @param year  年
     * @param month 月
     * @return 根据年月，来判断这个月有多少天
     */
    public static int calculateMonthDay(int year, int month) {
        boolean yearleap = judgeYear(year);
        int day;
        if (yearleap && month == 2) {
            day = 29;
        } else if (!yearleap && month == 2) {
            day = 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = 30;
        } else {
            day = 31;
        }
        return day;
    }

    /**
     * 获取当前格式化后的日期  10-25 13：20
     */
    public static String getFormatTime() {
        //当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(calendar.get(Calendar.MONTH) + 1 > 9 ? (calendar.get(Calendar.MONTH) + 1) : "0" + (calendar.get(Calendar.MONTH) + 1))
                .append("-")
                .append(calendar.get(Calendar.DAY_OF_MONTH) > 9 ? calendar.get(Calendar.DAY_OF_MONTH) : "0" + calendar.get(Calendar.DAY_OF_MONTH))
                .append(" ")
                .append(calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) : "0" + calendar.get(Calendar.HOUR_OF_DAY))
                .append(":")
                .append(calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) : "0" + calendar.get(Calendar.MINUTE));

        return sb.toString();
    }

}
