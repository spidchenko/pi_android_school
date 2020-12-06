package com.spidchenko.week2task.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.db.models.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseHandler {

    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper databaseHelper;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "com_spidchenko_week2task.db";
    private static final String KEY_ID = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_SEARCH_STRING = "search_string";

    private static final String TABLE_USERS = "users";
    private static final String KEY_USER_LOGIN = "login";

    private static final String TABLE_SEARCH_REQUESTS = "searches";
    private static final String KEY_SEARCH_REQUEST_DATE = "date";

    private static final String TABLE_FAVORITES = "favourites";
    private static final String KEY_FAVOURITE_TITLE = "title";
    private static final String KEY_FAVORITE_URL = "url";


    public static synchronized DatabaseHelper getInstance(Context context) {

        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context.getApplicationContext());
            Log.d(TAG, "getInstance: DBHelper");
        }
        return databaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsers = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_LOGIN + " TEXT UNIQUE"
                + ");";
        db.execSQL(createUsers);

        String createFavorites = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " INTEGER,"
                + KEY_SEARCH_STRING + " TEXT," + KEY_FAVOURITE_TITLE + " TEXT,"
                + KEY_FAVORITE_URL + " TEXT UNIQUE);";
        db.execSQL(createFavorites);

        String createSearchRequests = "CREATE TABLE " + TABLE_SEARCH_REQUESTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " INTEGER,"
                + KEY_SEARCH_STRING + " TEXT," + KEY_SEARCH_REQUEST_DATE + " TEXT); ";
        db.execSQL(createSearchRequests);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        StringBuilder dropTablesQueryBuilder = new StringBuilder();
        dropTablesQueryBuilder.append("DROP TABLE IF EXISTS " + TABLE_USERS + ";");
        dropTablesQueryBuilder.append("DROP TABLE IF EXISTS " + TABLE_FAVORITES + ";");
        dropTablesQueryBuilder.append("DROP TABLE IF EXISTS " + TABLE_SEARCH_REQUESTS + ";");
        db.execSQL(dropTablesQueryBuilder.toString());
        onCreate(db);
    }

    @Override
    public void addUser(User user) {
        Log.d(TAG, "addUser: " + user);
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USER_LOGIN, user.getLogin());
            db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }


    @Override
    public User getUser(String login) {
        Log.d(TAG, "getUser: " + login);
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                            KEY_USER_LOGIN}, KEY_USER_LOGIN + "=?",
                    new String[]{String.valueOf(login)}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
                    cursor.close();
                    db.close();
                    return user;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    //Working with favorites
    @Override
    public void addFavorite(Favourite favourite) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USER, favourite.getUser());
            values.put(KEY_SEARCH_STRING, favourite.getSearchRequest());
            values.put(KEY_FAVOURITE_TITLE, favourite.getTitle());
            values.put(KEY_FAVORITE_URL, favourite.getUrl());
            db.insert(TABLE_FAVORITES, null, values);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public Favourite getFavourite(int id) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                            KEY_USER_LOGIN}, KEY_USER_LOGIN + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Favourite favorite = new Favourite(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    cursor.close();
                    db.close();
                    return favorite;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    @Override
    public Favourite getFavourite(int user, String url) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_USER
                    + " = ?" + " AND " + KEY_FAVORITE_URL + " = ?", new String[]{Integer.toString(user), url});
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Favourite favourite = new Favourite(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4));
                    cursor.close();
                    db.close();
                    return favourite;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    @Override
    public ArrayList<Favourite> getAllFavourites(int user, String searchRequest) {
        ArrayList<Favourite> favoritesList = new ArrayList<Favourite>();
        String selectQuery = null;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Cursor cursor = null;
            if (searchRequest == null) {
                Log.d(TAG, "getAllFavourites: searchRequest = null");
                selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_USER
                        + " = ? ORDER BY " + KEY_SEARCH_STRING + " ASC";
                Log.d(TAG, "getAllFavourites: query: " + selectQuery);
                cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user)});

            } else {
                selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_USER
                        + " = ?" + " AND " + KEY_SEARCH_STRING + "= ? ORDER BY " + KEY_SEARCH_STRING + " ASC";
                cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user), searchRequest});
            }

            String cunningSearchString = ""; //for FavoritesAdapter to have different cards

            if (cursor.moveToFirst()) {
                do {
                    String searchReq = cursor.getString(2);
                    if (!cunningSearchString.equals(searchReq)) {
                        cunningSearchString = searchReq;
                        favoritesList.add(new Favourite(user, cunningSearchString, "", ""));
                    }
                    Favourite favourite = new Favourite();
                    favourite.setId(Integer.parseInt(cursor.getString(0)));
                    favourite.setUser(Integer.parseInt(cursor.getString(1)));
                    favourite.setSearchRequest(cursor.getString(2));
                    favourite.setTitle(cursor.getString(3));
                    favourite.setUrl(cursor.getString(4));
                    favoritesList.add(favourite);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }

        return favoritesList;
    }

    @Override
    public int updateFavourite(Favourite favourite) {
        SQLiteDatabase db = null;
        int result = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SEARCH_STRING, favourite.getSearchRequest());
            result = db.update(TABLE_FAVORITES, values, KEY_FAVORITE_URL + " = ?",
                    new String[]{String.valueOf(favourite.getUrl())});
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return result;
    }

    public void deleteFavourite(Favourite favourite) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            if (!favourite.getUrl().equals("")) {
                db.delete(TABLE_FAVORITES, KEY_FAVORITE_URL + " = ?", new String[]{String.valueOf(favourite.getUrl())});
            } else {
                db.delete(TABLE_FAVORITES, KEY_SEARCH_STRING + " = ?", new String[]{String.valueOf(favourite.getSearchRequest())});
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }

    //Working with search requests
    @Override
    public void addSearchRequest(SearchRequest searchRequest) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USER, searchRequest.getUser());
            values.put(KEY_SEARCH_STRING, searchRequest.getSearchRequest());
            values.put(KEY_SEARCH_REQUEST_DATE, System.currentTimeMillis());
            db.insert(TABLE_SEARCH_REQUESTS, null, values);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public SearchRequest getLastSearchRequest(int user) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_SEARCH_REQUESTS + " WHERE " + KEY_USER
                    + "= ?" + " ORDER BY " + KEY_SEARCH_REQUEST_DATE + " DESC LIMIT 1";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user)});
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    SearchRequest searchRequest = new SearchRequest(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                            Long.parseLong(cursor.getString(3)));
                    cursor.close();
                    db.close();
                    return searchRequest;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return new SearchRequest(0, user, "", 0);
    }

    @Override
    public ArrayList<SearchRequest> getAllSearchRequests(int user) {
        ArrayList<SearchRequest> searchRequestsList = new ArrayList<SearchRequest>();
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_SEARCH_REQUESTS + " WHERE " + KEY_USER
                    + "= ?" + " ORDER BY " + KEY_SEARCH_REQUEST_DATE + " DESC LIMIT 20";

            Cursor cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user)});

            if (cursor.moveToFirst()) {
                do {
                    SearchRequest searchRequest = new SearchRequest();
                    searchRequest.setId(Integer.parseInt(cursor.getString(0)));
                    searchRequest.setUser(Integer.parseInt(cursor.getString(1)));
                    searchRequest.setSearchRequest(cursor.getString(2));
                    searchRequest.setDate(Long.parseLong(cursor.getString(3)));
                    searchRequestsList.add(searchRequest);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return searchRequestsList;
    }

    @Override
    public void deleteSearchRequest(SearchRequest searchRequest) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_SEARCH_REQUESTS, KEY_ID + " = ?", new String[]{String.valueOf(searchRequest.getId())});
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }
}