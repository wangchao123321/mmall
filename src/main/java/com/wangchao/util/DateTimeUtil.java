package com.wangchao.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {

    // joda-time

    // str->Date

    // Date->str

    public static final String STANDATD_FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr,String dataFormateStr){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(dataFormateStr);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String datetoStr(Date date,String dataFormateStr){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(dataFormateStr);
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(STANDATD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String datetoStr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDATD_FORMAT);
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.datetoStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateTimeUtil.strToDate("2010-01-01 11:11:11","yyyy-MM-dd HH:mm:ss"));
    }
}
