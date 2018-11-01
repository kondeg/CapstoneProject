package edu.udacity.project.dividendpayout.util;

import android.arch.persistence.room.util.StringUtil;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ValidationUtil {

    private final static String LOG_TAG = ValidationUtil.class.getSimpleName();

    public static boolean isEmpty(EditText editText) {
        String input = editText.getText().toString();
        if (input !=null) {
            return input.length() == 0;
        }
        return true;
    }

    public static Date convertTextToDate(EditText dateText) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        format.setLenient(false);
        String input = dateText.getText().toString();
        Date returnDate = null;
        if (input!=null && input.trim().length()>0) {
            returnDate = format.parse(input.trim());
        }
        return returnDate;
    }

    public static boolean isInTheFuture(Date date) {
        if (date!=null) {
            return date.after(new Date());
        }
        return false;
    }

    public static BigDecimal convertCurrencyToDecimal(String currency) {
        if (currency==null || currency.trim().length()==0) {
            return null;
        }
        String price = currency.replace("$","").replace(",","");
        BigDecimal sPrice = new BigDecimal(price);
        return sPrice;
    }

    public static String convertDecimalToCurrency(BigDecimal decimal) {
        Locale locale = Locale.US;
        if (decimal!=null) {
            return NumberFormat.getCurrencyInstance(locale).format(decimal);
        } else {
            return "$0.00";
        }
    }

    public static Integer convertTextToIntenger(EditText integer) {
        String intTxt = integer.getText().toString();
        if (intTxt==null ||intTxt.trim().length()==0) {
            return null;
        } else {
            return Integer.parseInt(intTxt);
        }
    }

    public static boolean checkTicker(String ticker) {








        if (ticker!=null && ticker.length()>=1 && ticker.length()<5) {
            return true;
        } else return false;
    }

}
