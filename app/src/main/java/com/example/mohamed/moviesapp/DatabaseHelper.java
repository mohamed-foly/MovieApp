package com.example.mohamed.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohamed on 8/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String db_name ="movie_app";
    private static final String table_name ="favorites";
    //public static final String table_name ="exp";
    public DatabaseHelper(Context context) {
        super(context, db_name, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table "+ table_name +" (id integer primary key ,originalTitle Text,posterPath Text,overview Text,voteAverage Real,releaseDate Text) ");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean insert (int id,String originalTitle,String posterPath,String overview,double voteAverage,String releaseDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put("id",id);
        contentValues.put("originalTitle",originalTitle);
        contentValues.put("posterPath",posterPath);
        contentValues.put("overview",overview);
        contentValues.put("voteAverage",voteAverage);
        contentValues.put("releaseDate",releaseDate);

        long res = db.insert(table_name,null,contentValues);
        if (res == -1 ) {return false ;}
        else {return true;}
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " +table_name,null);
    }

    public Cursor deleteById(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("delete from " +table_name +" where id = "+id ,null);
    }
    public boolean isFavorite(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " +table_name +" where id = "+id ,null);
        if (cursor.getCount() > 0 ){
            return true;
        }else {
            return false;
        }
    }
}

