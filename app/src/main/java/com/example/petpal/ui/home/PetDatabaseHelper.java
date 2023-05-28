package com.example.petpal.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PetDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PetDatabase";
    private static final String TABLE_PETS = "pets";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_DESCRIPTION = "description";

    public PetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PETS_TABLE = "CREATE TABLE " + TABLE_PETS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_AGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PETS);
        // Create tables again
        onCreate(db);
    }

    // Add a new pet to the database
    public void addPet(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, pet.getName());
        values.put(KEY_AGE, pet.getAge());
        values.put(KEY_DESCRIPTION, pet.getDescription());
        db.insert(TABLE_PETS, null, values);
        db.close();
    }

    // Get all pets from the database
    public List<Pet> getAllPets() {
        List<Pet> petList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PETS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int nameIndex = cursor.getColumnIndex(KEY_NAME);
        int ageIndex = cursor.getColumnIndex(KEY_AGE);
        int descIndex = cursor.getColumnIndex(KEY_DESCRIPTION);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String age = cursor.getString(ageIndex);
            String description = cursor.getString(descIndex);
            Pet pet = new Pet(name, age, description);
            petList.add(pet);
        }

        cursor.close();
        db.close();

        return petList;
    }



    // Delete a pet from the database
// Delete a pet from the database
    public void deletePet(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PETS, KEY_NAME + " = ?", new String[]{name});
        db.close();
        if (rowsAffected > 0) {
            // Pet deleted successfully
            Log.d("PetDatabaseHelper", "Pet deleted successfully");
        } else {
            // Pet deletion failed
            Log.d("PetDatabaseHelper", "Pet deletion failed");
        }
    }


}
