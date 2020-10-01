package com.example.healthyeats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/*
 * Database helper contains:
 *  1. User table, which stores information about the user's account; username, password, email, body information
 *  2. Date table, which stores the list of foods which the user has eaten throughout a specific date.
 *  3. Food table, which stores information about a specific food including name, brand, and calories.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table for storing user data information
    public static final String DATABASE_NAME = "people.db";
    public static final String TABLE_USER = "people_table";
    //public static final String TABLE_BREAKFAST = "breakfast_table";
    public static final String KEY_ID = "ID";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_USER = "USER";
    public static final String KEY_PASS = "PASSWORD";
    public static final String KEY_AGE = "AGE";
    public static final String KEY_GENDER = "GENDER";
    public static final String KEY_WEIGHT = "WEIGHT";
    public static final String KEY_HEIGHT = "INCHES";
    public static final String KEY_ACTIVITY = "ACTIVITY";
    public static final String KEY_GOAL = "GOAL";

    /*
        TO DO: ADD WEIGHT FEATURES ON USER TABLE
     */
    //public static final String KEY_CURR_WEIGHT = "CURRENT_WEIGHT";
    public static final String KEY_GOAL_WEIGHT = "GOAL_WEIGHT";
    public static final String KEY_CAL_GOAL = "CAL_GOAL";

    // Table for storing user daily data
    public static final String TABLE_DATE = "date_table";
    public static final String KEY_DATE = "DATE";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_LIST = "LIST";
    public static final String KEY_CAL = "CAL";
    public static final String KEY_SUCCESS = "SUCCESS";


    public static final String TABLE_MONTH = "month_table";
    public static final String KEY_GOLD = "GOLD";
    public static final String KEY_SILVER = "SILVER";
    public static final String KEY_BRONZE = "BRONZE";

    // Table for storing information about food items
    public static final String TABLE_FOOD = "food_table";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_BRAND = "BRAND";
    public static final String KEY_CODE = "CODE";
    // item id
    // KEY_CAL

    public static final String TABLE_CUSTOM = "custom_table";
    public static final String KEY_INFO = "ITEM_INFO";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUserTable = "CREATE TABLE " + TABLE_USER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " EMAIL TEXT, USER TEXT, PASSWORD TEXT, AGE INTEGER, GENDER INTEGER, WEIGHT INTEGER, " +
                "INCHES INTEGER, ACTIVITY INTEGER, GOAL INTEGER, GOLD INTEGER, SILVER INTEGER, BRONZE INTEGER)";

        String createDateTable = "CREATE TABLE " + TABLE_DATE + "(" + KEY_USER_ID + " INTEGER,"+ KEY_DATE + " TEXT,"+KEY_LIST
                +" TEXT,"+KEY_SUCCESS+" INTEGER );";

        String createMonthTable = "CREATE TABLE " + TABLE_MONTH + "(GOLD INTEGER, SILVER INTEGER, BRONZE INTEGER)";

        String createFoodTable = "CREATE TABLE " + TABLE_FOOD +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, BRAND TEXT, CODE TEXT, ITEM_ID TEXT, CAL INTEGER)";

        // Not currently in user
        String createCustomTable = "CREATE TABLE " + TABLE_CUSTOM + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " USER_ID TEXT, ITEM_INFO TEXT)";

        db.execSQL(createUserTable);
        db.execSQL(createDateTable);
        db.execSQL(createMonthTable);
        db.execSQL(createCustomTable);
        db.execSQL(createFoodTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_USER + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_DATE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_MONTH + "'");
        //db.execSQL("DROP TABLE IF EXISTS '" + TABLE_FOODLIST + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_FOOD + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_CUSTOM + "'");
        onCreate(db);
    }

    /*
     * 0 - email already in user
     * 1 - registeration failed
     * 2 - registration success
     */
    public int addUser(String username, String email, String password) {
        if(checkEmail(email)) {
            return 0;
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", email);
            contentValues.put("user", username);
            contentValues.put("password", password);
            long result = db.insert(TABLE_USER, null, contentValues);
            db.close();
            if(result == -1) {
                return 1;
            }else{
                return 2;
            }
        }
    }

    public boolean addInfo(String id, int age, int gender, int weight, int inches, int activity, int goals){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_AGE, age);
        contentValues.put(KEY_GENDER, gender);
        contentValues.put(KEY_WEIGHT, weight);
        contentValues.put(KEY_HEIGHT, inches);
        contentValues.put(KEY_ACTIVITY,activity);
        contentValues.put(KEY_GOAL, goals);
        db.update(TABLE_USER, contentValues, "ID = ?", new String[] {id});
        return true;
    }


    public String checkUser(String username, String password) {
        String[] columns = { KEY_ID };
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_USER + "=?" + " and " + KEY_PASS + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USER,columns,selection,selectionArgs,null,null,null);
        String userID = "";
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            userID = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return userID;
    }

    // Returns true when email is already in a row within the table
    public boolean checkEmail(String email) {
        String[] columns = { KEY_ID };
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_EMAIL + "=?";
        String[] selectionArgs = { email };
        Cursor cursor = db.query(TABLE_USER,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        if(count > 0){
            return true;
        }
        return false;

    }

    public Cursor getUserInfo(String userID) {
        String[] columns = { KEY_USER, KEY_AGE, KEY_GENDER, KEY_WEIGHT, KEY_HEIGHT, KEY_ACTIVITY, KEY_GOAL };
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_ID + "=?";
        String[] selectionArgs = { userID };
        Cursor cursor = db.query(TABLE_USER,columns,selection,selectionArgs,null,null,null);
        return cursor;
    }


    public boolean insertFood(String foodID, String name, String brand, String code, int cal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cal", cal);
        contentValues.put("name", name);
        contentValues.put("brand", brand);
        contentValues.put("item_id", foodID);
        contentValues.put("code", code);
        long result = db.insert(TABLE_FOOD, null, contentValues);
        db.close();
        if(result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public Cursor insertmFood(String name, String brand, int cal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cal", cal);
        contentValues.put("name", name);
        contentValues.put("brand", brand);
        long result = db.insert(TABLE_FOOD, null, contentValues);
        db.close();

        String[] columns = { KEY_ID };
        SQLiteDatabase db2 = getReadableDatabase();
        String selection = "NAME=? and BRAND=? and CAL=?";
        String[] selectionArgs = { name, brand, cal + "" };
        Cursor data = db2.query(TABLE_FOOD,columns,selection,selectionArgs,null,null,null);
        return data;

    }

    public Cursor findItem(String foodID) {
        String[] columns = { KEY_NAME, KEY_BRAND, KEY_CAL };
        SQLiteDatabase db = getReadableDatabase();
        String selection = "ITEM_ID=?";
        String[] selectionArgs = { foodID };
        Cursor data = db.query(TABLE_FOOD,columns,selection,selectionArgs,null,null,null);
        return data;
    }

    public Cursor findmItem(String tagID) {
        String[] columns = { KEY_NAME, KEY_BRAND, KEY_CAL };
        SQLiteDatabase db = getReadableDatabase();
        String selection = "ID=?";
        String[] selectionArgs = { tagID };
        Cursor data = db.query(TABLE_FOOD,columns,selection,selectionArgs,null,null,null);
        return data;
    }


    public boolean newDate(String userID, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userID);
        contentValues.put("date", date);
        long result = db.insert(TABLE_DATE, null, contentValues);
        db.close();
        if(result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean updateList(String userID, String date, String list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USER_ID,userID);
        contentValues.put(KEY_DATE,date);
        contentValues.put(KEY_LIST,list);
        db.update(TABLE_DATE, contentValues, "USER_ID = ? and DATE = ?", new String[] {userID, date});
        return true;
    }

    public String getList(String userID, String date) {
        String[] columns = { KEY_LIST };
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_USER_ID + "=?" + " and " + KEY_DATE + "=?";
        String[] selectionArgs = { userID, date };
        Cursor cursor = db.query(TABLE_DATE,columns,selection,selectionArgs,null,null,null);
        String list = "";
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            list = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return list;
    }

    // Returns true when there is already a data table for the date and user
    public boolean checkDateData(String userID, String date){
        String[] columns = { KEY_LIST };
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_USER_ID + "=?" + " and " + KEY_DATE + "=?";
        String[] selectionArgs = { userID, date };
        Cursor cursor = db.query(TABLE_DATE,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        if(count > 0){
            return true;
        }
        return false;

    }

    public void addSuccess(String userID, String date, int success){
        if(checkDateData(userID, date)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_SUCCESS, success);
            db.update(TABLE_DATE, contentValues, "USER_ID = ? and DATE = ?", new String[] {userID, date});
        }
    }

    public String getSuccess(String userID, String date){
        String[] columns = { KEY_SUCCESS };
        SQLiteDatabase db = getReadableDatabase();
        String selection = KEY_USER_ID + "=?" + " and " + KEY_DATE + "=?";
        String[] selectionArgs = { userID, date };
        Cursor cursor = db.query(TABLE_DATE,columns,selection,selectionArgs,null,null,null);
        String list = "";
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            list = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return list;
    }




}