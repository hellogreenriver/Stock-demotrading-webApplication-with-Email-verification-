package com.example.stockprophet.service;

import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import com.example.stockprophet.model.SiteUser;

public interface IUserService {
    void register(SiteUser user, String siteURL) throws UnsupportedEncodingException, MessagingException;
    boolean verify(String verificationCode);
}
