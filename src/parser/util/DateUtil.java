package parser.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

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
