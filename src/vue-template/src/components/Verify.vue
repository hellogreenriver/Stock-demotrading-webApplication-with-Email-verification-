<template>
    <div>
        <div v-if="success">
            <div class="container text-center" >
            <h3>Congratulations, your account has been verified.</h3>
		    <h4> <router-link to='/login' 　class='ml-3'>Click here to login</router-link></h4>
	    </div>
        </div>
        <div v-else>
            <div class="container text-center" >
            <h3>Sorry, we could not verify account. It maybe already verified,
                or verification code is incorrect.</h3>
		    <h4> <router-link to='/login' 　class='ml-3'>Click here to login</router-link></h4>
	    </div>
        </div>
    </div>

</template>
 
 
<script>
    
import axios from 'axios'

     export default {
        data () {
            return {
                success: '',
                errors: {},
            }
        },
        computed:{

        },
        created:function(){
           console.log("query" + this.$route.query.code)
           this.verify()
        }, 
        methods:{
            async verify(){
                
               await axios
                    .get('/security/verify?code='+ this.$route.query.code)
                    .then(response => {
                        this.success = true
                        
                    })
                    .catch(error => {
                        this.errors = error.response.data.errors;
                        console.log(this.errors)
                        this.success = false
                    })
                    .finally(() => {

                })
            },
            
            }
        }
     

</script>