import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    drawer: false,
    token: false,
    snackbar: false,
    dialog: false,
    user_id:null,
    accountValue: "You are not logged in",
    symbol: 'AAPL',
    numberOfShares: null,
    price: null,
    stockData: null,

  },
  getters: {
    user_id(state){
      return state.user_id
    },
    accountValue(state){
      return state.accountValue
    }
  },
  mutations: {
    SET_DRAWER (state, payload) {
      state.drawer = payload
    },
    SET_SNACKBAR(state, payload){
      state.snackbar = payload
    },
    SET_DIALOG(state, payload){
      state.dialog = payload
    },
    SET_USERID(state, payload){
      state.user_id = payload
    },
    SET_ACCOUNTVALUE(state, payload){
      state.accountValue = payload
    },
    SET_SYMBOL(state, payload){
      state.symbol = payload
    },
    SET_NUMBER_OF_SHARES(state, payload){
      state.numberOfShares = payload
    },
    SET_PRICE(state, payload){
      state.price = payload
    },
    SET_STOCKDATA(state, payload){
      state.stockData = payload
    },
    SET_TOKEN (state,token) {
      state.token = token
    },
    REMOVE_TOKEN(state){
      state.token = ''
    }
  },
  actions: {
    
  },
  modules: {
  }
})

