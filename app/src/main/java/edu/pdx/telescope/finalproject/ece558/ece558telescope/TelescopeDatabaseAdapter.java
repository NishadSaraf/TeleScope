package edu.pdx.telescope.finalproject.ece558.ece558telescope;

/**
 * Created by Spoorthi Chandra.K on 12/6/2016.
 */

public class TelescopeDatabaseAdapter {

    static final String DATABASE_NAME = "login.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;

    // SQL Statement to create a new table.
    static final String DATABASE_CREATE = "create table "+"LOGIN"+
            " ( " +
            "ID integer primary key autoincrement, "+
            "USERNAME  text,"+
            "PASSWORD text, "+
            "LONGITUDE text, "+
            "LATITUDE text ); ";
}
