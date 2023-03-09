package com.example.stockprophet.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Symbol implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    private String symbol;
    private BigDecimal numberOfShares;
    private BigDecimal price;
    private BigDecimal currentPrice;

    @ManyToOne
    @JsonBackReference
    private SiteUser siteUser;

    @Override
    public String toString() {
        return "id=" + this.getId() + ", symbol=" + this.getSymbol() + ", numberOfShares =" + this.getNumberOfShares() + ", price =" + this.getPrice() + ", currentPrice =" + this.getCurrentPrice();
    }
}
