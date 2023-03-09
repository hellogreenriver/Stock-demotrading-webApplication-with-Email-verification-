package com.example.stockprophet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.stockprophet.model.SiteUser;
import org.springframework.data.jpa.repository.Query;

public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);

  
    Optional<SiteUser> findByVerificationToken(String verificationtoken);
    Optional<SiteUser> findByEmail(String email);
    Optional<SiteUser> findUserByResetToken(String resetToken);
}
