package com.example.stockprophet.controller;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.stockprophet.model.Symbol;
import com.example.stockprophet.model.CurrentPrice;
import com.example.stockprophet.model.SiteUser;
import com.example.stockprophet.model.Stock;
import com.example.stockprophet.repository.SymbolRepository;
import com.example.stockprophet.repository.SiteUserRepository;
import com.example.stockprophet.service.StockService;
import javax.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/api")
public class ApiContoroller {
    
 

    @Autowired
    StockService service;

    @Autowired
    SiteUserRepository userRepo;
    @Autowired
    SymbolRepository symbolRepo;

    @GetMapping("/stock/{symbol}")
    public List<ArrayList<BigDecimal>> getHistricalQuote(@PathVariable("symbol") String symbol) throws IOException{
        final Stock stock = service.findStock(symbol);
         
        return service.callChart(stock);
    }
    @GetMapping(value = "/symbol")
    public List<Symbol> accountGet() {
       
        List<Symbol> list = symbolRepo.findAll();
       
        return list;
    }

    @GetMapping(value = "/accountValue/{user_id}")
    public ResponseEntity <BigDecimal> getAccountValue(@PathVariable(value = "user_id") Long user_id) {
       
        SiteUser accountValue = userRepo.findById(user_id).orElseThrow(RuntimeException::new);
       
        return  new ResponseEntity<>(accountValue.getAccount(), HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public List<SiteUser> loggerGet() {
       
        List<SiteUser> list = userRepo.findAll();
       
        return list;
    }
    
   

    

    @GetMapping("/currentPrice/{symbol}")
    public ResponseEntity <BigDecimal> getCurrentPrice(@PathVariable(value = "symbol") String symbol) throws IOException{

      CurrentPrice currentPrice = service.findCurrentPrice(symbol);
      
       return new ResponseEntity<>(service.callCurrentPrice(currentPrice), HttpStatus.OK);
    }

    @GetMapping("/dataTable/{user_id}")
    public ResponseEntity <List<Symbol>> getSymbolListWithCurrentPrice(@PathVariable(value = "user_id") Long user_id) throws IOException{

      List<Symbol> list = service.getSymbolListWithCurrentPrice(user_id);
      
       return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
  
   
    @GetMapping("/user_id")
    public ResponseEntity<BigDecimal> getuserId() {
      
      
      
      BigDecimal userId = service.getUserId();
     
      return new ResponseEntity<>(userId, HttpStatus.OK);
    }

    @GetMapping("/isLogin")
    public boolean isLogin(){
        return true;
    }
    
   
    

  @PostMapping("/buy/{user_id}")
  public ResponseEntity buySymbol(@PathVariable(value = "user_id") Long user_id,
   @RequestBody Symbol symbolRequest) throws IOException {
    
    return service.buySymbol(user_id,  symbolRequest);
    

}

  @PostMapping("/buy/{user_id}/{symbol_id}")
  public ResponseEntity buySymbolAtAV(@PathVariable(value = "user_id") Long user_id,
  @PathVariable(value = "symbol_id") Long symbol_id, @RequestBody Symbol symbolRequest) throws IOException {
    
    return service.buySymbolAtAccountVue(user_id, symbol_id, symbolRequest);
    

}

  @PostMapping("/sell/{user_id}/{symbol_id}")
  public ResponseEntity sellSymbol(@PathVariable(value = "user_id") Long user_id,
  @PathVariable(value = "symbol_id") Long symbol_id, @RequestBody Symbol symbolRequest) throws IOException {
    
    return  service.sellSymbol(user_id, symbol_id, symbolRequest);

}


 
@PutMapping("/user/{user_id}")
public ResponseEntity<SiteUser> updateAccount(@PathVariable(value = "user_id") Long user_id,
  @RequestBody SiteUser userRequest ) {
    
    SiteUser Account = service.updateAccount(user_id, userRequest);

    return new ResponseEntity<>(Account, HttpStatus.CREATED);
}
  
  @PutMapping("/symbol/{id}")
  public ResponseEntity<Symbol> updateSymbol(@PathVariable("id") long id, @RequestBody Symbol accountRequest) {
    Symbol account= symbolRepo.findById(id)
        .orElseThrow(RuntimeException::new);

    account.setSymbol(accountRequest.getSymbol());
    account.setNumberOfShares(accountRequest.getNumberOfShares());
    account.setPrice(accountRequest.getPrice());
    return new ResponseEntity<>(symbolRepo.save(account), HttpStatus.OK);
  }

  @DeleteMapping("/list/{symbol_id}")
  public ResponseEntity<HttpStatus> deleteSymbol(@PathVariable("symbol_id") long symbol_id) {
   symbolRepo.deleteById(symbol_id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
