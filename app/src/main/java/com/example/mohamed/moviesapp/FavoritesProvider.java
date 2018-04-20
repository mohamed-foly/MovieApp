package com.example.mohamed.moviesapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesProvider extends ContentProvider {

    Context context;
    static final String PROVIDER_NAME = "com.example.mohamed.moviesapp.FavoritesProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/movies";
    static final Uri CONTENT_URI = Uri.parse(URL);


    //Columns Define
//id ,originalTitle ,posterPath ,overview ,voteAverage Real,releaseDate
    static final String ID = "id";
    static final String TITLE = "originalTitle";
    static final String POSTER = "posterPath";
    static final String OVERVIEW = "overview";
    static final String VOTE = "voteAverage";
    static final String RELEASE = "releaseDate";

    //-------------//




    DatabaseHelper dbHelper ;


    public FavoritesProvider(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);

    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return dbHelper.getData();
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values != null) {
            dbHelper.insert(values.getAsInteger(ID),
                    values.getAsString(TITLE),
                    values.getAsString(POSTER),
                    values.getAsString(OVERVIEW),
                    values.getAsDouble(VOTE),
                    values.getAsString(RELEASE)
            );
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        dbHelper.deleteById(selectionArgs[0]);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public boolean isFavorite(int ID){
        return dbHelper.isFavorite(ID);
    }
}
