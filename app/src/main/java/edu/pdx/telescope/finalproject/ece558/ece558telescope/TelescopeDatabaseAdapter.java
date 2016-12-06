package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Spoorthi Chandra.K on 12/6/2016.
 */

public class TelescopeDatabaseAdapter {

    static final String DATABASE_NAME = "telescope.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;

    // SQL Statement to create a new tableS
    static final String DATABASE_CREATE =
            "create table GROUPS( ID integer primary key autoincrement, GROUP_ text );  " +
            "create table TAGS"+" ( " +
                "ID integer primary key autoincrement, "+
                "TAG_NAME  text, "+
                "MAC_ADDRESS text, "+
                "GROUP_ID text, "+
                "LAST_ACTIVITY text ), "+
                "foreign key GROUP_ID REFERENCES "+"GROUPS(ID) );";

    // reference to the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DatabaseHelper dbHelper;

    /***
     * Constructor for databaseAdapter
     * @param _context
     */
    public  TelescopeDatabaseAdapter(Context _context){
        context = _context;
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public  TelescopeDatabaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }


}
