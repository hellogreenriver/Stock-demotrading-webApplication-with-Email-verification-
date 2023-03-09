package com.example.stockprophet.config;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class LoginFilter extends OncePerRequestFilter{
        
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get token from header
        String header = request.getHeader("X-AUTH-TOKEN");

        //　Check
        if(header == null || !header.startsWith("Bearer ")){
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request,response);
            return;
        }
        String token = header.substring(7);
        // Token verification and authentication.
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("secret")).build().verify(token);
        // Get username
        String username = decodedJWT.getClaim("username").asString();
        // Set login status
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username,null,new ArrayList<>()));
        filterChain.doFilter(request,response);
        System.out.println("loginfilter");
    }
}
