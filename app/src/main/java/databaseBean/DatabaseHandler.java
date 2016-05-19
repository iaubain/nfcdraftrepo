package databaseBean;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 4/9/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "deviceDB";

    // device table name
    private static final String TABLE_DEVICE = "device";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "deviceName";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DEVICE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new DEVICE
    public long addDevice(MyDevice device) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getDeviceName()); // Device Name

        // Inserting Row
        long insertStatus=db.insert(TABLE_DEVICE, null, values);
        db.close(); // Closing database connection

        return insertStatus;
    }

    // Getting single device
    public MyDevice getDevice(String deviceName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DEVICE, new String[] { KEY_ID,
                        KEY_NAME}, KEY_NAME + "=?",
                new String[] { String.valueOf(deviceName) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MyDevice device = new MyDevice();
        device.setDeviceId(cursor.getString(0).toString());
        device.setDeviceId(cursor.getString(1));
        //(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        // return device
        return device;
    }

    // Getting All device
    public MyDevice getAllDevice() {
        MyDevice device=new MyDevice();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                device.setDeviceId(cursor.getString(0).toString());
                device.setDeviceName(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // return contact list
        return device;
    }

    // Updating single device
    public int updateDevice(MyDevice device) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getDeviceName());

        // updating row
        return db.update(TABLE_DEVICE, values, KEY_NAME + " = ?",
                new String[] { device.getDeviceName() });
    }

    // Deleting single contact
    public void deleteDevice(MyDevice device) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEVICE, KEY_ID + " = ?",
                new String[]{String.valueOf(device.getDeviceId())});
        db.close();
    }


    // Getting device Count
    public int getDeviceCount() {
        int count=0;
        try{
        String countQuery = "SELECT  * FROM " + TABLE_DEVICE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
            count=cursor.getCount();
            db.close();
            // return count
            return count;
        }catch (Exception e){
            if(count==0)
                count+=1;
            return count;
        }
    }

    //Truncate device table
    public int truncate(){
        SQLiteDatabase db = this.getWritableDatabase();;
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE);

        onCreate(db);

        SQLiteDatabase db1= this.getWritableDatabase();
        int truncateStatus= db1.delete(TABLE_DEVICE, null, null);
        return truncateStatus;
    }
}
