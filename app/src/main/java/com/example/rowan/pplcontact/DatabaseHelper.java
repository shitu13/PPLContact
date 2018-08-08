package com.example.rowan.pplcontact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "Phonebook.db";
    public static final String TABLE_NAME = "Contact_list";
    public static final String UserTable="UserProfile";
    public static final String COL_1 = "Id";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Designation";
    public static final String COL_4 = "Branch";
    public static final String COL_5 = "Phone";
    public static final String COL_6 =  "Mobile";
    public static final String COL_7 = "Email";
    public static final String COL_8 = "Fb_id";
    public static final String COL_9 = "Image";
    public static final String COL_10 = "EmpId";
    public static final String COL_11 ="Whtsapp";
    public static final String COL_12 ="Viber";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Name TEXT,Designation TEXT,Branch TEXT,Phone INTEGER,Mobile INTEGER,Email TEXT," +
                    "Fb_id TEXT,Image TEXT,EmpId TEXT,Whtsapp INTEGER,Viber INTEGER)");
            db.execSQL("CREATE TABLE " + UserTable + " (Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Name TEXT,Designation TEXT,Branch TEXT,Phone INTEGER,Mobile INTEGER,Email TEXT," +
                    "Fb_id TEXT,Image TEXT,EmpId TEXT,Whtsapp INTEGER,Viber INTEGER)");
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.toString());
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP  TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP  TABLE IF EXISTS " + UserTable);
        onCreate(db);

    }

    //insert data
    public boolean InsertData(DataHolder data) {
        long result=-1;

        try {

            db = this.getReadableDatabase();
            String empid=data.getEmpId();
            Cursor cursor = db.rawQuery("SELECT Id FROM " +
                    TABLE_NAME + " WHERE EmpId= '" + empid+"'", null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String Id = cursor.getString(cursor.getColumnIndex("Id"));

                db = this.getWritableDatabase();
                ContentValues NewData = new ContentValues();

                NewData.put(COL_2, data.getName());
                NewData.put(COL_3, data.getDesignation());
                NewData.put(COL_4, data.getBranch());
                NewData.put(COL_5, data.getPhone());
                NewData.put(COL_6, data.getMobile());
                NewData.put(COL_7, data.getEmail());
                NewData.put(COL_8, data.getFbId());
                NewData.put(COL_9, data.getImage());
                NewData.put(COL_11, data.getWhtsapp());
                NewData.put(COL_12, data.getViber());
               result= db.update(TABLE_NAME, NewData, "Id=" + Id, null);



            } else {
                db = this.getWritableDatabase();
                ContentValues NewData = new ContentValues();

                NewData.put(COL_2, data.getName());
                NewData.put(COL_3, data.getDesignation());
                NewData.put(COL_4, data.getBranch());
                NewData.put(COL_5, data.getPhone());
                NewData.put(COL_6, data.getMobile());
                NewData.put(COL_7, data.getEmail());
                NewData.put(COL_8, data.getFbId());
                NewData.put(COL_9, data.getImage());
                NewData.put(COL_10, data.getEmpId());
                NewData.put(COL_11, data.getWhtsapp());
                NewData.put(COL_12, data.getViber());

                 result = db.insert(TABLE_NAME, null, NewData);

                if (result == -1) {
                    return false;
                }
                return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        }
        return true;
    }



    //Retrieve Data
    public List<DataHolder> RetrieveData(boolean user) {
        List<DataHolder> Data = new ArrayList<>();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        while (cursor.moveToNext()) {
            try {


                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String designation = cursor.getString(2);
                String branch = cursor.getString(3);
                String phone = cursor.getString(4);
                String mobile= cursor.getString(5);
                String mail = cursor.getString(6);
                String fb = cursor.getString(7);
                String image = cursor.getString(8);
                String empid=cursor.getString(9);
                String whtsapp=cursor.getString(10);
                String viber=cursor.getString(11);

                DataHolder newData = new DataHolder(image,id, name, phone, mail, fb,
                        mobile, designation,branch,empid,whtsapp,viber);

                Log.d(TAG, "RetrieveData: "+branch);
                if(user && branch.equals("Head Office"))
                {
                    Data.add(newData);
                }
                else
                if(!user && !branch.equals("Head Office"))
                {
                    Data.add(newData);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(Data,new CustomComparator());
        return Data;

    }

    //queryData
    public String getCallerName(String phone)
    {
        db = this.getReadableDatabase();
        String Caller="Unknown Number";

        try{
            Log.d(TAG, "getCallerName: "+phone);

            Cursor cursor =db.rawQuery("SELECT Name FROM " +
                    TABLE_NAME+" WHERE Phone= "+phone,null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                Caller=cursor.getString(cursor.getColumnIndex("Name"));

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }



        return Caller;
    }

    public DataHolder getUserProfile()
    {
        List<DataHolder> Data = new ArrayList<>();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable, null);

        while (cursor.moveToNext()) {
            try {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String designation = cursor.getString(2);
                String branch = cursor.getString(3);
                String phone = cursor.getString(4);
                String mobile= cursor.getString(5);
                String mail = cursor.getString(6);
                String fb = cursor.getString(7);
                String image = cursor.getString(8);
                String empid=cursor.getString(9);
                String whtsapp=cursor.getString(10);
                String viber=cursor.getString(11);

                DataHolder newData = new DataHolder(image,id, name, phone, mail, fb,
                        mobile, designation,branch,empid,whtsapp,viber);

                Data.add(newData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        return Data.get(0);
    }

    public String getUserId()
    {
        db = this.getReadableDatabase();
        String Id="NotFound";

        try{

            Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable, null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
               Id=cursor.getString(cursor.getColumnIndex("EmpId"));

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }



        return Id;
    }


    public boolean InsertUserData(DataHolder data) {
        long result=-1;

        try {


                db = this.getWritableDatabase();
                ContentValues NewData = new ContentValues();

                NewData.put(COL_2, data.getName());
                NewData.put(COL_3, data.getDesignation());
                NewData.put(COL_4, data.getBranch());
                NewData.put(COL_5, data.getPhone());
                NewData.put(COL_6, data.getMobile());
                NewData.put(COL_7, data.getEmail());
                NewData.put(COL_8, data.getFbId());
                NewData.put(COL_9, data.getImage());
                NewData.put(COL_10, data.getEmpId());
                NewData.put(COL_11, data.getWhtsapp());
                NewData.put(COL_12, data.getViber());

                result = db.insert(UserTable, null, NewData);

                if (result == -1) {
                    return false;
                }
                return true;


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        }
        return true;
    }
    public boolean UpdateUserProfile(HashMap<String,String> data)
    {
        long result=-1;

        try {
            String empid=data.get("EmpId");
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT Id FROM " +
                    UserTable + " WHERE EmpId= '" + empid+"'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

               String Id = cursor.getString(cursor.getColumnIndex("Id"));
                Log.d(TAG, "UpdateUserProfile: "+Id);
                db = this.getWritableDatabase();
                ContentValues NewData = new ContentValues();

                if(data.containsKey("Name"))
                NewData.put(COL_2, data.get("Name"));
                if(data.containsKey("Designation"))
                NewData.put(COL_3, data.get("Designation"));
                if(data.containsKey("Branch"))
                NewData.put(COL_4, data.get("Branch"));
                if(data.containsKey("Phone"))
                NewData.put(COL_5,data.get("Phone"));
                if(data.containsKey("Mobile"))
                NewData.put(COL_6, data.get("Mobile"));
                if(data.containsKey("Email"))
                NewData.put(COL_7, data.get("Email"));
                if(data.containsKey("FbId"))
                NewData.put(COL_8, data.get("FbId"));
                if(data.containsKey("Image"))
                NewData.put(COL_9, data.get("Image"));
                if(data.containsKey("Whtsapp"))
                    NewData.put(COL_11, data.get("Whtsap"));
                if(data.containsKey("Viber"))
                    NewData.put(COL_12, data.get("Viber"));
                result = db.update(UserTable, NewData, "Id=1", null);
                Log.d(TAG, "UpdateUserProfile: "+result);


            }
        }catch (Exception e)
        {

        }
        if (result == -1) {
            return false;
        }
        return true;
    }
}
