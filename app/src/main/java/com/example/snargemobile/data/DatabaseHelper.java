package com.example.snargemobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.snargemobile.models.Artist;
import com.example.snargemobile.models.Event;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "snarge.db";
    private static final int DATABASE_VERSION = 2; // Updated to cover schema changes

    // Events Table
    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_EVENT_ID = "id";
    private static final String COLUMN_EVENT_NAME = "name";
    private static final String COLUMN_EVENT_DESCRIPTION = "description";
    private static final String COLUMN_EVENT_DATE = "date";
    private static final String COLUMN_EVENT_PRICE = "price";
    private static final String COLUMN_EVENT_PAYMENT_STATUS = "paymentStatus"; // New column for payment status

    // Artists Table
    private static final String TABLE_ARTISTS = "artists";
    private static final String COLUMN_ARTIST_ID = "id";
    private static final String COLUMN_ARTIST_NAME = "name";
    private static final String COLUMN_ARTIST_GENRE = "genre";
    private static final String COLUMN_ARTIST_TRACK = "track";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Events table
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EVENT_NAME + " TEXT,"
                + COLUMN_EVENT_DESCRIPTION + " TEXT,"
                + COLUMN_EVENT_DATE + " TEXT,"
                + COLUMN_EVENT_PRICE + " REAL,"
                + COLUMN_EVENT_PAYMENT_STATUS + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        // Create Artists table
        String CREATE_ARTISTS_TABLE = "CREATE TABLE " + TABLE_ARTISTS + "("
                + COLUMN_ARTIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ARTIST_NAME + " TEXT, "
                + COLUMN_ARTIST_GENRE + " TEXT, "
                + COLUMN_ARTIST_TRACK + " TEXT" + ")";
        db.execSQL(CREATE_ARTISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Upgrade Events table to include paymentStatus column
            db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COLUMN_EVENT_PAYMENT_STATUS + " INTEGER DEFAULT 0");
        }
        // Handle further upgrades for artists table if needed in future versions
    }

    // Methods for Event Operations
    public long insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, event.getName());
        values.put(COLUMN_EVENT_DESCRIPTION, event.getDescription());
        values.put(COLUMN_EVENT_DATE, event.getDate());
        values.put(COLUMN_EVENT_PRICE, event.getPrice());
        values.put(COLUMN_EVENT_PAYMENT_STATUS, event.isPaymentStatus() ? 1 : 0);

        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EVENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EVENT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EVENT_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EVENT_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EVENT_PRICE))
                );
                events.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return events;
    }

    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, event.getName());
        values.put(COLUMN_EVENT_DESCRIPTION, event.getDescription());
        values.put(COLUMN_EVENT_DATE, event.getDate());
        values.put(COLUMN_EVENT_PRICE, event.getPrice());
        values.put(COLUMN_EVENT_PAYMENT_STATUS, event.isPaymentStatus() ? 1 : 0);

        int rowsUpdated = db.update(TABLE_EVENTS, values, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(event.getId())});
        db.close();
        return rowsUpdated;
    }

    public int deleteEvent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }

    // Methods for Artist Operations
    public long addArtist(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTIST_NAME, artist.getName());
        values.put(COLUMN_ARTIST_GENRE, artist.getGenre());
        values.put(COLUMN_ARTIST_TRACK, artist.getTrack());

        long id = db.insert(TABLE_ARTISTS, null, values);
        db.close();
        return id;
    }

    public Artist getArtist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ARTISTS,
                new String[]{COLUMN_ARTIST_ID, COLUMN_ARTIST_NAME, COLUMN_ARTIST_GENRE, COLUMN_ARTIST_TRACK},
                COLUMN_ARTIST_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        Artist artist = new Artist(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_GENRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_TRACK))
        );
        cursor.close();
        return artist;
    }

    public int updateArtist(Artist artist) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTIST_NAME, artist.getName());
        values.put(COLUMN_ARTIST_GENRE, artist.getGenre());
        values.put(COLUMN_ARTIST_TRACK, artist.getTrack());

        int result = db.update(TABLE_ARTISTS, values, COLUMN_ARTIST_ID + " = ?", new String[]{String.valueOf(artist.getId())});
        db.close();
        return result;
    }

    public void deleteArtist(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARTISTS, COLUMN_ARTIST_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ARTISTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Artist artist = new Artist(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_GENRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST_TRACK))
                );
                artistList.add(artist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return artistList;
    }
}
