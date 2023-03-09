package com.example.stockprophet.service;

import java.io.IOException;
import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.example.stockprophet.model.SiteUser;
import com.example.stockprophet.model.Symbol;

public interface IStockService {
    List<Symbol> getSymbolListWithCurrentPrice(final Long user_id) throws IOException;
    
    ResponseEntity  buySymbolAtAccountVue( Long user_id, Long symbol_id,  Symbol symbolRequest) throws IOException; 
    ResponseEntity sellSymbol( Long user_id, Long symbol_id, Symbol symbolRequest) throws IOException;
    SiteUser updateAccount(Long user_id, SiteUser userRequest );
}
