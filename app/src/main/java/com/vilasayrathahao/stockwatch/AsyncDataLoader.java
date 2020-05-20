package com.vilasayrathahao.stockwatch;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import androidx.appcompat.app.AlertDialog;

public class AsyncDataLoader extends AsyncTask<Stock, Void, String> {
    private static final String LINK = "https://cloud.iexapis.com/stable/stock/";
    private static final String APIKEY = "pk_c8a53b4769bc4b108b0372798cccdd72";
    private MainActivity mainActivity;
    private Stock stock;
    private static final String TAG = "AsyncDataLoader";

    public AsyncDataLoader(MainActivity ma){
        this.mainActivity = ma;
    }

    @Override
    protected String doInBackground(Stock... params){
        stock = params[0];
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(LINK + stock.getStockSymbol() + "/quote?token=" + APIKEY);
            Log.d(TAG, "doInBackground: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseJson(String s){
        DecimalFormat df = new DecimalFormat("#.##%");
        try {
            JSONObject jStock = new JSONObject(s);
            stock.setPrice(jStock.getDouble("latestPrice"));
            stock.setPriceChange(jStock.getDouble("change"));
            stock.setChangePercent(df.format(jStock.getDouble("changePercent")));

        } catch (JSONException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("Error");
            builder.setMessage("Response Code: 400 Not Found");
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        parseJson(s);
        if (stock.getPrice() == 0){
            return;
        } else mainActivity.addData(stock);
    }
}