package com.example.outburst.service;


import com.example.outburst.dto.UserDTO;
import com.example.outburst.exceptions.UserExistException;
import com.example.outburst.model.entity.User;
import com.example.outburst.model.enums.Role;
import com.example.outburst.payload.request.SignUpRequest;
import com.example.outburst.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {

    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public User createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
        user.getRoles().add(Role.USER);

        try{
            LOG.info("Saving user {}", signUpRequest.getEmail());
            return userRepository.save(user);
        }
        catch (Exception e){
            LOG.error("Error saving user {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exists. Please check credentials");

        }

    }


    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setEmail(userDTO.getEmail());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setBio(userDTO.getBio());

        return userRepository.save(user);
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("The user with id: " + userId + " does not exist."));
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user with username: " + username + " does not exist."));
    }
}
