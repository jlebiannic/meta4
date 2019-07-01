package parser.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
    public static final Integer INFINITE_DATE = 99999999; // YYYYMMDD : Attention => besoin d'être grand pour la relation d'ordre

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYWW = "yyyyww";
    /** yyyy____ */
    public static final int YEAR_4DIGITS = 10000;
    /** ____MM__ */
    public static final int MOUTH_2DIGITS = 100;
    /** yyyy__ */
    public static final int WEEK_2DIGITS = 100;

    private DateUtil() {
    }

    public static Integer addDay(Integer date, int day) {
        Date startJavaDate;
        try {
            startJavaDate = new SimpleDateFormat(YYYYMMDD).parse(date.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(startJavaDate);
            cal.add(Calendar.DATE, day);
            return dateToInteger(cal);
        } catch (ParseException e) {
            return -1;
        }
    }

    public static Integer dateToInteger(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dateToInteger(cal);
    }

    public static Integer dateToInteger(Date date, Integer defaultIfNull) {
        if (date == null) {
            return defaultIfNull;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dateToInteger(cal);
    }

    public static Integer dateToInteger(Calendar cal) {
        return cal.get(Calendar.YEAR) * YEAR_4DIGITS + (cal.get(Calendar.MONTH) + 1) * MOUTH_2DIGITS + cal.get(Calendar.DATE);
    }

    public static Integer strDateToInteger(String strDate) {
        Integer dateAsInt = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar d = Calendar.getInstance();
            d.setTime(df.parse(strDate));
            dateAsInt = d.get(Calendar.YEAR) * 10000 + (d.get(Calendar.MONTH) + 1) * 100 + d.get(Calendar.DATE);
        } catch (ParseException e) {
            return null;
        }
        return dateAsInt;
    }
}
