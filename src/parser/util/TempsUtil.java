package parser.util;

public class TempsUtil {

    public static int strToQuardHeure(String strHeure) {
        double heureDouble = Double.parseDouble(strHeure);
        int heureInt = (int) heureDouble;
        double minDouble = heureDouble - heureInt;
        return (int) (heureInt * 4 + minDouble * 4 /* = 60/15 */);
    }
}
