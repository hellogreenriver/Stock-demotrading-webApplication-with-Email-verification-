package com.example.stockprophet.config;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import com.example.stockprophet.model.Symbol;
import com.example.stockprophet.model.SiteUser;
import com.example.stockprophet.repository.SymbolRepository;
import com.example.stockprophet.repository.SiteUserRepository;
import com.example.stockprophet.util.Authority;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Component
public class DataLoader implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;
    private final SiteUserRepository userRepo;
    private final SymbolRepository symbolRepo;
   
    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        var user = new SiteUser();
        user.setAuthority(Authority.ADMIN);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAccount(new BigDecimal(1000000));
        userRepo.save(user);
        
        
        
    }
}
