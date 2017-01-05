package store.singto.singtostore.Tools;

import android.util.Patterns;

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
}
