package com.nnguye51.stockwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private final List<Stock> stockList;
    private final MainActivity mainActivity;

    StockAdapter(List<Stock> sList, MainActivity ma) {
        this.stockList = sList;
        this.mainActivity = ma;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.latestPrice.setText(Double.toString(stock.getLatestPrice()));
        String s = (stock.getChange() > 0) ? "▲": "▼";
        holder.change.setText(s + " " + Double.toString(stock.getChange()));
        holder.changePercent.setText("(" + Double.toString(stock.getChangePercent()) + "%)");
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
