package com.example.tier_list_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tier_list_app.model.Item;
import com.example.tier_list_app.model.Tier;
import com.example.tier_list_app.model.TierList;
import com.example.tier_list_app.model.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getName();
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Users.db";

    private static final String TABLE_USER = "Users";
    private static final String TABLE_TIER_LIST = "TierList";

    private static final String TABLE_TIER = "Tier";

    private static final String TABLE_ITEM = "Item";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASS = "password";

    private static final String COL_USER_USERNAME = "user_username";

    private static final String COL_TIER_LIST_NAME = "tier_list_name";

    private static final String COL_TIER_NAME = "tier_name";
    private static final String COL_ITEM_URL = "item_url";
    SQLiteDatabase db;

    private static final String TABLE_CREATE_USER = "CREATE TABLE " + TABLE_USER +
            "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME + " TEXT NOT NULL, " + COL_EMAIL + " TEXT NOT NULL, " +
            COL_USERNAME + " TEXT UNIQUE, " + COL_PASS + " TEXT NOT NULL);";

    private static final String TABLE_CREATE_TIER_LIST = "CREATE TABLE " + TABLE_TIER_LIST +
            "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_USER_USERNAME + " TEXT NOT NULL, " +
            COL_NAME + " TEXT NOT NULL);";

    private static final String TABLE_CREATE_TIER = "CREATE TABLE " + TABLE_TIER +
            "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TIER_LIST_NAME + " TEXT NOT NULL, " +
            COL_NAME + " TEXT NOT NULL);";


    private static final String TABLE_CREATE_ITEM = "CREATE TABLE " + TABLE_ITEM +
            "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TIER_NAME+ " TEXT NOT NULL, " +
            COL_NAME + " TEXT NOT NULL, " +
            COL_ITEM_URL + " TEXT);";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_USER);
        db.execSQL(TABLE_CREATE_TIER);
        db.execSQL(TABLE_CREATE_TIER_LIST);
        db.execSQL(TABLE_CREATE_ITEM);

        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query1 = "DROP TABLE IF EXISTS " + TABLE_USER;
        String query2 = "DROP TABLE IF EXISTS " + TABLE_TIER_LIST;
        String query3 = "DROP TABLE IF EXISTS " + TABLE_ITEM;
        String query4 = "DROP TABLE IF EXISTS " + TABLE_TIER;
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
        this.onCreate(db);
    }

    public void insereUser (User user) {
        db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, user.getName());
            values.put(COL_EMAIL, user.getEmail());
            values.put(COL_USERNAME, user.getUsername());
            values.put(COL_PASS, user.getPassword());
            db.insertOrThrow(TABLE_USER, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add user to database");
        } finally {
            db.endTransaction();
        }
    }

    public String buscarSenha(String usuario) {
        db = this.getReadableDatabase();
        String query = String.format("SELECT %s FROM %s WHERE %s = ?",
                COL_PASS, TABLE_USER, COL_USERNAME);
        String senha = "não encontrado";
        db.beginTransaction();
        try {
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuario)});
            try {
                if (cursor.moveToFirst()) {
                    senha = cursor.getString(0);
                    db.setTransactionSuccessful();
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return senha;
    }



    public User buscarUser(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columnsUser = {COL_ID, COL_NAME, COL_EMAIL, COL_USERNAME, COL_PASS};
        String selectionUser = COL_USERNAME + "=?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query(TABLE_USER, columnsUser, selectionUser, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User u = new User();
            u.setId(cursor.getInt(0));
            u.setUsername(cursor.getString(1));
            u.setEmail(cursor.getString(2));
            u.setPassword(cursor.getString(3));
            ArrayList<TierList> tierLists = buscarTierLists(u);
            u.setTierLists(tierLists);
            return u;
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return user;
    }


    public long atualizarUser(User user) {
        long retornoBD;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_PASS, user.getPassword());
        String[] args = {String.valueOf(user.getId()), "Maria", "teste"};
        retornoBD = db.update(TABLE_USER, values, "id=?", args);
        db.close();
        return retornoBD;
    }

    public void insereTierList(User user, TierList tierList) {
        db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, tierList.getName());
            values.put(COL_USER_USERNAME, user.getUsername());
            db.insertOrThrow(TABLE_TIER_LIST, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add tier list to database");
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<TierList> buscarTierLists(User user) {
        String[] columns = {COL_ID, COL_USER_USERNAME,COL_NAME};
        String selection = COL_USER_USERNAME + " = ?";
        String[] selectionArgs = {user.getUsername()};
        Cursor cursor = getReadableDatabase().query(TABLE_TIER_LIST,
                columns, selection, selectionArgs, null,
                null, null, null);

        ArrayList<TierList> tierLists = new ArrayList<>();
        while (cursor.moveToNext()) {
            TierList tl = new TierList();
            tl.setId(cursor.getInt(0));
            tl.setUsername(cursor.getString(1));
            tl.setName(cursor.getString(2));
            tierLists.add(tl);
        }
        return tierLists;
    }

    public TierList buscarTierList(int idTierList) {
        SQLiteDatabase db = this.getReadableDatabase();
        TierList tierList = null;

        String[] columns = {COL_ID, COL_USER_USERNAME,COL_NAME};
        String selection = COL_ID + "=?";
        String[] selectionArgs = {String.valueOf(idTierList)};
        Cursor cursor = db.query(TABLE_TIER_LIST, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            TierList tl = new TierList();
            tl.setId(cursor.getInt(0));
            tl.setUsername(cursor.getString(1));
            tl.setName(cursor.getString(2));
            return tl;
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return tierList;
    }

    public TierList buscarTierListByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        TierList tierList = null;

        String[] columns = {COL_ID, COL_USER_USERNAME, COL_NAME};
        String selection = COL_NAME + "=?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query(TABLE_TIER_LIST, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            TierList tl = new TierList();
            tl.setId(cursor.getInt(0));
            tl.setUsername(cursor.getString(1));
            tl.setName(cursor.getString(2));
            return tl;
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return tierList;
    }

    public long excluirTierList(TierList tl) {
        long retornoBD;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {String.valueOf(tl.getId())};
        retornoBD = db.delete(TABLE_TIER_LIST, COL_ID + "=?", args);
        return retornoBD;
    }

    public long atualizarTierList(TierList tl) {
        long retornoBD;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, tl.getName());
        String[] args = { String.valueOf(tl.getId()) };
        retornoBD = db.update(TABLE_TIER_LIST, values, COL_ID + "=?", args);
        db.close();
        return retornoBD;
    }



    public void insereTier(TierList tierList, Tier tier) {
        db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, tier.getName());
            values.put(COL_TIER_LIST_NAME, tierList.getName());
            db.insertOrThrow(TABLE_TIER, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add tier to database", e);

        } finally {
            db.endTransaction();
        }
    }

    public List<Tier> buscarTiers(TierList tierList) {
        if (tierList == null) {
            return new ArrayList<>();
        }

        String[] columns = {COL_ID, COL_TIER_LIST_NAME, COL_NAME};
        String selection = COL_TIER_LIST_NAME + " = ?";
        String[] selectionArgs = {tierList.getName()};
        Cursor cursor = getReadableDatabase().query(TABLE_TIER,
                columns, selection, selectionArgs, null,
                null, null, null);

        List<Tier> tiers = new ArrayList<>();
        while (cursor.moveToNext()) {
            Tier tl = new Tier();
            tl.setId(cursor.getInt(0));
            tl.setTierlistName(cursor.getString(1));
            tl.setName(cursor.getString(2));
            tiers.add(tl);
        }

        cursor.close();
        return tiers;
    }


    public long excluirTier(Tier t) {
        long retornoBD;
        db = this.getWritableDatabase();
        String[] args = {String.valueOf(t.getId())};
        retornoBD=db.delete(TABLE_TIER, COL_ID+"=?",args);
        return retornoBD;
    }

    public long atualizarTier(Tier t){
        long retornoBD;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,t.getName());
        values.put(COL_TIER_LIST_NAME, t.getTierlistName());
        String[] args = {String.valueOf(t.getId())};
        retornoBD=db.update(TABLE_TIER,values,"id=?",args);
        db.close();
        return retornoBD;
    }

    public void insereItems(Tier tier, Item item) {
        db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, item.getName());
            values.put(COL_TIER_NAME, tier.getName());
            values.put(COL_ITEM_URL, item.getUrlItem());
            db.insertOrThrow(TABLE_ITEM, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add tier list to database");
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Item> buscarItems(Tier tier) {
        String[] columns = {COL_ID, COL_TIER_NAME,COL_NAME,COL_ITEM_URL };
        String selection = TABLE_ITEM + " = ?";
        String[] selectionArgs = {tier.getName()};
        Cursor cursor = getReadableDatabase().query(TABLE_ITEM,
                columns, selection, selectionArgs, null,
                null, null, null);

        ArrayList<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            Item i = new Item();
            i.setId(cursor.getInt(0));
            i.setTierName(cursor.getString(1));
            i.setName(cursor.getString(2));
            i.setUrlItem(cursor.getString(3));
            items.add(i);
        }
        return items;
    }

    public long excluirItem(Item i) {
        long retornoBD;
        db = this.getWritableDatabase();
        String[] args = {String.valueOf(i.getId())};
        retornoBD=db.delete(TABLE_ITEM, COL_ID+"=?",args);
        return retornoBD;
    }

    public long atualizarItem(Item i){
        long retornoBD;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,i.getName());
        values.put(COL_TIER_NAME, i.getTierName());
        values.put(COL_ITEM_URL, i.getUrlItem());
        String[] args = {String.valueOf(i.getId())};
        retornoBD=db.update(TABLE_ITEM,values,"id=?",args);
        db.close();
        return retornoBD;
    }

    public List<Tier> buscarTiersByTierListName(String tierListName) {

        String[] columns = {COL_ID, COL_TIER_LIST_NAME, COL_NAME};
        String selection = COL_TIER_LIST_NAME + " = ?";
        String[] selectionArgs = {tierListName};
        Cursor cursor = getReadableDatabase().query(TABLE_TIER,
                columns, selection, selectionArgs, null,
                null, null, null);

        List<Tier> tiers = new ArrayList<>();
        while (cursor.moveToNext()) {
            Tier tl = new Tier();
            tl.setId(cursor.getInt(0));
            tl.setTierlistName(cursor.getString(1));
            tl.setName(cursor.getString(2));
            tiers.add(tl);
        }

        cursor.close();
        return tiers;
    }



}
