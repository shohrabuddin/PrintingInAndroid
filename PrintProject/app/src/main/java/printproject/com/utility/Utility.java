package printproject.com.utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by shohrab.uddin on 29.12.2015.
 */
public class Utility {

    public static String doubleFormatter(double number){
        Locale locale = new Locale("en");
        Locale.setDefault(locale);

        NumberFormat formatter = new DecimalFormat("#0.00");

        String formattedNumber=formatter.format(number);

        return formattedNumber;

    }
}
