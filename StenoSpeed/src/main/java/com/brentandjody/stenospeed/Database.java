package com.brentandjody.stenospeed;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brentn on 22/01/14.
 */
public class Database extends SQLiteOpenHelper {

    static final String DATABASE_NAME="progress";
    static final int DATABASE_VERSION = 1;

    static final String TABLE_RECORDS="records";

    static final String COL_DATE="date";
    static final String COL_DUR="duration";
    static final String COL_WORDS="words";
    static final String COL_SPEED="top_speed";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_RECORDS+" (id INTEGER PRIMARY KEY AUTOINCREMENT, "
        +COL_DATE+" INTEGER, "
        +COL_DUR+" INTEGER, "
        +COL_WORDS+" INTEGER, "
        +COL_SPEED+" INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO:in the future we probably need to preserve this data
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECORDS);
        onCreate(db);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery("SELECT "+ COL_DATE + "," + COL_DUR + "," + COL_WORDS + "," + COL_SPEED
                + " FROM " + TABLE_RECORDS + " ORDER BY " + COL_DATE + ";", null);
        return result;
    }

}
