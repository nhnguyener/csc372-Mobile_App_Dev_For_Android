package com.nnguye51.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    TextView companyName;
    TextView latestPrice;
    TextView change;
    TextView changePercent;

    public StockViewHolder(@NonNull View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.viewSymbol);
        companyName = itemView.findViewById(R.id.viewCompany);
        latestPrice = itemView.findViewById(R.id.viewPrice);
        change = itemView.findViewById(R.id.viewChange);
        changePercent = itemView.findViewById(R.id.viewPercent);
    }
}
