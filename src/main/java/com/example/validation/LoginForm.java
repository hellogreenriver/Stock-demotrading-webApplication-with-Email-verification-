package com.example.validation;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    
    @NotBlank
    @Email
    private String email;

    @Min(6)
    private String password;
}
