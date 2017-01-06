package store.singto.singtostore.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Singto on 1/6/2017 AD.
 */

public class SaveEmailPassword {
    private Context context;
    public SaveEmailPassword(){}
    public SaveEmailPassword(Context context){
        this.context = context;
    }

    public void save(String email, String password){
        SharedPreferences sp = context.getSharedPreferences("EandP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();

    }

    public Map<String, String> read(){
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = context.getSharedPreferences("EandP", Context.MODE_PRIVATE);
        data.put("email", sp.getString("email", ""));
        data.put("password", sp.getString("password",""));
        return data;
    }
}
