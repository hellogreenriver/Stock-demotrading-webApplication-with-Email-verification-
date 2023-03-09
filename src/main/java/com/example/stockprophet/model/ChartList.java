package com.example.stockprophet.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter

public class ChartList {
    public List<ChartData> chartData;

    public void setChartList(List<ChartData> list) {
        this.chartData = list;
    }
}
