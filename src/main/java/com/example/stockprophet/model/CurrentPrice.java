package com.example.stockprophet.model;


import lombok.Getter;
import lombok.With;

@Getter
@With

public class CurrentPrice {
    private final String symbol;

    public CurrentPrice( String symbol){
        this.symbol = symbol; 
    }
}
