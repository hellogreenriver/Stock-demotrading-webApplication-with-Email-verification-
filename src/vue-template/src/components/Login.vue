<template>
<div>
  <v-app >
    <v-main>
      <v-container
        class="fill-height"
        fluid
      >
        <v-row
          align="center"
          justify="center"
        >
          <v-col
            cols="12"
            sm="8"
            md="4"
          >
            <v-card class="elevation-12" :loading="isLoading">
              <v-toolbar
                color="#e0e0e0"
                dark
                flat
              >
                <v-toolbar-title class="grey--text text--darken-1">Login form</v-toolbar-title>
              </v-toolbar>
              <v-card-text>
                <v-form>
                  <v-text-field
                    label="Username"
                    prepend-icon="mdi-account"
                    type="text"
                    v-model="username"
                    
                  ></v-text-field>

                  <v-text-field
                    label="Password"
                    prepend-icon="mdi-lock"
                    type="password"
                    v-model="password"
                    :error-messages="errors.password"
                  ></v-text-field>
                </v-form>
              </v-card-text>
              <v-card-actions>
                <div class='flex-grow-1'>
                  <v-row dense justify="space-around">
                    <v-col><router-link to='/register' 　class='ml-3'>Sigh up</router-link></v-col>
                   
                    <v-col><v-btn color='#e0e0e0' @click='login'>Login</v-btn></v-col>
                  </v-row>
                </div>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</div>
</template> 
 <script>
import { mapState } from "vuex"
import axios from 'axios'
export default {
    data() {
        return {
            username: '',
            password: '',
            token: '',
            errors: {},
            isLoading: false,
            idErrors:{},
        };
    },
    computed:{
      ...mapState(['user_id']),
    },
    mounted() {
        this.$refs.form.resetValidation()
        const recaptcha = this.$recaptchaInstance
        recaptcha.showBadge()
    },
    methods: {
      async  login() {
            this.isLoading = 'red';
            await this.$recaptchaLoaded();
            const RecaptchaResponseToken = await this.$recaptcha('login');
            this.token = RecaptchaResponseToken
                axios
                    .post('/api/login', {
                        username: this.username,
                        password: this.password,
                        recaptchaResponseToken: this.token,
                    })
                    .then(response => {
                        localStorage.setItem('token', response.headers['x-auth-token'])
                        
                        this.$router.push('/')
                        
                    })
                    .catch(error => {
                        this.errors = error;
                        console.log(this.errors);
                        this.isLoading = false
                    })
                    .finally(() => {
                    this.isLoading = false;
                    
                })
                
                setTimeout(stopLoading(),5000)
        },
        stopLoading(){
          this.isLoading = false
        }
       

        


    }
};
 </script>