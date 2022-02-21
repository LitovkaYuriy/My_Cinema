package com.example.cinema.controllers;

import com.example.cinema.models.Film;
import com.example.cinema.models.Role;
import com.example.cinema.models.User;
import com.example.cinema.models.UserFilm;
import com.example.cinema.repositories.FilmRepo;
import com.example.cinema.repositories.UserFilmRepo;
import com.example.cinema.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserPageController {

    @Autowired
    UserFilmRepo userFilmRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    FilmRepo filmRepo;


    @GetMapping("/userpage")
    public String UserPage(@AuthenticationPrincipal User user,Model model){
        //for header
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);
        List<UserFilm> userFilms = userFilmRepo.findByUser(user);

        model.addAttribute("userFilms",userFilms);
        model.addAttribute("user",user);

        return "userfilm";
    }


    @PostMapping("open")
    public String ChangeOpen(@AuthenticationPrincipal User user){

        user.setOpen(!user.getOpen());
        userRepo.save(user);

        return "redirect:/userpage";
    }


    @GetMapping("/user{name}")
    public String AnyPage(@AuthenticationPrincipal User user,
                          @PathVariable String name,
                          Model model){
        try {
            //for header
            model.addAttribute("user", user);
            Role[] roles = {Role.ADMIN};
            model.addAttribute("roles", roles);
            System.out.println();

            User findUser = userRepo.findByUsername(name);

            if (findUser.getId().equals(user.getId())) return "redirect:/userpage";

            else if (findUser.getOpen() == true) {
                List<UserFilm> userFilms = userFilmRepo.findByUser(findUser);
                model.addAttribute("userFilms", userFilms);

                return "looklist";
            } else return "hidden";
        }
        catch (RuntimeException ex){
            model.addAttribute("message","Пользователь " + name + " не был найден");
            return "filmlist";
        }
    }


    @PostMapping("deleteUserFilm")
    public String DeleteUserFilm(@AuthenticationPrincipal User user,
                                 @RequestParam String film){

        Film flm = filmRepo.findById(Integer.parseInt(film)).orElseThrow();
        List<UserFilm> userFilm = userFilmRepo.findByFilmAndUser(flm,user);

        for (UserFilm usrfilm: userFilm) {
            UserFilm fr = userFilmRepo.findById(usrfilm.getId()).orElseThrow();
            flm.DeleteScore(fr.getScore());
            filmRepo.save(flm);
            userFilmRepo.delete(fr);
        }

        return "redirect:/userpage";
    }
}
