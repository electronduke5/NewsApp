package com.example.newsapp;

import android.app.Presentation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "newsApp.db";

    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Создание таблицы с пользователями
        db.execSQL(NewsDBContract.User.SQL_CREATE_TABLE);
        //Создание таблицы с новостями
        db.execSQL(NewsDBContract.News.SQL_CREATE_TABLE);
        //Добавление админа
        db.execSQL(NewsDBContract.User.SQL_CREATE_ADMIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Boolean insertUser(String lastName, String name, int isAdmin, String login, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NewsDBContract.User.COLUMN_LastName, lastName);
        contentValues.put(NewsDBContract.User.COLUMN_Name, name);
        contentValues.put(NewsDBContract.User.COLUMN_IsAdmin, isAdmin);
        contentValues.put(NewsDBContract.User.COLUMN_Login, login);
        contentValues.put(NewsDBContract.User.COLUMN_Password, password);

        long result = db.insert(NewsDBContract.User.TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Boolean insertNews(String title, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NewsDBContract.News.COLUMN_TITLE, title);
        contentValues.put(NewsDBContract.News.COLUMN_TEXT, text);

        long result = db.insert(NewsDBContract.News.TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Boolean update(int id, String title, String text) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsDBContract.News.COLUMN_TITLE, title);
        contentValues.put(NewsDBContract.News.COLUMN_TEXT, text);

        long result = db.update(NewsDBContract.News.TABLE_NAME, contentValues, NewsDBContract.News._ID + " = " + id + ";", null);
        return result != -1;
    }

    public void removeAt(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NewsDBContract.News.TABLE_NAME, NewsDBContract.News._ID + "=" + id, null);
    }

    public Cursor getNewsData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + NewsDBContract.News.TABLE_NAME, null);
    }

    public Cursor getUserData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + NewsDBContract.User.TABLE_NAME, null);
    }

    /**
     * Проверяет на наличие логина в базе данных
     *
     * @param login - Логин, введенный пользователем при регистрации
     * @return true or false
     */
    public boolean isFreeLogin(String login) {
        Cursor data = getUserData();
        try {
            int loginColumnIndex = data.getColumnIndex(NewsDBContract.User.COLUMN_Login);
            while (data.moveToNext()) {
                if (data.getString(loginColumnIndex).equals(login)) {
                    return false;
                }
            }
        } finally {
            data.close();
        }
        return true;
    }

    /**
     * @param login    Логин при авторизации
     * @param password Пароль при авторизации
     * @return 0 - Ошибка при проверке
     * 1 - Неверный логин,
     * 2 - Неверный пароль,
     * 3 - Данные верны
     */
    public int isUserExist(String login, String password) {
        int result = Authorization.ERROR_READ;
        try (Cursor data = getUserData()) {
            int loginColumnIndex = data.getColumnIndex(NewsDBContract.User.COLUMN_Login);
            int passwordColumnIndex = data.getColumnIndex(NewsDBContract.User.COLUMN_Password);
            int isAdminColumnIndex = data.getColumnIndex(NewsDBContract.User.COLUMN_IsAdmin);

            while (data.moveToNext()) {
                Log.d("User", data.getString(loginColumnIndex) + " " + data.getString(passwordColumnIndex));
                if (data.getString(loginColumnIndex).equals(login.trim())) {
                    if (data.getString(passwordColumnIndex).equals(password.trim())) {
                        result = data.getInt(isAdminColumnIndex) == 1 ? Authorization._successAdmin : Authorization._successUser;
                        break;
                    } else result =  Authorization._invalidPassword;
                }
                else result = Authorization._invalidLogin;
            }
        }
        return result;
    }
}
