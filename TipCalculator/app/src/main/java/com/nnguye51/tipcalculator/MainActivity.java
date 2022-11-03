package com.nnguye51.tipcalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText inputTotal;
    private EditText inputPeople;
    private TextView outputTip;
    private TextView outputTotalWithTip;
    private TextView outputTotalSplit;
    private TextView outputOverage;
    private RadioGroup rg;

    private Double total;
    private Double people;
    private Double totalWithTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputTotal = findViewById(R.id.inputTotal);
        inputPeople = findViewById(R.id.inputPeople);
        outputTip = findViewById(R.id.displayTipAmount);
        outputTotalWithTip = findViewById(R.id.displayTotalWithTip);
        outputTotalSplit = findViewById(R.id.displayTotalSplit);
        outputOverage = findViewById(R.id.displayOverage);
        rg = findViewById(R.id.radioGroupTip);
    }

    /* user input */
    public void storeInputTotal(View v) {
        String temp = inputTotal.getText().toString();
        if ( temp.trim().isEmpty() )
            return;
        total = Double.parseDouble(temp.trim());
    }

    public void storeInputPeople(View v) {
        String temp = inputPeople.getText().toString();
        if ( temp.trim().isEmpty() )
            return;
        people = Double.parseDouble(temp.trim());
    }

    public void clickGroupTip(View v) {
        if (total == null) {
            rg.clearCheck();
            Toast.makeText(this, "Please press enter for Bill Total.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.tip12) {
            Double tempTip = calculateTip(0.12);
            outputTip.setText(String.format("$%.2f", tempTip));
            Double tempBill = total + tempTip;
            totalWithTip = tempBill;
            outputTotalWithTip.setText(String.format("$%.2f", tempBill));
        } else if (v.getId() == R.id.tip15) {
            Double tempTip = calculateTip(0.15);
            outputTip.setText(String.format("$%.2f", tempTip));
            Double tempBill = total + tempTip;
            totalWithTip = tempBill;
            outputTotalWithTip.setText(String.format("$%.2f", tempBill));
        } else if (v.getId() == R.id.tip18) {
            Double tempTip = calculateTip(0.18);
            outputTip.setText(String.format("$%.2f", tempTip));
            Double tempBill = total + tempTip;
            totalWithTip = tempBill;
            outputTotalWithTip.setText(String.format("$%.2f", tempBill));
        } else if (v.getId() == R.id.tip20) {
            Double tempTip = calculateTip(0.20);
            outputTip.setText(String.format("$%.2f", tempTip));
            Double tempBill = total + tempTip;
            totalWithTip = tempBill;
            outputTotalWithTip.setText(String.format("$%.2f", tempBill));
        } else {
            String label = ((RadioButton)v).getText().toString();
            Log.d(TAG, "clickGroupTip: Unexpected button click: " + label);
            Double tempTip = calculateTip(0.0);
            outputTip.setText(String.format("$%.2f", tempTip));
            Double tempBill = total + tempTip;
            totalWithTip = tempBill;
            outputTotalWithTip.setText(String.format("$%.2f", tempBill));
        }
    }

    /* extra functions */
    public Double calculateTip(double tipPercent) {
        double answer = total * tipPercent;
        answer = Math.ceil(answer * 100.0) / 100.0;

        return answer;
    }

    /* buttons */
    public void splitAmount(View v) {
        if ( (people == null) || (people < 1) ) {
            Toast.makeText(this, "Please enter a valid positive # of people", Toast.LENGTH_SHORT).show();
            return;
        } else if (totalWithTip == null) {
            Toast.makeText(this, "Please enter bill total first.", Toast.LENGTH_SHORT).show();
            return;
        }
        double split = (totalWithTip)/people;
        split = Math.ceil(split * 100.0) / 100.0;
        double overage = (split * people) - totalWithTip;
        outputTotalSplit.setText(String.format("$%.2f", split));
        outputOverage.setText(String.format("$%.2f", overage));
    }

    public void clearAll(View v) {
        /* clear variables */
        total = null;
        people = null;
        totalWithTip = null;
        /* clear boxes */
        inputTotal.setText("");
        rg.clearCheck();
        outputTip.setText("");
        outputTotalWithTip.setText("");
        inputPeople.setText("");
        outputTotalSplit.setText("");
        outputOverage.setText("");
    }

    /* save on rotate */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("TIP", outputTip.getText().toString());
        outState.putString("TTIP", outputTotalWithTip.getText().toString());
        outState.putString("SPLIT", outputTotalSplit.getText().toString());
        outState.putString("OVERAGE", outputOverage.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        outputTip.setText(savedInstanceState.getString("TIP"));
        outputTotalWithTip.setText(savedInstanceState.getString("TTIP"));
        outputTotalSplit.setText(savedInstanceState.getString("SPLIT"));
        outputOverage.setText(savedInstanceState.getString("OVERAGE"));
    }
}