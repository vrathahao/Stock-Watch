package com.vilasayrathahao.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;
    private MainActivity mainAct;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //mainAct = context;
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    public ArrayList<String[]> loadStocks(){    //get stock
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, COMPANY},
                null,
                null,
                null,
                null,
                null);

        if(cursor != null){
            cursor.moveToFirst();

            for(int i=0;i<cursor.getCount();i++){
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                //add stocks to arraylist
                stocks.add(new String[]{symbol,company});

                //move cursor to next row
                cursor.moveToNext();
            }
            //close cursor after gone thru each row
            cursor.close();
        }
        return stocks;
    }

    public void addStock(Stock stock){
        // Create a contentVal object that store the information of the stock in key-value form and can be used in the insert command of the database
        ContentValues values = new ContentValues();

        //put into contentVal the column value
        values.put(SYMBOL, stock.getStockSymbol());
        values.put(COMPANY, stock.getCompanyName());

        database.insert(TABLE_NAME, null, values);
    }

    public void deleteStock(String symbol){
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[] { symbol });
    }

    public void dumpDbToLog(){
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            //Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                //Log.d(TAG, "dumpDbToLog: " +
                        //String.format("%s %-18s", SYMBOL + ":", symbol) +
                        //String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }

    }


}
