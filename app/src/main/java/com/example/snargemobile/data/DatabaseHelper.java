package com.example.snargemobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.snargemobile.models.Artist;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "snarge.db";
    private static final int DATABASE_VERSION = 1;

    // Artist Table
    private static final String TABLE_ARTIST = "artists";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_GENRE = "genre";
    private static final String COLUMN_TRACK = "track";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ARTIST_TABLE = "CREATE TABLE " + TABLE_ARTIST + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_GENRE + " TEXT, "
                + COLUMN_TRACK + " TEXT" + ")";
        db.execSQL(CREATE_ARTIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST);
        onCreate(db);
    }

    // Create Artist
    public long addArtist(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, artist.getName());
        values.put(COLUMN_GENRE, artist.getGenre());
        values.put(COLUMN_TRACK, artist.getTrack());

        long id = db.insert(TABLE_ARTIST, null, values);
        db.close();
        return id;
    }

    // Read Artist
    public Artist getArtist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ARTIST,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_GENRE, COLUMN_TRACK},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Artist artist = new Artist(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRACK))
        );
        cursor.close();
        return artist;
    }

    // Update Artist
    public int updateArtist(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, artist.getName());
        values.put(COLUMN_GENRE, artist.getGenre());
        values.put(COLUMN_TRACK, artist.getTrack());

        int result = db.update(TABLE_ARTIST, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(artist.getId())});
        db.close();
        return result;
    }

    // Delete Artist
    public void deleteArtist(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTIST, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Get All Artists
    public List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ARTIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Artist artist = new Artist(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRACK))
                );
                artistList.add(artist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return artistList;
    }
}
