package com.example.outburst.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    @NotEmpty
    private String email;
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    private String bio;


}
