package in.co.androidadda.wallpapertrending.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.R.attr.version;

/**
 * Created by Anand
 */

public class FavDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WallpaperTrendingFavDatabase.db";
    public static final String TABLE_NAME = "Favorites_Table";
    public static final String COL_1 = "wallUrl";
    public static final String COL_2 = "wallFullUrl";
    public static final String COL_3 = "filetype";
    public static final String COL_4 = "wallId";
    public static final String COL_5 = "isFavorite";

    public FavDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_NAME + "(wallUrl String Not Null, wallFullUrl String Not Null, filetype String,  wallId Int, isFavorite Int)" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addFavToDatabase(String wallUrl, String wallFullUrl, String filetype, int wallId, int isFavorite){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(6);
        contentValues.put(COL_1, wallUrl);
        contentValues.put(COL_2, wallFullUrl);
        contentValues.put(COL_3, filetype);
        contentValues.put(COL_4, wallId);
        contentValues.put(COL_5, isFavorite);
        Log.i("IN DATABSE FAV", String.valueOf(isFavorite));
        Log.i("IN DATABSE small", wallUrl);
        Log.i("IN DATABSE full ", wallFullUrl);
        Log.i("IN DATABSE ID", String.valueOf(wallId));
        Log.i("IN DATABSE type ", filetype);

        long result = database.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        }
        else return true;
    }

    public void deleteEntry(String URL) throws SQLException {
        String[] whereArgs = new String[] { URL };
        this.getReadableDatabase().delete(TABLE_NAME, COL_1 + "=?", whereArgs);
    }

    public void deleteAllFavs() throws SQLException {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("Drop table if exists " + TABLE_NAME);
        sqLiteDatabase.execSQL("create table if not exists " + TABLE_NAME + "(wallUrl String Not Null, wallFullUrl String Not Null, filetype String,  wallId Int, isFavorite Int)");
    }

    public Cursor readFavFromDatabase(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        return cursor;
    }

    public Cursor readLastFromFav(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        int last = cursor.getCount()-1;
        cursor.move(last);
        return cursor;
    }

    public boolean checkExist(String URL)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {COL_2},
                COL_2 + " = ?",
                new String[] {URL},
                null, null, null, null);

        if(cursor.moveToFirst())
            return true; //row exists
        else
            return false;

    }
}
