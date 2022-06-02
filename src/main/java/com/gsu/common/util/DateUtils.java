package com.gsu.common.util;

import com.gsu.knowledgebase.service.TextService;
import com.mysql.jdbc.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by cnytync on 06/06/15.
 */
public class DateUtils {

    private static TextService textService;
    private static final String dtfFormatString = "dd/MM/yyyy";
    private static final String dtfFormatString2 = "yyy-MM-dd";
    private static final String dtfFormatString3 = "yyyy-MM";
    private static final String dtfFormatStringWithTime = "dd/MM/yyyy HH:mm:ss";
    private static final String dtfTimeZoneString = "Europe/Istanbul";
    private static DateTimeZone dtfTimeZone = DateTimeZone.forID(dtfTimeZoneString);
    private static DateTimeFormatter dtf;
    private static DateTimeFormatter dtf2;
    private static DateTimeFormatter dtf3;
    private static DateTimeFormatter dtfWithTime;
    private static Logger logger = Logger.getLogger(DateUtils.class);

    static {
        textService = new TextService();
        try {
            textService.init();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        dtf = DateTimeFormat.forPattern(dtfFormatString);
        dtf2 = DateTimeFormat.forPattern(dtfFormatString2);
        dtf3 = DateTimeFormat.forPattern(dtfFormatString3);
        dtfWithTime = DateTimeFormat.forPattern(dtfFormatStringWithTime);
    }

    /**
     * Get date time formatter: dd/MM/yyyy
     *
     * @return Date time formatter: dd/MM/yyyy
     */
    public static DateTimeFormatter getDateTimeFormatter() {
        return dtf.withZone(dtfTimeZone);
    }

    /**
     * Get date time formatter: yyy-MM-dd
     *
     * @return Date time formatter: yyy-MM-dd
     */
    public static DateTimeFormatter getDateTimeFormatter2() {
        return dtf2.withZone(dtfTimeZone);
    }

    /**
     * Get date time formatter: yyyy-MM
     *
     * @return Date time formatter: yyyy-MM
     */
    public static DateTimeFormatter getDateTimeFormatter3() {
        return dtf3.withZone(dtfTimeZone);
    }

    /**
     * Get date time formatter with time: dd/MM/yyyy HH:mm:ss
     *
     * @return Date time formatter: dd/MM/yyyy HH:mm:ss
     */
    public static DateTimeFormatter getDateTimeFormatterWithTime() {
        return dtfWithTime.withZone(dtfTimeZone);
    }

    /**
     * Convert Java Date to Joda DateTime
     *
     * @param date Java date object
     * @return Joda DateTİme object
     */
    public static DateTime dateToDateTime(Date date) {
        if (date == null) {
            return null;
        } else {
            return new DateTime(date.getTime());
        }
    }

    /**
     * Convert timestamp to Joda DateTime
     *
     * @param timestamp Timestamp object
     * @return Joda DateTime object
     */
    public static DateTime timestampToDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return new DateTime(timestamp.getTime());
        }
    }

    /**
     * Get current date time with Turkey zone
     *
     * @return Current Joda DateTime with Turkey Zone
     */
    public static DateTime getDateNowDateTime() {
        return new DateTime().withZone(dtfTimeZone);
    }

    /**
     * Parse date time String to Joda DateTime object
     * The string date format must be as dd/MM/yyyy
     *
     * @param str String contains date time value
     * @return Jodate DateTime object
     * @throws java.text.ParseException Thrown when parse failed
     */
    public static DateTime parse(String str) throws ParseException {
        if (!StringUtils.isNullOrEmpty(str)) {
            return getDateTimeFormatter().parseDateTime(str);
        } else {
            return null;
        }
    }

    /**
     * Parse date time string with time value
     * The string date time format must be as dd/MM/yyyy HH:mm:ss
     *
     * @param str String contains date time value
     * @return Joda DateTime Object
     * @throws java.text.ParseException Thrown when parse failed
     */
    public static DateTime parseWithTime(String str) throws ParseException {
        if (!StringUtils.isNullOrEmpty(str)) {
            return getDateTimeFormatterWithTime().parseDateTime(str);
        } else {
            return null;
        }
    }

    /**
     * Format Joda DateTime object into String.
     * The String date time format will be as dd/MM/yyyy
     *
     * @param date Joda DateTime object
     * @return String contains DateTime object's date value in format as dd/MM/yyyy
     */
    public static String format(DateTime date) {
        return getDateTimeFormatter().print(date.getMillis());
    }

    /**
     * Format with yyy-mm-dd
     *
     * @param date Given date
     * @return String version of the date
     */
    public static String format2(DateTime date) {
        return getDateTimeFormatter2().print(date.getMillis());
    }

    /**
     * Format Joda DateTime object into String with time as formatted: dd/MM/yyyy HH:mm:ss
     *
     * @param date Joda DateTime object
     * @return String contains object's data formatted: dd/MM/yyyy HH:mm:ss
     */
    public static String formatWithTime(DateTime date) {
        return getDateTimeFormatterWithTime().print(date.getMillis());
    }

    public static DateTime millisToJodaTime(Timestamp timestamp) {
        if (timestamp != null) {
            return new DateTime(timestamp.getTime()).withZone(dtfTimeZone);
        } else {
            return null;
        }
    }

    /**
     * Get month String according to the given value. Values start from 1 to 12
     *
     * @param value  Value contains the sequence number of the month in year. Starts with 1.
     * @param locale Current locale value go get text with proper language
     * @return Month text with proper language
     */
    public static String getMonthString(int value, Locale locale) {
        switch (value) {
            case 1:
                return textService.getText(TextService.Keys.Month.JANUARY, locale);
            case 2:
                return textService.getText(TextService.Keys.Month.FEBRUARY, locale);
            case 3:
                return textService.getText(TextService.Keys.Month.MARCH, locale);
            case 4:
                return textService.getText(TextService.Keys.Month.APRIL, locale);
            case 5:
                return textService.getText(TextService.Keys.Month.MAY, locale);
            case 6:
                return textService.getText(TextService.Keys.Month.JUNE, locale);
            case 7:
                return textService.getText(TextService.Keys.Month.JULY, locale);
            case 8:
                return textService.getText(TextService.Keys.Month.AUGUST, locale);
            case 9:
                return textService.getText(TextService.Keys.Month.SEPTEMBER, locale);
            case 10:
                return textService.getText(TextService.Keys.Month.OCTOBER, locale);
            case 11:
                return textService.getText(TextService.Keys.Month.NOVEMBER, locale);
            case 12:
                return textService.getText(TextService.Keys.Month.DECEMBER, locale);
            default:
                return "";
        }
    }

    /**
     * Check if d1 is after or equal to d2
     *
     * @param d1 First DateTime object
     * @param d2 Second DateTime object
     * @return True if d1 is after or equal to d2
     */
    public static boolean afterEquals(DateTime d1, DateTime d2) {
        return d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0;
    }

    /**
     * Check if d1 is before or equal to d2
     *
     * @param d1 First DateTime object
     * @param d2 Second DateTime object
     * @return True if d1 is before or equal to d2
     */
    public static boolean beforeEquals(DateTime d1, DateTime d2) {
        return d1.compareTo(d2) < 0 || d1.compareTo(d2) == 0;
    }

    /**
     * The same with getDateNowDateTime
     *
     * @return Current Joda DateTime value with Turkey's time zone
     */
    public static DateTime now() {
        return getDateNowDateTime();
    }

    /**
     * Get current date String in format dd/MM/yyyy
     *
     * @return Date String in format dd/MM/yyyy
     */
    public static String getDateNow() {
        return getDateTimeFormatter().print(getDateNowDateTime().getMillis());
    }

    /**
     * Get current year and month in String in format yyyy-MM
     *
     * @return Year and month in String in format yyyy-MM
     */
    public static String getYearAndMonthNow() {
        return getDateTimeFormatter3().print(getDateNowDateTime().getMillis());
    }

    /**
     * Get current date with time in String with format: dd/MM/yyyy HH:mm:ss
     *
     * @return Current date with time in String with format: dd/MM/yyyy HH:mm:ss
     */
    public static String getDateNowWithTime() {
        return getDateTimeFormatterWithTime().print(getDateNowDateTime().getMillis());
    }

    /**
     * Get date before one month
     *
     * @return Joda DateTime before one month
     */
    public static String getOneMonthEarly() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(30);
        return getDateTimeFormatter().print(dt.getMillis());
    }

    /**
     * Get date before one month
     *
     * @return Joda DateTime before one month
     */
    public static String getOneMonthEarlyStartOfDay() {
        return getDateTimeFormatter().print(getOneMonthEarlyStartOfDayDateTime().getMillis());
    }

    /**
     * Get date before one month
     *
     * @return Joda DateTime before one month
     */
    public static DateTime getOneMonthEarlyStartOfDayDateTime() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(30);
        dt = setFirstHourOfDay(dt);
        return dt;
    }

    /**
     * Get date before one week
     *
     * @return Joda DateTime before one week
     */
    public static String getOneWeekEarly() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(7);
        return getDateTimeFormatter().print(dt.getMillis());
    }

    /**
     * Get date before one week
     *
     * @return Joda DateTime before one week
     */
    public static String getOneWeekEarlyStartOfDay() {
        return getDateTimeFormatter().print(getOneWeekEarlyStartOfDayDateTime().getMillis());
    }

    /**
     * Get date before one week
     *
     * @return Joda DateTime before one week
     */
    public static DateTime getOneWeekEarlyStartOfDayDateTime() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(7);
        dt = setFirstHourOfDay(dt);
        return dt;
    }

    /**
     * Get date before one day
     *
     * @return Joda DateTime before one day
     */
    public static String getDateYesterday() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(1);
        return getDateTimeFormatter().print(dt.getMillis());
    }

    /**
     * Get date before one day
     *
     * @return Joda DateTime before one day
     */
    public static String getDateYesterdayStartOfDay() {
        return getDateTimeFormatter().print(getDateYesterdayStartOfDayDateTime().getMillis());
    }

    /**
     * Get date before one day
     *
     * @return Joda DateTime before one day
     */
    public static DateTime getDateYesterdayStartOfDayDateTime() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(1);
        dt = setFirstHourOfDay(dt);
        return dt;
    }

    /**
     * Get date before one day
     *
     * @return Joda DateTime before one day
     */
    public static DateTime getDateBeforeOneMonth() {
        DateTime dt = getDateNowDateTime();
        dt = dt.minusDays(30);
        return dt;
    }

    /**
     * Get date after one day
     *
     * @return Joda DateTime after one day
     */
    public static String getDateTomorrow() {
        return getDateTimeFormatter().print(getDateTomorrowDateTime().getMillis());
    }

    /**
     * Get date after one day
     *
     * @return Joda DateTime after one day
     */
    public static DateTime getDateTomorrowDateTime() {
        DateTime dt = getDateNowDateTime();
        dt = dt.plusDays(1);
        return dt;
    }

    /**
     * Get current date with time value 00:00:00 (time at start of day)
     *
     * @return Current date with time value 00:00:00
     */
    public static DateTime getCurrentDateStartOfDay() {
        return new DateTime().withZone(dtfTimeZone).withTimeAtStartOfDay();
    }

    /**
     * Set hour to the last hour of the day
     *
     * @param dateTime Date time object to set time
     * @return Date time object with set last hour of day
     */
    public static DateTime setLastHourOfDay(DateTime dateTime) {
        return dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
    }

    /**
     * Set hour to the last hour of the day
     *
     * @param dateTime Date time object to set time
     * @return Date time object with set last hour of day
     */
    public static DateTime setFirstHourOfDay(DateTime dateTime) {
        return dateTime.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
    }

    public static java.sql.Timestamp getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    public static Timestamp fromDatetime(DateTime dateTime) {
        return new Timestamp(dateTime.getMillis());
    }

    public static DateTime fromResultSet(ResultSet rs, String field) throws SQLException {
        return DateUtils.millisToJodaTime(rs.getTimestamp(field));
    }

}
