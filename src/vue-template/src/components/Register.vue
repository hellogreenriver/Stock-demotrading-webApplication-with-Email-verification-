<template>
    
    <div>
        <v-app >
        <v-main>
            <v-container class='fill-height' fluid>
                <v-row align='center' justify='center'>
                    <v-col cols='12' sm='8' md='4'>
                        <v-card class='elevation-12' :loading='isLoading'>
                            <v-toolbar color='#e0e0e0' dark flat>
                                <v-toolbar-title class='grey--text text--darken-1'>Registration Form</v-toolbar-title>
                            </v-toolbar>
                            <v-card-text>
                                <v-form ref='form'>
                                    <v-text-field
                                        label='Name'
                                        prepend-icon='mdi-account'
                                        type='text'
                                        v-model='username'
                                        :rules="[rules.required, rules.minmax]"
                                    ></v-text-field>

                                    <v-text-field
                                        label='Email'
                                        prepend-icon='mdi-email'
                                        type='text'
                                        v-model='email'
                                        :rules="[rules.required, rules.email]"
                                    ></v-text-field>
                                    <v-text-field
                                        label='Password'
                                        prepend-icon='mdi-lock'
                                        type='password'
                                        v-model='password'
                                        :rules="[rules.required, rules.passwordMinmax]"
                                    ></v-text-field>
                                    <v-text-field
                                        label='Password confirmation'
                                        prepend-icon='mdi-lock-question'
                                        type='password'
                                        v-model='passwordConfirmation'
                                        :rules="[rules.required, rules.passwordMinmax, comformPassword]"
                                    ></v-text-field>
                                </v-form>
                            </v-card-text>
                            <v-card-actions>
                                
                                <router-link to='/login' class='pull-left'
                                    >Go back to login</router-link
                                >
                                <div class='flex-grow-1'></div>
                                <v-btn color='#e0e0e0' @click='doRegister()' class='pull-right'
                                    >Sign up</v-btn
                                >
                            </v-card-actions>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </v-main>
        </v-app>
            
        <v-snackbar v-model="snackbar" timeout='-1' >
            Please check your Email to verify your account
            <template v-slot:action="{ attrs }">
                <v-btn
                    color='pink'
                    text
                    v-bind='attrs'
                    @click='goLogin'
                >
                    click here to login!
                </v-btn>
            </template>
        </v-snackbar>
                
    </div>
            
   
</template>
<script>
import axios from 'axios'

export default {
    data() {
        return {
            isLoading: false,
            snackbar: false,
            username: '',
            email: '',
            token:'',
            password: false,
            passwordConfirmation: false,
            errors: '',
            rules: {
                required: value => !!value || 'Please input',
                email: value =>  !value || /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(value)|| 'Email address format is incorrect',
                minmax: value => (value && value.length >= 3 && value.length <= 50) || 'Please input between 3 and 50 characters',
                passwordMinmax: value => (value && value.length >= 7 && value.length <= 128) || 'Please input between 7 and 128 characters',
                comformPassword: function(){if(this.passwordConfirmation === this.password){return true} else {return 'Your comform password is incorrect'} } ,
      },
        };
    },
    mounted() {
        this.$refs.form.resetValidation()
        const recaptcha = this.$recaptchaInstance
        recaptcha.showBadge()
    },
    methods: {
       async doRegister() {
            this.isLoading = 'red';
            await this.$recaptchaLoaded();
            const RecaptchaResponseToken = await this.$recaptcha('register');
            this.token = RecaptchaResponseToken
            if(this.$refs.form.validate()){
                axios
                .post("security/process_register", {
                    username: this.username,
                    email: this.email,
                    recaptchaResponseToken: this.token,
                    password: this.password,
                    passwordConfirmation: this.passwordConfirmation
                })
                .then(response => {
                    console.log(response.data);
                    this.snackbar = true;
                    this.$refs.form.reset();
                    this.$refs.form.resetValidation();
                })
                .catch(error => {
                    this.errors =error;
                    this.isLoading = false;
                    console.log(this.errors)
                })
                .finally(() => {
                    this.isLoading = false;

                })
                }
                
            
           
        },
        comformPassword: function(){
            if(this.passwordConfirmation === this.password)
            {return true
            } else {
            return 'Your comform password is incorrect'} } ,
        goLogin(){
            this.snackbar = false;
            setTimeout(() => {
                this.$router.push('/login')
            }, 300);
        }
    }
};
</script>
