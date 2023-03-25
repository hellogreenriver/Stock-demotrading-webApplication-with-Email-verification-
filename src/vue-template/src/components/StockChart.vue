<template>
 <div>
  
  <v-container>
  <v-row
  align="center"
  justify="space-around"
  >
  <v-col align="center">
    
       <h1 class="text-center"> {{ getSymbol }}</h1>
       
       
       <h1 class="text-center"> {{ this.currentPrice }}</h1>
       
  </v-col>
  <v-col
  align="center"
  >
      <v-btn
        class="ma-4"
        large
        depressed
        color="error"
        @click="sell()"
      >
       SELL
      </v-btn>
      <v-btn
        class="ma-4"
        large
        depressed
        color="success"
        @click="dialog=true;loginCheck()"
      >
        BUY
      </v-btn>
  </v-col>
  </v-row>
  <highcharts :constructor-type="'stockChart'" class="hc" :options="chartOptions" ref="chart"></highcharts>
</v-container>
  <v-dialog
      v-model="dialog"
      width="500"
    >
      

      <v-card
      :loading=isLoading
      >
        <v-card-title >
         {{ getSymbol }}
        </v-card-title>

       

      <v-container>
        <v-text-field 
        label="Number of shares"
        :rules="rules"
        hide-details="auto"
        v-model="numberOfShares"
      ></v-text-field>
      
         
      </v-container>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn
            color="primary"
            text
            @click="dialog=false"
            
          >
            Cancel
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            text
            
            @click="buy()"
            
          >
            Buy
          </v-btn>
         
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbar" timeout='-1' >
      {{ message }}
            <template v-slot:action="{ attrs }">
                <v-btn
                    color='pink'
                    text
                    v-bind='attrs'
                    @click='snackbar=false'
                >
                    Close
                </v-btn>
            </template>
    </v-snackbar>
  </div>
            
           

  

          
</template>

<script>
import axios from 'axios'
import { mapState, mapMutations} from "vuex"

export default {
  data () {
    return {
      snackbar: false,
      dialog: false,
      loaded: false,
      errors: '',
      message:null,
      numberOfShares: null,
      currentPrice: null,
      chartOptions: {
        rangeSelector: {
          selected: 1
        },
        xAxis: {
        type: 'datetime',
       
        },
        title: {
          text: localStorage.getItem('symbol') 
        },
        series: [{
          name: localStorage.getItem('symbol'),
          data: null,
          type: "area",
          threshold: null,
          tooltip: {
            valueDecimals: 2
          }
        }]
      }
    
    }
  },
  computed:{
    getSymbol: function(){
      return localStorage.getItem('symbol')
    },
    user_id: function(){
        return this.$store.getters.user_id
      }
  },
  created: function(){
    this.fetchData()
    this.getCurrentPrice()

  },
  
  
  methods: {
      fetchData(){
        if(!localStorage.getItem('symbol')){
          axios
            .get('/api/stock/' + this.$store.state.symbol )
            .then(response => {

              localStorage.setItem('symbol', this.$store.state.symbol)
              
              this.chartOptions.series[0].data= response.data
              console.log(response.data)
              console.log('data' + this.chartOptions.series[0].data)
              
            })
            .catch(error => {
              this.errors = error;
              console.log(this.errors);
              this.isLoading = false
            })
            .finally(() => {
            })
        }
        else if(localStorage.getItem('symbol')){
          axios
            .get('/api/stock/' + localStorage.getItem('symbol') )
            .then(response => {

              
              this.chartOptions.series[0].data= response.data
              console.log(response.data)
              console.log('data' + chartOptions.series[0].data)
              
            })
            .catch(error => {
              this.errors = error;
              console.log(this.errors);
              this.isLoading = false
            })
            .finally(() => {
            })
        }
        else{
          console.log('error')
        }
      },
      
      buy(){
        this.loginCheck()
        
        axios
            .post('/api/buy/' + this.user_id,{
              symbol: localStorage.getItem('symbol'),
              numberOfShares: this.numberOfShares,},
              {
              headers: {
                "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
              }  
              }
              
              )
            .then(response => {
              this.getAccountValue()
              console.log(response.data)
              this.message = 'Succeeded in buying stock'
              this.dialog = false
            })
            .catch(error => {
              this.errors = error;
              this.message = 'Failed to buy stock'
              console.log(this.errors);
              
            })
            .finally(() => {
      
            })
            this.dialog = false
            this.snackbar = true
            
      },
      sell(){
        this.loginCheck()
        this.$router.push('/account')
      },
     async loginCheck(){
        {
        if(localStorage.getItem('token')){
        await axios.get('/api/isLogin',{
            headers: {
                "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
            }
        })
        .then( res => {
            localStorage.setItem('tokenVerified', true)
            
            
            this.isLogin= true
            console.log(this.isLogin)
        })
        .catch( error => {
          localStorage.setItem('tokenVerified', false)
          this.isLogin = false
          this.$router.push('/login')
          console.log(error)
        })

      }
      else{
        this.$router.push('/login')
      }
        
      }
        
      },
      getCurrentPrice(){
        if(!localStorage.getItem('symbol')){
        axios
             .get('/api/currentPrice/' + this.$store.state.symbol,)
            .then(response => {
              this.currentPrice = response.data
              
            })
            .catch(error => {
              this.errors = error;
              console.log(this.errors);
              this.isLoading = false
            })
            .finally(() => {
            })
        }
        else if(localStorage.getItem('symbol')){
          axios
             .get('/api/currentPrice/' + localStorage.getItem('symbol'),)
             .then(response => {
              this.currentPrice = response.data
              console.log('price' + response.data)
              console.log('pricetype' + typeof response.data)
            })
            .catch(error => {
              this.errors = error;
              console.log(this.errors);
              this.isLoading = false
            })
            .finally(() => {
            })
        }
      },
      getAccountValue(){
        
        axios
             .get('/api/accountValue/' + this.user_id, {
              headers: {
                "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
              }  
              })
             .then(response => {
               this.setAccountValue(response.data) 
               
             })
             .catch(error => {
               this.errors = error;
               console.log(this.errors);
               
             })
             .finally(() => {
               
             })
     },
     ...mapMutations({
      setAccountValue:'SET_ACCOUNTVALUE',
      }),


    },
    
  }

  </script>
  
