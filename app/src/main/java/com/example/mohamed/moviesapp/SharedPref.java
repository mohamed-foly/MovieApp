package com.example.mohamed.moviesapp;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPref {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Boolean asc;
    public SharedPref(Context context){
        this.context = context;
        pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
    }

    public Boolean getAsc(){
        asc = pref.getBoolean("asc", true);
        return asc;
    }

    public void setAsc(Boolean asc){
        this.asc = asc;
        editor = pref.edit();
        editor.putBoolean("asc", asc); // Storing integer
        editor.apply();
    }
}
