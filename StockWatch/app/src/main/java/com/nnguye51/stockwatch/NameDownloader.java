package com.nnguye51.stockwatch;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NameDownloader implements Runnable {

    private final String TAG = getClass().getSimpleName();
    private final MainActivity mainActivity;

    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static HashMap<String, String> nameList = new HashMap<String, String>();

    public NameDownloader(MainActivity ma) {
        this.mainActivity = ma;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: START");

        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();

        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader( (new InputStreamReader(is)) );

            String line;
            while ( (line = br.readLine()) != null ) {
                sb.append( line ).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());
        } catch (Exception e) {
            Log.e(TAG,"run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());

        Log.d(TAG, "run: STOP");
    }

    public HashMap<String, String> matches(String s) {
        HashMap<String, String> foundMatches = new HashMap<String, String>();
        for (String key : nameList.keySet()) {
            if (key.contains(s)) {
                foundMatches.put(key, nameList.get(key));
            }
        }

        return foundMatches;
    }

    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Data download error");
            return;
        }

        final HashMap<String, String> nameMap = parseJSON(s);
        nameList = nameMap;
        mainActivity.runOnUiThread(() -> {
            mainActivity.updateNameMap(nameMap);
        });
    }

    private HashMap<String, String> parseJSON(String s) {
        String symbol;
        String name;
        HashMap<String, String> parseNameMap = new HashMap<String, String>();

        try {
            JSONArray jArrMain = new JSONArray(s);

            for (int i = 0; i < jArrMain.length(); i++) {
                JSONObject jObject = (JSONObject) jArrMain.get(i);
                String objSymbol = jObject.getString("symbol");
                String objName = jObject.getString("name");

                parseNameMap.put(objSymbol, objName);
            }
            Log.d(TAG, "parseJSON HASHMAP: " + parseNameMap);
            return parseNameMap;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
