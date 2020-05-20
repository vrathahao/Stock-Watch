package com.vilasayrathahao.stockwatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncSymbolLoader extends AsyncTask<String, Void, String> {
    private MainActivity mainAct;

    private static final String URL1 = "https://api.iextrading.com/1.0/ref-data/symbols";
    private String target = "";

    AsyncSymbolLoader(MainActivity ma){
        mainAct = ma;
    }
    private static final String TAG = "AsyncSymbolLoader";

    @Override
    protected void onPostExecute(String s) {
        final ArrayList<Stock> candidateList = parseJSON(s);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainAct);
        final Stock[] stock = new Stock[1];
        if (candidateList.size() == 0) {
            builder.setTitle("Symbol Not Found: " + target);
            builder.setMessage("Cannot find this symbol");
            AlertDialog alert = builder.create();
            alert.show();
            return;
        } else if(candidateList.size() == 1){
            stock[0] = candidateList.get(0);
            AsyncDataLoader adl = new AsyncDataLoader(mainAct);
            adl.execute(stock[0]);
        } else {
            CharSequence[] stocks = new CharSequence[candidateList.size()];
            int i = 0;
            for (Stock entry : candidateList) {
                CharSequence cs = entry.getStockSymbol() + "-" + entry.getCompanyName();
                stocks[i] = cs;
                i++;
            }

            builder.setTitle("Make a selection");
            builder.setItems(stocks, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stock[0] = candidateList.get(which);
                    AsyncDataLoader adl = new AsyncDataLoader(mainAct);
                    adl.execute(stock[0]);
                }
            });
            builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected String doInBackground(String... params){

        target = params[0];
        Uri dataUri = Uri.parse(URL1);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }

        }catch (Exception e) {
            //Log.t(TAG, "doInBackground: ", sb.toString());
        }
        Log.d(TAG, "doInBackground: " + sb.toString());
        return sb.toString();
    }

    private ArrayList<Stock> parseJSON(String s){
        ArrayList<Stock> candidateList = new ArrayList<>();
        try{
            JSONArray jobjMain = new JSONArray(s);

            for(int i=0;i<jobjMain.length();i++){
                JSONObject jStocks = (JSONObject) jobjMain.get(i);
                String symbol = jStocks.getString("symbol");
                String name = jStocks.getString("name");
                //System.out.println(name.contains("TGT"));
                if(symbol.startsWith(target)){
                    candidateList.add(new Stock(symbol,name));
                }
                Log.d(TAG, "parseJSON: "+name);

            }

            return candidateList;
        }catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
