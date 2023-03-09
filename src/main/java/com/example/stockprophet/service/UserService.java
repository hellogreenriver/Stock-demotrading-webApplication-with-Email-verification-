package com.example.stockprophet.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.stockprophet.model.SiteUser;
import com.example.stockprophet.repository.SiteUserRepository;

@Transactional
@Service
public class UserService implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
	private  SiteUserRepository userRepo;
	
	@Autowired
	private JavaMailSender mailSender;
    //Registration before e-mail verified
    public void register( SiteUser user, String siteURL) 
			throws UnsupportedEncodingException, MessagingException {
		if(user.getPassword().equals(user.getPasswordConfirmation())){
			String encodedPassword = passwordEncoder.encode(user.getPassword());
		    user.setPassword(encodedPassword);
		    user.setUsername(user.getUsername());
		    String randomCode = UUID.randomUUID().toString();
		    user.setVerificationToken(randomCode);
		    user.setEnabled(false);
		
		    userRepo.save(user);
		
		    sendVerificationEmail(user, siteURL);
		}
		else{
			System.out.println("Register failed");
		}
	}

    
    public boolean verify(String verificationToken) {
		Optional<SiteUser> Optional = userRepo.findByVerificationToken(verificationToken);
		SiteUser user = Optional.get();
		if (!Optional.isPresent() || user.isEnabled()) {
			return false;
		} else  {

			user.setVerificationToken(null);
			user.setEnabled(true);
			user.setAccount(new BigDecimal(100000));
			userRepo.save(user);
			
			return true;
		}
    }

	public boolean processForgotPassword(String userEmail, String siteURL)throws MessagingException, UnsupportedEncodingException {

		// Lookup user in database by e-mail
		Optional<SiteUser> optional = userRepo.findByEmail(userEmail);

		if (!optional.isPresent()) {
			return false;
		} else {
			
			// Generate random  string token for reset password 
			SiteUser user = optional.get();
			user.setResetToken(UUID.randomUUID().toString());

			// Save token to database
			userRepo.save(user);
			sendResetEmail(user, siteURL);
			
			 return true;
		}
	}
	public void setNewPassword(Optional <SiteUser> user ) {

			SiteUser resetUser = user.get(); 
			String encodedPassword = passwordEncoder.encode(resetUser.getPassword());
			// Set new password    
		    resetUser.setPassword(encodedPassword);
            
			// Set the reset token to null so it cannot be used again
			resetUser.setResetToken(null);

			// Save user
			userRepo.save(resetUser);

   }
   @Async
   private void sendVerificationEmail(SiteUser user, String siteURL) 
			throws MessagingException, UnsupportedEncodingException {
		String content = "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
				+ "Thank you,<br>";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		//Mail setting and send
		helper.setFrom("maqatibet@gmail.com", "Demotrade");
		helper.setTo(user.getEmail());
		helper.setSubject("Please verify your registration");
		content = content.replace("[[URL]]", siteURL + "/#" +"/verify?code=" + user.getVerificationToken());
		helper.setText(content, true);

		mailSender.send(message);
		
		System.out.println("VerificationEmail has been sent");
	}
	@Async
	private void sendResetEmail(SiteUser user, String siteURL) throws MessagingException, UnsupportedEncodingException{
		//Mail setting and send
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
		helper.setFrom("support@demo.com", "your company name");
		helper.setTo(user.getEmail());
		helper.setSubject("Password Reset Request");
		helper.setText("To reset your password, click the link below:\n" + siteURL
				+ "/reset?token=" + user.getResetToken());
			
		mailSender.send(passwordResetEmail);

		System.out.println("ResetEmail has been sent");
	}
	
	
	
}
