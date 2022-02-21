package com.example.cinema.controllers;

import com.example.cinema.models.Role;
import com.example.cinema.models.User;
import com.example.cinema.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }


    @PostMapping("/registration")
    public String addUser(User user,Model model){

        User userFromDb =userRepo.findByUsername(user.getUsername());

        if(user.getUsername()=="" || user.getPassword()==""){
            model.addAttribute("message","Fields must be not empty");
            return "registration";
        }

        if(userFromDb!=null){
            model.addAttribute("message","User Exist");
            return "registration";
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        return "redirect:/login";
    }

}