import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import( '../components/View.vue'),
    children: [
      

       
      {
        name: 'chart',
        path: '',
        component: () => import('../components/StockChart.vue'),
      },
      
      
      {
        name: 'account',
        path: '/account',
        component: () => import('../components/Account.vue'),
      },
      
      {
        name: 'verify',
        path: '/verify',
        component: () => import('../components/Verify.vue'),
      },
    ],
  },
  {
    name: 'Login',
    path: '/login',
    component: () => import('../components/Login.vue'),
  },

  {
    name: 'Register',
    path: '/register',
    component: () => import('../components/Register.vue'),
  },
 

  
    
  
]

const router = new VueRouter({
 
  base: process.env.BASE_URL,
  routes
})

export default router
