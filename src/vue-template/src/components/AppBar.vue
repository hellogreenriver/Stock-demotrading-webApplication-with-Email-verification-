<template>
  <div>
    <v-navigation-drawer app v-model="drawer">
      <v-list-item>
        <v-list-item-content>
          <v-list-item-title class="text-h6">
            {{ accountValue }}
          </v-list-item-title>
          
            <v-list-item-subtitle>
             Your cash remaining
            </v-list-item-subtitle>
     
          
        </v-list-item-content>
      </v-list-item>

      <v-divider></v-divider>
      <v-list dense nav>
      <v-list-item to="/">
        <v-list-item-icon>
          <v-icon>{{ mdi-chart-line }}</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Chart</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item >
        <v-list-item-icon>
          <v-icon>{{ mdi-account }}</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title @click="routerPush()">Account</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <div v-if="isLogin">
      <v-list-item  >
        <v-list-item-icon>
          <v-icon>{{ mdi-logout }}</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title @click="logout()">Logout</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </div>
    </v-list>
    </v-navigation-drawer>
    <v-app-bar color="primary" dark app>
      <v-app-bar-nav-icon @click="setDrawer(!drawer)"></v-app-bar-nav-icon>
      <v-toolbar-title>DemoTrade</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-text-field
      :label="Symbol"
      color="secondary"
      hide-details
      style="max-width: 165px;"
      v-model = "search"
      type="text"
    >
      <template
        v-slot:append-outer
      >
        <v-btn
          class="mt-n2"
          elevation="1"
          fab
          small
          @click="searchSymbol()"

        >
          <v-icon>mdi-magnify</v-icon>
        </v-btn>
      </template>
    </v-text-field>

    </v-app-bar>

    <v-snackbar v-model="snackbar" timeout='-1' >
      Symbol  not found
            <template v-slot:action="{ attrs }">
                <v-btn
                    text
                    v-bind='attrs'
                    @click='snackbar=false'
                >
                    OK
                </v-btn>
            </template>
    </v-snackbar>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex"
import axios from 'axios'
export default {
  data(){
    return{
        snackbar:false,
        isLogin: false,
        idErrors: null,
        search: this.$store.state.search,
        nav_list:[
          {
            name:'Chart',
            icon:'mdi-chart-line',
            link:''
          },
          {
            name:'Account',
            icon: 'mdi-account',
            link:''
          },
          {
            name:'Logout',
            icon:'mdi-logout',
            link:''
          },
          

        ]

    }
  },
  computed:{
      ...mapState(['drawer']),
      tokenVerified: function(){
        return localStorage.getItem('tokenVerified')
      },
      user_id: function(){
        return this.$store.getters.user_id
      },
      accountValue: function(){
        return this.$store.getters.accountValue
      }
      
  },
  created:async function(){
    this.loginCheck()
    this.fetchUserInfo()
    
  },
  methods:{
    async loginCheck(){
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
            console.log(error)
        })

      }
      else{
        this.setAccountValue("You are not logged in") 
        this.isLogin = false
      }
    },
    
    logout(){
      localStorage.removeItem('token')
      localStorage.removeItem('tokenVerified')
      this.isLogin = false
      this.setUser_id(null)
      this.setAccountValue("You are not logged in") 
      this.$router.go({path: this.$router.currentRoute.path, force: true})
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
    async fetchUserInfo(){
     
      await axios
                .get('/api/user_id', {
                  headers: {
                    "X-AUTH-TOKEN" : "Bearer " + localStorage.getItem('token')
                }
                
                })
                    .then(response => {
                      
                      this.setUser_id(response.data)
                      
                    })
                    .catch(error => {
                        this.idErrors = error;
                        console.log(this.idErrors)
                    })
                    .finally(() => {})
                    await this.getAccountValue()

                   
    },
                   
        
    searchSymbol(){
      
      axios
           .get('api/stock/' + this.search)
           .then(response => {
            
            localStorage.setItem('symbol', this.search)
            this.$router.go({path: this.$router.currentRoute.path, force: true})
           })
           .catch( error => {
            this.snackbar =true
            console.log('appbar error' + error)
           })
    },
    routerPush(){
      if(localStorage.getItem('tokenVerified')){
        this.$router.push('/account')
      }
      else{
        this.$router.push('/login')
      }
    },
    
    ...mapMutations({
        setDrawer: 'SET_DRAWER',
        setUser_id:'SET_USERID',
        setAccountValue:'SET_ACCOUNTVALUE',
      }),
   
  }
}
</script>