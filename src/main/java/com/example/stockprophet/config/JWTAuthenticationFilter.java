package com.example.stockprophet.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.stockprophet.model.SiteUser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private AuthenticationManager authenticationManager;

public JWTAuthenticationFilter(AuthenticationManager authenticationManager){

    this.authenticationManager = authenticationManager;

    //set login api url
    setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login","POST"));
    
   

   //set token at response 
    this.setAuthenticationSuccessHandler((req,res,ex) -> {
        
        Date issuedAt = new Date();
       
        String token = JWT.create()
                .withIssuer("DemoTrading") 
                .withClaim("username", ex.getName())
                
                .withIssuedAt(issuedAt)
              
              
                .sign(Algorithm.HMAC256("secret")); 
                System.out.println( token );
        //set token at header
        res.setHeader("X-AUTH-TOKEN", token);
        res.setStatus(200);
    });

    // when login fails
    this.setAuthenticationFailureHandler((req,res,ex) -> {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    });
}

@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
        ServletInputStream stream = request.getInputStream();
        
        SiteUser user = new ObjectMapper().readValue(request.getInputStream(), SiteUser.class);
        return this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>())
        );
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
    
}
