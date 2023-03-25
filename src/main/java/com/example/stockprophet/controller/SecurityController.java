package com.example.stockprophet.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.stockprophet.model.SiteUser;
import com.example.stockprophet.repository.SiteUserRepository;
import com.example.stockprophet.service.UserService;
import com.example.stockprophet.util.Authority;
import lombok.RequiredArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SiteUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
	private UserService service;
    
    @PostMapping("/process_register")
	public String processRegister(@RequestBody  SiteUser user, HttpServletRequest request) 
			throws UnsupportedEncodingException, MessagingException {
				
		service.register(user, getSiteURL(request));	
		return "register_success";
	}
    @GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		System.out.println("code");
		if (service.verify(code)) {
			
			return "verify_success";
		} else {
			
			return "verify_failed";
		}
	}
    @GetMapping("/forgot")
	public String displayForgotPasswordPage() {
		return "forgotPassword";
    }
    @PostMapping("/forgot")
    public String processForgotPasswordForm(Model model, String userEmail, HttpServletRequest request) 
	       throws UnsupportedEncodingException, MessagingException  {

		Optional<SiteUser> optional = repo.findByUsername(userEmail);
		
		if (!optional.isPresent()) {
			model.addAttribute("errorMessage", "We didn't find an account for that e-mail address.");
		} else {
			service.processForgotPassword(userEmail, getSiteURL(request));
			model.addAttribute("successMessage", "A password reset link has been sent to " + userEmail);
		}
		return "redirect:/login?register";
	}
	

    @GetMapping("/reset")
	public String displayResetPasswordPage(Model model, @Param("token") String token) {
		
		Optional<SiteUser> user = repo.findByUsername(token);

		if (user.isPresent()) { // Token found in DB

			model.addAttribute("resetToken", token);

		} else { // Token not found in DB

			model.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");

		}

		return "resetPassword";

	}

    @PostMapping("/reset")
	public String setNewPassword(Model model, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {

		// Find the user associated with the reset token
		Optional<SiteUser> user = repo.findByUsername(requestParams.get("token"));

		// This should always be non-null but we check just in case
		if (user.isPresent()) {
			
			service.setNewPassword(user);

			// In order to set a model attribute on a redirect, we must use
			// RedirectAttributes
			redir.addFlashAttribute("successMessage", "You have successfully reset your password.  You may now login.");

			return "redirect:login";
			
		} else {
			model.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
		}
		
		return "resetPassword";
   }

   @GetMapping("/api/test")
    public String test(){
        return "You were logged in ";
    }	
   @ExceptionHandler(MissingServletRequestParameterException.class)
	public String handleMissingParams(MissingServletRequestParameterException ex) {
		return "redirect:login";
	}
	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}	
}

