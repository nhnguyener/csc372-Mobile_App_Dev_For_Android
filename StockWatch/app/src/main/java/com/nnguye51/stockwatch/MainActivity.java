package com.nnguye51.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextDirectionHeuristicCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.widget.GridLayout.LayoutParams;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView rView;
    private SwipeRefreshLayout swiper;

    private final List<Stock> stockList = new ArrayList<>();
    private StockAdapter stockAdapter;

    private HashMap<String, String> nameMap = new HashMap<String, String>();
    private HashMap<String, Double> dataMap = new HashMap<String, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Stock Watch");

        rView = findViewById(R.id.recycler);

        stockAdapter = new StockAdapter(stockList, this);
        rView.setAdapter(stockAdapter);
        rView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

        readJSON(); //check for old stocks

        boolean x = doNetCheck();
        if ( x == true ) {
            NameDownloader nameDownloader = new NameDownloader(this);
            new Thread(nameDownloader).start();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            builder.create().show();
        }

        //dummy stocks
        for (int i = 0; i < 5; i++) {
            Stock s = new Stock("DUMMY-" + i, "DUMMY INC.", 420.00, 0.69, 0.88);
            stockList.add(s);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeJSON();
    }

    private void doRefresh() {
        swiper.setRefreshing(false);
        Toast.makeText(this, "Stocks refreshed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stocks_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if  (item.getItemId() == R.id.menuAddStock) {
            //Toast.makeText(this, "TO-DO: Add Stock Selected", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Stock Selection");
            builder.setMessage("Please enter a Stock Symbol");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                Toast.makeText(this, "Adding Stock", Toast.LENGTH_SHORT).show();
                String inputSymbol = input.getText().toString();
                StockDownloader stockDownloader = new StockDownloader(this, inputSymbol);
                new Thread(stockDownloader).start();

                Stock addStock = makeStock(inputSymbol);
                stockList.add(addStock);
            });
            builder.setNegativeButton("CANCEL", (dialog, which) -> {});

            builder.create().show();
        } else {
            Toast.makeText(this, "Unlisted Option Selected/Error", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int pos = rView.getChildLayoutPosition(view);
        Stock stock = stockList.get(pos);

        Toast.makeText(this, "onClick " + stock.getSymbol() + " TO-DO", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = rView.getChildLayoutPosition(view);
        Stock stock = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete '" + stock.getSymbol() + "'?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            stockList.remove(pos);
            stockAdapter.notifyItemRemoved(pos);
            Toast.makeText(this, "Stock '" + stock.getSymbol() + "' Deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> {});

        builder.create().show();

        return false;
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean isMetered = cm.isActiveNetworkMetered();

        String metered =
                isMetered ? "\nConnection is metered" : "\nConnection is not metered";

        if (isConnected) {
            return true;
        } else {
            return false;
        }
    }

    public void networkCheck(View v) {
        doNetCheck();
    }

    public void updateNameMap(HashMap<String, String> hashMap) {
        try {
            nameMap = hashMap;
            Log.d(TAG, "updateNameMap: " + nameMap);
        } catch (Exception e) {
            Log.d(TAG, "updateNameMap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateDataMap(HashMap<String, Double> hashMap) {
        try {
            dataMap = hashMap;
            Log.d(TAG, "updateNameMap: " + dataMap);
        } catch (Exception e) {
            Log.d(TAG, "updateNameMap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Stock makeStock(String inputSymbol) {
        try {
            Log.d(TAG, "AddStock symbol: " + inputSymbol);
            Log.d(TAG, "AddStock name: " + nameMap.get(inputSymbol));
            Log.d(TAG, "AddStock price: " + dataMap.get("latestPrice"));
            Log.d(TAG, "AddStock change: " + dataMap.get("change"));
            Log.d(TAG, "AddStock changePercent: " + dataMap.get("changePercent"));
            Stock newStock = new Stock(inputSymbol, nameMap.get(inputSymbol), dataMap.get("latestPrice"), dataMap.get("change"), dataMap.get("changePercent"));
            Log.d(TAG, "AddStock create new Stock: " + newStock);
            return newStock;
        } catch (Exception e) {
            Log.d(TAG, "AddStock Error: " + e.getMessage());
        }
        return null;
    }

    private void writeJSON() {
        try {
            FileOutputStream outStream = getApplicationContext().openFileOutput("Notes.json", Context.MODE_PRIVATE);
            JsonWriter jwriter = new JsonWriter(new OutputStreamWriter(outStream, StandardCharsets.UTF_8));

            jwriter.setIndent("  ");
            jwriter.beginArray();

            for (Stock s : stockList) {
                jwriter.beginObject();
                jwriter.name("SYMBOL").value(s.getSymbol());
                jwriter.name("NAME").value(s.getCompanyName());
                jwriter.name("PRICE").value(s.getLatestPrice());
                jwriter.name("CHANGE").value(s.getChange());
                jwriter.name("CHANGEPER").value(s.getChangePercent());
                jwriter.endObject();
            }

            jwriter.endArray();
            jwriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readJSON() {
        try {
            InputStream inStream = getApplicationContext().openFileInput("Stocks.json");
            BufferedReader jreader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String currLine;

            while ( (currLine = jreader.readLine()) != null) {
                builder.append(currLine);
            }

            JSONArray jArray = new JSONArray(builder.toString());

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String symbol = jObject.getString("SYMBOL");
                String companyName = jObject.getString("NAME");
                double latestPrice = jObject.getDouble("PRICE");
                double change = jObject.getDouble("CHANGE");
                double changePercent = jObject.getDouble("CHANGEPER");

                Stock s = new Stock(symbol, companyName, latestPrice, change, changePercent);
                stockList.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}