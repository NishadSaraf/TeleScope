package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spoorthi Chandra.K on 12/6/2016.
 */

public class TelescopeDatabaseAdapter {

    static final String DATABASE_NAME = "telescope.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;

    // SQL Statement to create a new tableS
    static final String DATABASE_CREATE =
            "CREATE TABLE `GROUPS` ( `ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `GROUP_NAME` TEXT NOT NULL UNIQUE );  ";
    static final String DATABASE_TAG_CREATE =
            "CREATE TABLE `TAGS` ( `TAG_ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `TAG_NAME` TEXT NOT NULL, `MAC` TEXT NOT NULL UNIQUE, `GROUP_ID` INTEGER NOT NULL, `LAST_ACTIVE` TEXT, FOREIGN KEY(`GROUP_ID`) REFERENCES `GROUPS`(`ID`) ); ";


    private final String TAGS_TABLE="TAGS";
    private final String GROUPS_TABLE="GROUPS";

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
        db.setForeignKeyConstraintsEnabled(true);
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

    /***
     * Inserts a new tag into the TAGS table
     * @param inserttag the tag to be inserted
     */
    public void insertNewTag(BLETag inserttag)
    {
        ContentValues newValues = new ContentValues();
        long groupId = getGroupId(inserttag.getmGroup());

        //Inserts new group if missing
        if (groupId== -1)
        {
            groupId= insertGroup(inserttag.getmGroup());
        }

        // Assigning values for each row.
        newValues.put("TAG_NAME",inserttag.getmDeviceName());
        newValues.put("MAC",inserttag.getmMACAddress());
        newValues.put("GROUP_ID",groupId);
        newValues.put("LAST_ACTIVE",inserttag.getmLastActive());

        // Insert the row into your table
        db.insertOrThrow(""+TAGS_TABLE+"", null, newValues);
    }

    public long insertGroup(String groupname)
    {
        ContentValues newValues = new ContentValues();

        // Assigning values for each row.
        newValues.put("GROUP_NAME",groupname);

        // Insert the row into your table
        long i = db.insert(""+GROUPS_TABLE+"", null, newValues);

        return i;
    }
    /***
     * Returns the groupID corresponsing to a group name
     * @param groupName group name
     * @return -1 if group name is missing in table
     */
    private long getGroupId(String groupName)
    {
        Cursor cursor=db.query(""+GROUPS_TABLE+"", null, " GROUP_NAME=?", new String[]{groupName}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return -1;
        }
        cursor.moveToFirst();
        long groupId= cursor.getInt(cursor.getColumnIndex("ID"));
        cursor.close();
        return groupId;
    }

    /***
     * Deletes a tag
     * @param macAddress MAC address of the tag to be deleted
     * @return number of tags deleted
     */
    public int deleteTag(String macAddress)
    {
        String where="MAC=?";
        int numberOfEntriesDeleted= db.delete(""+TAGS_TABLE+"", where, new String[]{macAddress}) ;
        return numberOfEntriesDeleted;
    }


    public String getSingleEntry(String macAddress)
    {
        Cursor cursor=db.query(""+TAGS_TABLE+"", null, " MAC=?", new String[]{macAddress}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));
        cursor.close();
        return password;
    }

    public ArrayList<BLETag> getAllTags() {
        ArrayList<BLETag> tagsList = new ArrayList<BLETag>();    //stores list of tags


        Cursor cursor = db.rawQuery("SELECT * FROM " + TAGS_TABLE + " JOIN GROUPS ON GROUP_ID= ID;", null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String tagName = cursor.getString(cursor.getColumnIndex("TAG_NAME"));
                String macAddress = cursor.getString(cursor.getColumnIndex("MAC"));
                String groupName = cursor.getString(cursor.getColumnIndex("GROUP_NAME"));
                String lastActive = cursor.getString(cursor.getColumnIndex("LAST_ACTIVE"));

                BLETag fetchedTag = new BLETag();
                fetchedTag.setmDeviceName(tagName);
                fetchedTag.setmMACAddress(macAddress);
                fetchedTag.setmGroup(groupName);
                fetchedTag.setmLastActive(lastActive);

                tagsList.add(fetchedTag);
                cursor.moveToNext();
            }

        }
        return tagsList;
    }


    public ArrayList<String> getAllGroups() {

        ArrayList<String> groupList = new ArrayList<String>();    //stores list of groups
        Cursor cursor = db.rawQuery("SELECT * FROM " + GROUPS_TABLE + " ;", null);

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String groupName = cursor.getString(cursor.getColumnIndex("GROUP_NAME"));
                groupList.add(groupName);
                cursor.moveToNext();
            }

        }
        return groupList;
    }

}
