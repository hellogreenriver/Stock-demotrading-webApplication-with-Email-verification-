package com.example.stockprophet.config;




import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.stockprophet.repository.SiteUserRepository;
import com.example.stockprophet.util.Authority;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Configuration
public class SecurityConfig {


   
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
      
        http.cors().configurationSource(this.corsConfigurationSource());

        http
        .csrf().disable();
        http.authorizeHttpRequests(auth -> auth
                // Make static resources accessible
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // Make everyone accessible to /register and /login
                .mvcMatchers("/register", "/api/stock/{symbol}","/api/currentPrice/{symbol}","/","/security/**","/h2-console/**","/login").permitAll()
                // Admin only URL
                .mvcMatchers("/admin/**").hasAuthority(Authority.ADMIN.name())
                .anyRequest().authenticated()
            );
            
           
           
            http.headers(headers -> headers.frameOptions().disable());
            // Remember-Me setting
           
 
        http.addFilter(new JWTAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))));
        http.addFilterAfter(new LoginFilter(),JWTAuthenticationFilter.class);   
        return http.build();
    }

  
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addExposedHeader("X-AUTH-TOKEN");
        corsConfiguration.addAllowedOrigin("http://localhost:8081");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsSource;
    }
   

   

    

}



