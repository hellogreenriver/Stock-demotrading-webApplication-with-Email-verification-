package com.example.stockprophet.service;

import java.io.IOException;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.stockprophet.model.Stock;
import com.example.stockprophet.model.Symbol;
import com.example.stockprophet.repository.SiteUserRepository;
import com.example.stockprophet.repository.SymbolRepository;
import com.example.stockprophet.model.CurrentPrice;
import com.example.stockprophet.model.SiteUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.HashSet;
import java.util.Set;

@Transactional
@AllArgsConstructor
@Service
public class StockService implements IStockService{

    @Autowired
    private SiteUserRepository userRepo;
    @Autowired
    private SymbolRepository symbolRepo;


    public Stock findStock(final String symbol){
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            String interval = "1d";
            
            from.add(Calendar.YEAR, -1); 
            
            long longFrom =  from.getTimeInMillis()/1000;
            long longTo = to.getTimeInMillis()/1000;
            return new Stock(symbol, longFrom, longTo, interval);
    }

    public CurrentPrice findCurrentPrice(final String symbol){
        return new CurrentPrice(symbol);
    }

    public List<ArrayList<BigDecimal>> callChart(final Stock stock) throws IOException {
        List<ArrayList<BigDecimal>> chart = new ArrayList<>();

        //getting timestamp and adjclose data from yahoo finance API
        final String endpoint = "https://query1.finance.yahoo.com/v8/finance/chart/";
        final String interval = "1d";
        final String url = endpoint + stock.getSymbol() + "?symbol=" + stock.getSymbol() + "&period1=" + stock.getFrom() + "&period2=" + 
        stock.getTo() + "&interval=" + interval + "&indicators=quote&includeTimestamps=true";
        System.out.println(url); 
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);
        JsonNode timestamp = node.get("chart").get("result").get(0).get("timestamp");
        JsonNode adjclose = node.get("chart").get("result").get(0).get("indicators")
        .get("adjclose").get(0).get("adjclose");
         
        //forming data for highcharts to apply
        for (int i = 0; i < timestamp.size(); i++){
            ArrayList<BigDecimal> chartData = new ArrayList<BigDecimal>();
            BigDecimal bigDecimalTimestamp =  new BigDecimal(timestamp.get(i).toString());

            //Milliseconds processing to highchart requirements
            chartData.add(bigDecimalTimestamp.multiply(new BigDecimal(1000)));
            
            BigDecimal bigDecimalAdjclose =  new BigDecimal(adjclose.get(i).toString());
            
            chartData.add(bigDecimalAdjclose);
            chart.add(chartData);
        }
        
        
        
