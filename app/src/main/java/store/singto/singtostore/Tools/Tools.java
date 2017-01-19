package store.singto.singtostore.Tools;
import android.icu.text.DateFormat;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Singto on 1/5/2017 AD.
 */

public class Tools {
    public static boolean isEmail(String email){
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }else {
            return true;
        }
    }

    public static String getDateOnly(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return format.format(date);
    }

    public static String getTimeOnly(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return format.format(date);

    }
}
