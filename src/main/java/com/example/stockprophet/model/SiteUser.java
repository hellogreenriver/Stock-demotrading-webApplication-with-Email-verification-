package com.example.stockprophet.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.example.stockprophet.util.Authority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Getter;
import lombok.Setter;



@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name="SITEUSER")
public class SiteUser {
    
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Size(min=3, max=50)
    private String username;

    @Getter
    @Setter
    @Size(min=7, max=128)
    private String password;

    @Getter
    @Setter
    @Size(min=7, max=128)
    private String passwordConfirmation;

    @Getter
    @Setter
    @Email
    private String email;

    @Getter
    @Setter
    @Size(min = 1)
    private String matchingPassword;

    @Getter
    @Setter
	  private String verificationToken;

    @Getter
    @Setter
    private String resetToken;

    @Getter
    @Setter
    private boolean enabled;

    @Getter
    @Setter
    private Date createdOn;

    @Getter
    @Setter
    private Date lastLogin;

    @Getter
    @Setter
    private  Authority authority;

    @Getter
    @Setter
    private BigDecimal account;

   
 
    
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private List<Symbol> symbol = new ArrayList<>();
  
    
   



}
