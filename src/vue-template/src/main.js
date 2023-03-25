import Vue from 'vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import App from './App.vue'
import Highcharts from "highcharts";
import Stock from "highcharts/modules/stock";
import HighchartsVue from "highcharts-vue";

Stock(Highcharts);
Vue.use(HighchartsVue);
Vue.config.productionTip = false
Vue.config.devtools = true
new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
