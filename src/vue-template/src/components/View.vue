<template>
  <v-app>
    <app-bar/>
    <v-main>
      
        <router-view />
      
    </v-main>
  </v-app>
    
  
</template>

<script>
  export default {
  components: {
    AppBar:() => import('../components/AppBar.vue'),
    StockChart:() => import('../components/StockChart.vue')
  },

    data: () => ({
      
    }),
  }
</script>
