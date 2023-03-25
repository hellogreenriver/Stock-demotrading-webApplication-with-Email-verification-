<template>
    <div class="container">
       <div class="row">
         <div class="col-12 col-sm-4">
            <line-chart
                v-if="loaded"
                :chart-data="datacollection"
                :options="options"
              >
            </line-chart>
         </div>
       </div>
    </div>
  </template>
  <script>
    import axios from 'axios';
    import LineChart from '../LineChart.js';
    export default {
      components: {
        LineChart
      },
      data () {
        return {
          loaded: false,
          closePrices: null,
          timestamps: null,
          datacollection: null,
          errors: null
        }
      },
      created () {
        this.loaded = false

  
        axios.get('https://finnhub.io/api/v1/stock/candle?symbol=FXCM:SPX500&resolution=D&from=1640984400&to=1664485200&token=ccmvv5aad3i1nkren8l0ccmvv5aad3i1nkren8lg')
        .then(response => {
          console.log(response.data)
          this.closePrices = response.data.c
          this.timestamps = response.data.t
          this.fillData(response.data)
          this.fillOption(response.data)
          this.loaded = true
        }).catch(error => {
          this.errors = error
          console.log(this.errors)
          this.loaded = true
        });
      },
      methods: {
        fillData (StockData) {
          let stock_data = StockData
          console.log("2",stock_data)
          let UnixTimeLabels = [stock_data.t]
          console.log(stock_data.t)
          let labels = UnixTimeLabels.map(function(x){
            
            return new Date(x * 1000)
          })
          console.log(labels)
          let values = [stock_data.c]
          console.log("3",labels)
          this.datacollection = {
            labels: labels,
            datasets: [
              {
                lineTension: 0,
                label: "US_SPX500",
                borderColor: "orange",
                backgroundColor:"rgba(255,165,0,0.2)",
                data: values,
                tension: 0.1
              },
            ]
          }
        },
        fillOption (StockData) {
          let stock_data = StockData
          this.options = {
            legend: {
              display: false,
            },
            
            scales: {
              bounds: "ticks",
              xAxes: [{
                  type: 'time',
                  time: {
                      
                      unit: 'day'
                  },
                  distribution: 'series',
                  ticks: {
                      min: stock_data.t[0],
                      max: stock_data.t[stock_data.t.length-1],
                      source: "auto",
                      autoSkip: false
                  }
              }],
              yAxes: [{
                  ticks: {
                      beginAtZero:true
                  }
              }]
            }
          }
        }
      }
    }
  </script>
  
  <style scoped>
    .small {
      display: block;
      width: 600px;
      height: 600px;
      background-color: rgba(0,0,0,.1);
      padding: 30px;
      border-radius: 16px;
    }
  </style>