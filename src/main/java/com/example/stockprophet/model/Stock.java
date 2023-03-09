package com.example.stockprophet.model;

import java.time.LocalDateTime;
import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;


@Getter
@With
@AllArgsConstructor
public class Stock {
    private final String symbol;
    private final long from;
    private final long to;
    private final String interval;
    private final LocalDateTime LastAccessed;

    public Stock(final String symbol , long from, long to, String interval){
        this.symbol = symbol;
        this.from = from;
        this.to = to;
        this.interval = interval;
        LastAccessed = LocalDateTime.now();
    }

}
