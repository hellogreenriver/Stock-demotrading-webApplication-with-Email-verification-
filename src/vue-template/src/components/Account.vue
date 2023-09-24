<template>
  <v-app>
        <v-data-table
        :headers="headers"
        :items="sharesHeldByUser"
        :items-per-page="10"
        hide-default-footer
        :loading="isLoading"
        >
        

      
    <template v-slot:[`item.order`]="{ item }">
          <v-btn @click="buy(item)">Buy</v-btn>
          <v-btn @click="sell(item)">Sell</v-btn>
    </template>
      </v-data-table>
      <v-dialog
      v-model="dialog"
      width="500"
    >
      

      <v-card
      >
        <v-card-title >
         {{ stockSymbol }}
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
            @click="cancel()"
            
          >
            Cancel
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            text
            @click="dialog = false; order()"
            
          >
            {{ buyOrSell }}
          </v-btn>
         
        </v-card-actions>
      </v-card>
    </v-dialog>
      <v-snackbar
      v-model="snackbar"
    >
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="snackbar=false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>
<script>
    import axios from 'axios'
    import { mapState, mapMutations} from "vuex"
    export default {
   
  
      data: () => ({
        dialog: false,
        message: null,
        isLoading: false,
        snackbar: false,
        item: null,
        buyOrSell: null,
        stockSymbol: null,
        numberOfShares: null,
        currentPrice: null,
        lastPrice: null,
        id: null,
        sharesHeldByUser: [],
        errors: '',
       
        headers: [
        {
            text: 'Stock',
            align: 'start',
            sortable: false,
            value: 'name',
        },
        { text: 'Symbol', value: 'symbol' },
        { text: 'Number of share', value: 'numberOfShares' },
        { text: 'Price', value: 'price' },
        { text: 'Current price', value: 'currentPrice' },
        { text: 'Order', value: 'order' },
        ],
        
      }),
      computed:{
      ...mapState(['symbol']),
      user_id: function(){
        return this.$store.getters.user_id
      }
      },
      created(){
        this.getAccountValue()
        this.fetchData()
        
      },
      mounted() {
        this.loginCheck()
        const path = this.$route.path
        console.log("path"+path)
        console.log("type"+typeof(path))
      },   
      methods:{
        buy(item){
          this.dialog = true
          console.log("item" +item)
          
          this.item = item
          console.log("thisitem" + this.item.symbol)
          this.buyOrSell = 'Buy'
          this.stockSymbol = item.symbol
          this.id = item.id
          this.numberOfSharesBeforeOrder = item.numberOfShares
        },
        sell(item){
          this.dialog = true
          console.log("item" + item)
          
          this.item = item
          console.log("thisitem" + this.item.symbol)
          this.buyOrSell = 'Sell'
          this.stockSymbol = item.symbol
          this.id = item.id
          this.numberOfSharesBeforeOrder = item.numberOfShares
        },
       async order(){

          
          if(this.buyOrSell==='Buy'){
            
            axios
                  .post('/api/buy/' + this.user_id, {
                    symbol: this.item.symbol,
                    numberOfShares: this.numberOfShares,
                    
                  },{
                  headers: {
                    "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
                }
                })
                  .then(response => {
                    this.getAccountValue()
                    this.fetchData()
                    this.message = "Succeeded in buying stock"
                    this.snackbar = true
                  })
                  .catch(error => {
                      this.errors = response.data.errors;
                      this.message = 'Failed to buy stock'
                      this.snackbar = true
                      console.log(error);
                      
                  })
                  .finally(() => {
              })
          }
          else if(this.buyOrSell==='Sell'){
            
            axios
                  .post('/api/sell/' + this.user_id + '/' + this.id, {
                      numberOfShares: this.numberOfShares,
                  },{
                  headers: {
                    "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
                }
                })
                  .then(response => {
                    this.getAccountValue()
                    this.fetchData()
                    this.message = "Succeeded in selling stock"
                    this.snackbar = true
                  })
                  .catch(error => {
                      this.errors = error;
                      this.message = 'Failed to sell stock'
                      this.snackbar = true
                      console.log(this.errors)
                  })
                  .finally(() => {

              })
          }
        },
        
        cancel(){
          this.dialog = false
        },
        loginCheck(){
          axios.get('http://localhost:8080/api/isLogin',{
            headers: {
                "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
              }})
              .then( response => {
                
              })
              .catch( error => {
                this.$router.push("/login")
                console.log(error)
              })
    },
        
        
      async fetchData(){
        
       this.isLoading = true
       await axios
                .get('/api/dataTable/'+ this.user_id,{
                  headers: {
                    "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
                }
                })
                    .then(response => {
                        this.sharesHeldByUser = response.data
                        console.log(this.sharesHeldByUser)
                    })
                    .catch(error => {
                        this.errors = error;
                        console.log(this.errors)
                    })
                    .finally(() => {})
        this.isLoading = false
        },
        getAccountValue(){
        
        axios
             .get('/api/accountValue/' + this.user_id,{
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
        

        
      }
    }
</script>
  