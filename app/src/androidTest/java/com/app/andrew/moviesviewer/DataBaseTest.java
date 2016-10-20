package com.app.andrew.moviesviewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.app.andrew.moviesviewer.DataBase.DataBaseContract;
import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Created by andrew on 10/20/16.
 */

@RunWith(AndroidJUnit4.class)
public class DataBaseTest {
    Context appContext = InstrumentationRegistry.getTargetContext();
    DataBaseHelper helper = new DataBaseHelper(appContext);
    @Test
    public void testInsert() throws Exception {

        Cursor cursor = helper.getReadableDatabase().query("movie", null, null, null, null, null, null);
        if(cursor.moveToFirst())
            return;// only run if no such available
        ContentValues values = new ContentValues();
        values.put("_id", 326);
        values.put("url", "abc");
        values.put("title", "suicide squad");
        values.put("rating", 3.5);
        values.put("date", "5 -7");
        values.put("overview", "very good");
        long id = helper.getWritableDatabase().insert("movie", null, values);
        assertNotEquals(id, -1);

    }

    @Test
    public void gettest() throws Exception {
        Cursor cursor = helper.getReadableDatabase().query("movie", null, null, null, null, null, null);
        while(cursor.moveToNext()){
            assertEquals(cursor.getInt(0), 326);
            assertEquals(cursor.getString(1), "suicide squad");
            assertEquals(cursor.getString(5), "very good");
        }
    }

    @Test
    public void addReview() throws Exception{
        ContentValues values = new ContentValues();
        values.put("comment", "not interesting");
        values.put("author", "me :D");
        values.put("ref", 326);
        long id = helper.getWritableDatabase().insert("review", null, values);

        assertNotEquals(id, -1);
    }

    @Test
    public void addTrailer() throws Exception{
        ContentValues values = new ContentValues();
        values.put("url", "youtube.com");
        values.put("ref", 326);
        long id = helper.getWritableDatabase().insert("trailers", null, values);

        assertNotEquals(id, -1);
    }

    @Test
    public void deleteAll() throws Exception{
    //    final String SQL_DELETE = "DELETE * FROM movie";
      //  helper.getWritableDatabase().execSQL(SQL_DELETE);
    //    helper.getWritableDatabase().delete("trailers", null, null);
     //   helper.getWritableDatabase().delete("review", null, null);
        helper.getWritableDatabase().delete("movie", null, null);

        Cursor cursor = helper.getReadableDatabase().query("movie", null, null, null, null, null, null);
        assertFalse(cursor.moveToFirst());

        cursor = helper.getReadableDatabase().query("trailers", null, null, null, null, null, null);
        assertFalse(cursor.moveToFirst());

        cursor = helper.getReadableDatabase().query("review", null, null, null, null, null, null);
        assertFalse(cursor.moveToFirst());
    }
}
