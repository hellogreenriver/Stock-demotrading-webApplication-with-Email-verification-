package com.example.stockprophet.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.stereotype.Service;

import com.example.stockprophet.model.Stock;


import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class RefreshService {

    private final Map<Stock, Boolean> stocksToRefresh;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Duration refreshPeriod = Duration.ofSeconds(15);

    public RefreshService(){
        stocksToRefresh = new HashMap<>();
        setRefreshEvery15Minutes();
    }

    public boolean shouldRefresh(final Stock stock){
        if (!stocksToRefresh.containsKey(stock)){
            stocksToRefresh.put(stock, true);
            return true;
        }
        return stocksToRefresh.get(stock);
    }

    private void setRefreshEvery15Minutes(){
        scheduler.scheduleAtFixedRate(() ->
        stocksToRefresh.forEach((stock, value) -> {
            if (stock.getLastAccessed().isBefore(LocalDateTime.now().minus(refreshPeriod)))
            {
                System.out.println("Setting should refresh" );
                stocksToRefresh.remove(stock);
                stocksToRefresh.put(stock.withLastAccessed(LocalDateTime.now()), true);
            }
        }),0, 15, SECONDS);
    }
}
