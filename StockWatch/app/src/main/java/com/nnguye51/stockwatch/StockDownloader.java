package com.nnguye51.stockwatch;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class StockDownloader implements Runnable {

    private final String TAG = getClass().getSimpleName();

    private final MainActivity mainActivity;
    private final String symbol;

    private static final String DATA_URL = "https://cloud.iexapis.com/stable/stock/STOCK_SYMBOL/quote?token=pk_da86d9d220a84673a6496e7fefc86d21";

    public StockDownloader(MainActivity ma, String s) {
        this.mainActivity = ma;
        this.symbol = s;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder(DATA_URL);
        int replaceStart = sb.indexOf("STOCK_SYMBOL");
        sb.replace(replaceStart, replaceStart+12, this.symbol);

        Log.d(TAG, "run: START");

        String newURL = sb.toString();

        try {
            URL url = new URL(newURL);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Data download error");
            return;
        }

        final HashMap<String, Double> financialData = parseJSON(s);

        mainActivity.runOnUiThread(() -> {
            mainActivity.updateDataMap(financialData);
        });
    }

    private HashMap<String, Double> parseJSON(String s) {
        String key;
        String value;
        HashMap<String, Double> parseData = new HashMap<String, Double>();

        try {
            JSONArray jArrMain = new JSONArray(s);

            for (int i = 0; i < jArrMain.length(); i++) {
                JSONObject jObject = (JSONObject) jArrMain.get(i);

                double objPrice;
                double objChange;
                double objChangePer;

                if (jObject.optJSONObject("latestPrice") != null)
                    objPrice = jObject.getDouble("latestPrice");
                else
                    objPrice = 0;

                if (jObject.optJSONObject("change") != null)
                    objChange = jObject.getDouble("change");
                else
                    objChange = 0;

                if (jObject.optJSONObject("changePercent") != null)
                    objChangePer = jObject.getDouble("changePercent");
                else
                    objChangePer = 0;

                Log.d(TAG, "parseJSON price: " + objPrice);
                Log.d(TAG, "parseJSON change: " + objChange);
                Log.d(TAG, "parseJSON changePercent: " + objChangePer);

                parseData.put("latestPrice", objPrice);
                parseData.put("change", objChange);
                parseData.put("changePercent", objChangePer);
            }
            Log.d(TAG, "parseJSON HASHMAP: " + parseData);
            return parseData;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
