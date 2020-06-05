package umn.ac.id.mahasiswaque;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("mahasiswaque", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setNightModeState (Boolean state){
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightModeState (){
        Boolean state = prefs.getBoolean("NightMode", false);
        return state;
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }
}
