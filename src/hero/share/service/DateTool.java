// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool extends Date {

    private static final long serialVersionUID = 1L;
    private static SimpleDateFormat sFormat1;
    private static SimpleDateFormat sFormat2;
    private static SimpleDateFormat sFormat3;
    private static SimpleDateFormat sFormat4;
    private static SimpleDateFormat sFormat5;
    private static SimpleDateFormat sFormat6;
    private static SimpleDateFormat sFormat7;
    private static SimpleDateFormat sFormat8;
    private static SimpleDateFormat sFormat9;
    private static SimpleDateFormat sFormat10;
    private static SimpleDateFormat sFormat15;
    private static SimpleDateFormat sFormat17;
    private static SimpleDateFormat sFormat18;

    static {
        DateTool.sFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateTool.sFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        DateTool.sFormat3 = new SimpleDateFormat("yyyy/MM/dd");
        DateTool.sFormat4 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        DateTool.sFormat5 = new SimpleDateFormat("HH:mm");
        DateTool.sFormat6 = new SimpleDateFormat("h:mm a");
        DateTool.sFormat7 = new SimpleDateFormat("yyyyMMddHHmmss");
        DateTool.sFormat8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTool.sFormat9 = new SimpleDateFormat("ddMMMyy");
        DateTool.sFormat10 = new SimpleDateFormat("yyyyMMdd");
        DateTool.sFormat15 = new SimpleDateFormat("yyMM");
        DateTool.sFormat17 = new SimpleDateFormat("MM/dd/yyyy");
        DateTool.sFormat18 = new SimpleDateFormat("dd/MM/yyyy");
    }

    public DateTool() {
        super(getSystemDate().getTime().getTime());
    }

    public Timestamp parseTime() {
        return new Timestamp(this.getTime());
    }

    public java.sql.Date parseDate() {
        return new java.sql.Date(this.getTime());
    }

    public static Calendar getSystemDate() {
        return Calendar.getInstance();
    }

    public static Date getAfterDay(final Date date, final int afterDays) {
        GregorianCalendar cal = new GregorianCalendar();
        if (date == null) {
            cal.setTime(new DateTool());
        } else {
            cal.setTime(date);
        }
        cal.add(5, afterDays);
        return cal.getTime();
    }

    public static String getAfterMonth(final String sDate, final int afterMonth) {
        Date date = null;
        try {
            date = DateTool.sFormat1.parse(sDate);
            date = getAfterMonth(date, afterMonth);
            return DateTool.sFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getAfterMonth(final Date date, final int afterMonth) {
        GregorianCalendar cal = new GregorianCalendar();
        if (date == null) {
            cal.setTime(new DateTool());
        } else {
            cal.setTime(date);
        }
        cal.add(2, afterMonth);
        return cal.getTime();
    }

    public static String getAfterDay(final String sDate, final int afterDays) {
        Date date = null;
        try {
            date = convertLongDate(sDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        date = getAfterDay(date, afterDays);
        return formatDate(date);
    }

    public static String formatDate(final Date date) {
        if (date == null) {
            return "";
        }
        return DateTool.sFormat2.format(date);
    }

    public static Date convertShortDate(final String sDate) {
        try {
            return DateTool.sFormat2.parse(sDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date convertLongDate(final String sDate) {
        try {
            return DateTool.sFormat1.parse(sDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Timestamp convertTimestamp1(final String sDate) {
        long lDate = 0L;
        try {
            lDate = convertLongDate(sDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Timestamp(lDate);
    }

    public static Timestamp convertTimestamp2(final String sDate) {
        long lDate = 0L;
        try {
            lDate = convertShortDate(sDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Timestamp(lDate);
    }

    public static Timestamp convertTimestamp(String sDate) {
        if (sDate.length() == 10) {
            sDate = String.valueOf(sDate) + " 00:00:00";
        }
        return Timestamp.valueOf(sDate);
    }

    public static Timestamp convertTimestampE(String sDate) {
        if (sDate.length() == 10) {
            sDate = String.valueOf(sDate) + " 23:59:59.0";
        }
        return Timestamp.valueOf(sDate);
    }

    public static int getDayByLong(final long _time) {
        int result = 0;
        float second = (float) (_time / 1000L);
        second = second / 60.0f / 60.0f / 24.0f;
        result = (int) second;
        return result;
    }

    public static int getDifference(final Date date1, final Date date2) {
        int day = 0;
        Calendar cld1 = Calendar.getInstance();
        Calendar cld2 = Calendar.getInstance();
        cld1.setTime(date1);
        cld2.setTime(date2);
        day = cld2.get(5) - cld1.get(5);
        return day;
    }

    public static long getDateDifference(final Date date1, final Date date2) {
        Calendar cld1Work = Calendar.getInstance();
        Calendar cld2Work = Calendar.getInstance();
        Calendar cld1 = Calendar.getInstance();
        Calendar cld2 = Calendar.getInstance();
        cld1Work.setTime(date1);
        cld2Work.setTime(date2);
        cld1.clear();
        cld2.clear();
        cld1.set(cld1Work.get(1), cld1Work.get(2), cld1Work.get(5));
        cld2.set(cld2Work.get(1), cld2Work.get(2), cld2Work.get(5));
        long lTime1 = cld1.getTime().getTime();
        long lTime2 = cld2.getTime().getTime();
        return (lTime2 - lTime1) / 86400000L;
    }

    public static String getDateDifferenceToStr(final String start_date, final String end_date) {
        String ret = "";
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date first = format.parse(start_date);
            Date second = format.parse(end_date);
            ret = String.valueOf(getDateDifference(first, second) + 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String formatDate(String sTsp, final int iType) {
        if (sTsp == null || "".equals(sTsp)) {
            return "";
        }
        if (sTsp.length() == 10) {
            return formatDate(convertTimestamp(sTsp), iType);
        }
        if (sTsp.length() > 10) {
            String[] sDatas = sTsp.split("\\.");
            if (sDatas.length > 2 && sDatas.length > 1) {
                String[] sDates = sDatas[0].split("-");
                if (sDates[1].length() == 1) {
                    sDates[1] = "0" + sDates[1];
                }
                if (sDates[2].length() == 1) {
                    sDates[2] = "0" + sDates[2];
                }
                for (int i = 1; i < 4; ++i) {
                    sDatas[i] = sDatas[i].trim();
                    if (sDatas[i].length() == 1) {
                        sDatas[i] = "0" + sDatas[i];
                    }
                }
                sTsp = String.valueOf(sDates[0]) + "-" + sDates[1] + "-" + sDates[2] + " " + sDatas[1] + ":" + sDatas[2] + ":" + sDatas[3] + ".000000000";
            }
            return formatDate(Timestamp.valueOf(sTsp), iType);
        }
        return "";
    }

    public static String formatDate(final Timestamp tsp, final int iType) {
        GregorianCalendar cal = new GregorianCalendar();
        if (tsp == null) {
            cal.setTime(new DateTool());
        } else {
            cal.setTime(tsp);
        }
        String sDate = "";
        if (iType == 0) {
            int iYear = cal.get(1);
            int iMonth = cal.get(2) + 1;
            int iDay = cal.get(5);
            sDate = iYear + "\u5e74" + iMonth + "\u6708" + iDay + "\u65e5";
        }
        if (iType == 1) {
            int iYear = cal.get(1);
            int iMonth = cal.get(2) + 1;
            int iDay = cal.get(5);
            sDate = iYear + "-" + iMonth + "-" + iDay;
        }
        if (iType == 2) {
            int iYear = cal.get(1);
            int iMonth = cal.get(2) + 1;
            int iDay = cal.get(5);
            sDate = iYear + "/" + iMonth + "/" + iDay;
        }
        if (iType == 3) {
            String strYear = String.valueOf(cal.get(1));
            String strMonth = String.valueOf(cal.get(2) + 1);
            strMonth = "00" + strMonth;
            strMonth = strMonth.substring(strMonth.length() - 2, strMonth.length());
            String strDay = String.valueOf(cal.get(5));
            strDay = "00" + strDay;
            strDay = strDay.substring(strDay.length() - 2, strDay.length());
            sDate = strYear + "\u5e74" + strMonth + "\u6708" + strDay + "\u65e5";
        }
        if (iType == 4) {
            sDate = DateTool.sFormat2.format(cal.getTime());
        }
        if (iType == 5) {
            sDate = DateTool.sFormat3.format(cal.getTime());
        }
        if (iType == 6) {
            sDate = DateTool.sFormat1.format(cal.getTime());
        }
        if (iType == 7) {
            sDate = DateTool.sFormat4.format(cal.getTime());
        }
        if (iType == 8) {
            sDate = DateTool.sFormat5.format(cal.getTime());
        }
        if (iType == 9) {
            sDate = DateTool.sFormat6.format(cal.getTime());
        }
        if (iType == 10) {
            sDate = DateTool.sFormat10.format(cal.getTime());
        }
        if (iType == 11) {
            sDate = DateTool.sFormat7.format(cal.getTime());
        }
        if (iType == 12) {
            sDate = DateTool.sFormat8.format(cal.getTime());
        }
        if (iType == 13) {
            sDate = DateTool.sFormat8.format(cal.getTime());
            sDate = sDate.substring(10);
        }
        if (iType == 14) {
            sDate = DateTool.sFormat9.format(cal.getTime());
        }
        if (iType == 15) {
            sDate = DateTool.sFormat15.format(cal.getTime());
        }
        if (iType == 16) {
            int iYear = cal.get(1);
            int iMonth = cal.get(2) + 1;
            int iDay = cal.get(5);
            switch (iMonth) {
                case 1: {
                    cal.set(iYear, 0, iDay);
                    break;
                }
                case 2: {
                    cal.set(iYear, 1, iDay);
                    break;
                }
                case 3: {
                    cal.set(iYear, 2, iDay);
                    break;
                }
                case 4: {
                    cal.set(iYear, 3, iDay);
                    break;
                }
                case 5: {
                    cal.set(iYear, 4, iDay);
                    break;
                }
                case 6: {
                    cal.set(iYear, 5, iDay);
                    break;
                }
                case 7: {
                    cal.set(iYear, 6, iDay);
                    break;
                }
                case 8: {
                    cal.set(iYear, 7, iDay);
                    break;
                }
                case 9: {
                    cal.set(iYear, 8, iDay);
                    break;
                }
                case 10: {
                    cal.set(iYear, 9, iDay);
                    break;
                }
                case 11: {
                    cal.set(iYear, 10, iDay);
                    break;
                }
                case 12: {
                    cal.set(iYear, 11, iDay);
                    break;
                }
            }
            cal.add(2, -1);
            sDate = DateTool.sFormat2.format(cal.getTime());
        }
        if (iType == 17) {
            sDate = DateTool.sFormat17.format(cal.getTime());
        }
        if (iType == 18) {
            sDate = DateTool.sFormat18.format(cal.getTime());
        }
        return sDate;
    }

    public static Timestamp gettimebefore(final long lminute) {
        Timestamp tsp = new DateTool().parseTime();
        long lngTime = tsp.getTime() - lminute * 60L * 1000L;
        return new Timestamp(lngTime);
    }

    public static Timestamp gettimebefore(final Date date, final long lminute) {
        long lngTime = date.getTime() - lminute * 60L * 1000L;
        return new Timestamp(lngTime);
    }

    public static String getFlyingTime(final String start, final String end) {
        int iSt = Integer.valueOf(start);
        int iEn = Integer.valueOf(end);
        String rtn = null;
        if (iEn <= iSt) {
            iEn += 2400;
        }
        int hour = iEn / 100 - 1 - iSt / 100;
        int mini = iEn % 100 + 60 - iSt % 100;
        if (mini >= 60) {
            mini -= 60;
            ++hour;
        }
        if (mini == 0) {
            rtn = String.valueOf(hour) + "\u5c0f\u65f6";
        } else if (hour == 0) {
            rtn = String.valueOf(mini) + "\u5206\u949f";
        } else {
            rtn = String.valueOf(hour) + "\u5c0f\u65f6" + mini + "\u5206\u949f";
        }
        return rtn;
    }

    public static String getMultiLangFlyingTime(final String start, final String end) {
        int iSt = Integer.valueOf(start);
        int iEn = Integer.valueOf(end);
        String rtn = null;
        if (iEn <= iSt) {
            iEn += 2400;
        }
        int hour = iEn / 100 - 1 - iSt / 100;
        int mini = iEn % 100 + 60 - iSt % 100;
        if (mini >= 60) {
            mini -= 60;
            ++hour;
        }
        if (mini == 0) {
            rtn = String.valueOf(hour) + "hr ";
        } else if (hour == 0) {
            rtn = String.valueOf(mini) + "mn";
        } else {
            rtn = String.valueOf(hour) + "hr " + mini + "mn";
        }
        return rtn;
    }

    public static int getFlyingMunites(final String start, final String end) {
        int iSt = Integer.valueOf(start);
        int iEn = Integer.valueOf(end);
        int rtn = 0;
        if (iEn <= iSt) {
            iEn += 2400;
        }
        int hour = iEn / 100 - 1 - iSt / 100;
        int mini = iEn % 100 + 60 - iSt % 100;
        rtn = hour * 60 + mini;
        return rtn;
    }

    public static String toTimeStamp(final Timestamp timestamp) {
        String sql = "to_timestamp('" + timestamp.toString() + "' , 'yyyy-mm-dd hh24:mi:ssxff')";
        return sql;
    }

    public static String toTimeStamp(final String s) {
        String sql = "to_timestamp('" + s + "' , 'yyyy-mm-dd hh24:mi:ssxff')";
        return sql;
    }

    public static String LastDay(final String s) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar gc = new GregorianCalendar();
        if (date == null) {
            gc.setTime(new DateTool());
        } else {
            gc.setTime(date);
        }
        gc.add(2, 1);
        gc.add(5, -date.getDate());
        DateFormat df = DateFormat.getDateInstance();
        Date dateTemp = gc.getTime();
        return df.format(dateTemp);
    }

    public static String getMonthLastDay(final String s) {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar gc = new GregorianCalendar();
        if (date == null) {
            gc.setTime(new DateTool());
        } else {
            gc.setTime(date);
        }
        gc.add(2, 1);
        gc.add(5, -date.getDate());
        DateFormat df = DateFormat.getDateInstance();
        Date dateTemp = gc.getTime();
        return sFormat.format(dateTemp);
    }

    public static String getAge(final String sBirthDay) {
        if (sBirthDay == null || sBirthDay.equals("")) {
            return "";
        }
        int age = 0;
        Date currentDate = new DateTool().parseDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        Date birthDay = convertLongDate(sBirthDay);
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(1);
        int monthNow = cal.get(2);
        int dayOfMonthNow = cal.get(5);
        cal.setTime(birthDay);
        int yearBirth = cal.get(1);
        int monthBirth = cal.get(2);
        int dayOfMonthBirth = cal.get(5);
        age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    --age;
                }
            } else {
                --age;
            }
        }
        if (age == 0) {
            return "\u5a74\u513f";
        }
        return String.valueOf(age);
    }

    public static String getTodayDate() {
        return formatDate(new DateTool().parseTime(), 4);
    }

    public static void main(final String[] args) {
        getDayByLong(82800000L);
    }
}
