package com.example.outburst.validations;

import com.example.outburst.annotations.PasswordMatches;
import com.example.outburst.payload.request.SignUpRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        SignUpRequest signUpRequest = (SignUpRequest) o;
        return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword());
    }
}