        return chart;
    }

    public BigDecimal callCurrentPrice(final CurrentPrice currentPrice) throws IOException {
        

        //getting current price data from yahoo finance API
        final String endpoint = "https://query1.finance.yahoo.com/v8/finance/chart/";
        final String url = endpoint + currentPrice.getSymbol();
        
       
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(responseBody);
        JsonNode price = node.get("chart").get("result").get(0).get("meta")
        .get("regularMarketPrice");
        return new BigDecimal(price.toString());
    }

    public BigDecimal getUserId() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SiteUser user = userRepo.findByUsername(username).orElseThrow(RuntimeException::new);
        BigDecimal id = new BigDecimal(user.getId());
        return id;
    }

    

   


    public  List<Symbol> getSymbolListWithCurrentPrice(Long user_id) throws IOException{
        SiteUser siteUser = userRepo.findById(user_id).orElseThrow(RuntimeException::new);

        ObjectMapper mapper = new ObjectMapper();
    
        
        var json = mapper.writeValueAsString(siteUser.getSymbol());
        
        List<Symbol> symbolList = mapper.readValue(json, new TypeReference<List<Symbol>>(){});
        System.out.println("sYMBOL"+ symbolList);
        //get symbol.Id and symbol.symbol from symbolList 
        //update symbol.currentPrice by symbol.id
        for(Symbol _symbol: symbolList){
           
            Map<String, BigDecimal> map = new  HashMap<>();
            Symbol symbol = symbolRepo.findById(_symbol.getId()).orElseThrow(RuntimeException::new);
            CurrentPrice findCurrentPrice = findCurrentPrice(_symbol.getSymbol());
            BigDecimal price = callCurrentPrice(findCurrentPrice);
            map.putIfAbsent(_symbol.getSymbol(), price);
            if(map.get(_symbol.getSymbol()) != null){
                symbol.setCurrentPrice(map.get(_symbol.getSymbol()));
            }
            else{
                symbol.setCurrentPrice(price);
            }
            symbolRepo.save(symbol);
        }
        return siteUser.getSymbol();
    }

   
   
    public ResponseEntity buySymbol( Long user_id,   Symbol symbolRequest) throws IOException {
      
        SiteUser siteUser = userRepo.findById(user_id).orElseThrow(RuntimeException::new);
       
        CurrentPrice symbol = findCurrentPrice(symbolRequest.getSymbol());
        BigDecimal currentPrice = callCurrentPrice(symbol);
        BigDecimal costOfPurchase = symbolRequest.getNumberOfShares().multiply(currentPrice);  
         //if getNumberOfShares()*currentprice > siteUser.getAccount()
        if(symbolRequest.getNumberOfShares().multiply(currentPrice).compareTo(siteUser.getAccount()) == 1){

            System.out.println("getNumberOfShares()" + symbolRequest.getNumberOfShares() + "currentPrice" + currentPrice + "siteUser.getAccount()" + siteUser.getAccount());
            return  new ResponseEntity<>("Incorrect input",HttpStatus.FORBIDDEN);
        }
        //if getNumberOfShares()*currentprice <= siteUser.getAccount()
        else {
            
            
            Symbol _symbol = new Symbol();
            siteUser.setAccount(siteUser.getAccount().subtract(costOfPurchase));
            _symbol.setSymbol(symbolRequest.getSymbol());
            _symbol.setNumberOfShares(symbolRequest.getNumberOfShares());
            _symbol.setPrice(currentPrice);
            symbolRepo.save(_symbol);
           
            siteUser.getSymbol().add(_symbol);
            System.out.println(_symbol.toString());
            userRepo.save(siteUser);
          
            
            
            System.out.println("repo" + userRepo.findAll());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
       
      }
    
    public ResponseEntity buySymbolAtAccountVue( Long user_id, Long symbol_id,  Symbol symbolRequest) throws IOException {
      
      SiteUser siteUser = userRepo.findById(user_id).orElseThrow(RuntimeException::new);
      Symbol symbol= symbolRepo.findById(symbol_id)
          .orElseThrow(RuntimeException::new);
      CurrentPrice findCurrentPrice = findCurrentPrice(symbol.getSymbol());
      BigDecimal currentPrice = callCurrentPrice(findCurrentPrice);
      BigDecimal costOfPurchase = symbolRequest.getNumberOfShares().multiply(currentPrice);  
       //if getNumberOfShares()*currentprice > siteUser.getAccount()
      if(symbolRequest.getNumberOfShares().multiply(currentPrice).compareTo(siteUser.getAccount()) == 1){
  
          return  new ResponseEntity<>("Incorrect input",HttpStatus.FORBIDDEN);
      }
      //if getNumberOfShares()*currentprice <= siteUser.getAccount()
      else if(symbol.getNumberOfShares().subtract(symbolRequest.getNumberOfShares()).compareTo(symbol.getNumberOfShares()) <= 0){
  
        Symbol _symbol = new Symbol();
        siteUser.setAccount(siteUser.getAccount().subtract(costOfPurchase));
        
        _symbol.setSymbol(symbolRequest.getSymbol());
        _symbol.setNumberOfShares(symbolRequest.getNumberOfShares());
        _symbol.setPrice(currentPrice);
        userRepo.save(siteUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
      }
      else{
        return new ResponseEntity<>("Error" ,HttpStatus.FORBIDDEN);
      }
    }

    public ResponseEntity sellSymbol( Long user_id, Long symbol_id, Symbol symbolRequest) throws IOException {
        SiteUser siteUser = userRepo.findById(user_id).orElseThrow(RuntimeException::new);
        Symbol symbol= symbolRepo.findById(symbol_id).orElseThrow(RuntimeException::new);
        CurrentPrice findCurrentPrice = findCurrentPrice(symbol.getSymbol());
        BigDecimal currentPrice = callCurrentPrice(findCurrentPrice);
        BigDecimal profitAndLoss = symbolRequest.getNumberOfShares().multiply(currentPrice);
      
        //if symbol.getNumberOfShares() - symbolRequest.getNumberOfShares() < 0
        if(symbol.getNumberOfShares().subtract(symbolRequest.getNumberOfShares()).compareTo(new BigDecimal( 0)) == -1){
            return  new ResponseEntity<>("Incorrect input",HttpStatus.FORBIDDEN);
       }

       //if symbol.getNumberOfShares() - symbolRequest.getNumberOfShares() == 0
       else if(symbol.getNumberOfShares().subtract(symbolRequest.getNumberOfShares()).compareTo(new BigDecimal( 0)) == 0){
        siteUser.setAccount(siteUser.getAccount().add(profitAndLoss));
        userRepo.save(siteUser);
        this.deleteSymbol(symbol_id);
        return new ResponseEntity<>(HttpStatus.CREATED);
       }

       // if symbol.getNumberOfShares() - symbolRequest.getNumberOfShares() > 0
       else if(symbol.getNumberOfShares().subtract(symbolRequest.getNumberOfShares()).compareTo(new BigDecimal( 0)) == 1){
        //add method of bigdecimal
        siteUser.setAccount(siteUser.getAccount().add(profitAndLoss));
        userRepo.save(siteUser);
        symbol.setNumberOfShares(symbol.getNumberOfShares().subtract(symbolRequest.getNumberOfShares()));
        symbolRepo.save(symbol);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    else{
      return new ResponseEntity<>("Error" ,HttpStatus.FORBIDDEN);
    }
    }


    public SiteUser updateAccount(Long user_id, SiteUser userRequest ) {
        SiteUser account = userRepo.findById(user_id).map(user -> {
            user.setAccount(userRequest.getAccount());
            return userRepo.save(user);}).orElseThrow(RuntimeException::new);
        return account;
    }

    public Symbol updateSymbol(long id, Symbol accountRequest) {
    Symbol account= symbolRepo.findById(id)
        .orElseThrow(RuntimeException::new);

    account.setSymbol(accountRequest.getSymbol());
    account.setNumberOfShares(accountRequest.getNumberOfShares());
    account.setPrice(accountRequest.getPrice());
    return symbolRepo.save(account);

    }

    public void deleteSymbol( long symbol_id) { 
        symbolRepo.deleteById(symbol_id);
       }
}

