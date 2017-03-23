package com.gargolin.passgen;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Date;

/**
 * Created by User on 03.03.2017.
 */

public class PasswordHolder {
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private  String password;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    PasswordHolder( Cursor cursor){
                id= cursor.getInt(0);
                password=cursor.getString(1);
                comment=cursor.getString(2);
               date=cursor.getString(3);
            }



}
