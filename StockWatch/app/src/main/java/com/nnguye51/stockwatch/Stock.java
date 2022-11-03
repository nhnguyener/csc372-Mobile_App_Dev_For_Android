package com.nnguye51.stockwatch;

import androidx.annotation.NonNull;

public class Stock {
    private final String symbol;
    private final String companyName;
    private final double latestPrice;
    private final double change;
    private final double changePercent;

    public Stock(String symbol, String companyName, double latestPrice, double change, double changePercent) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.latestPrice = latestPrice;
        this.change = change;
        this.changePercent = changePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getLatestPrice() { return latestPrice; }

    public double getChange() { return change; } //▲▼

    public double getChangePercent() { return changePercent; }

    @NonNull
    @Override
    public String toString() {
        return symbol + "\n" + companyName + "\n" + latestPrice + "\n" + change + "\n" + changePercent;
    }
}
