package com.example.cinema.controllers;

import com.example.cinema.models.*;
import com.example.cinema.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Controller
public class FilmDetailsController {

    @Autowired
    private FilmRepo filmRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private UserFilmRepo userFilmRepo;


    @GetMapping ("/film{id}")
    public String about(@AuthenticationPrincipal User user, @PathVariable String id, Model model){
        try {
        //For header
        model.addAttribute("user", user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);


        Film film = filmRepo.findById(Integer.parseInt(id)).orElseThrow(RuntimeException::new);

        model.addAttribute("film", film);

        String series = film.getSeries();
        List<Film> films = filmRepo.findByseries(series);
        films.remove(film);
        if(!films.isEmpty()) model.addAttribute("films", films);
        List<Comment> commentsW = commentRepo.findByfilm(film.getFilmName());
        ArrayList<Comment> comments = new ArrayList<>();
        for(int i=commentsW.size()-1;i>=0;i--){
            comments.add(commentsW.get(i));
        }
        List<UserFilm> userFilms = userFilmRepo.findByFilmAndUser(film,user);

        Integer selected = null;        //score
        String selectedn = "inplan";    //marker
        boolean inCollection = true;
        boolean alreadyScored = true;

        if(userFilms.isEmpty()) inCollection = false;
        else {selected = userFilms.get(0).getScore();selectedn = userFilms.get(0).getMarker();}
        if(selected == null) alreadyScored = false;

        Double score;                   //average

        if(film.getScores().size() == 0) score = 0.0;
        else score=film.Average();

        model.addAttribute("alreadyScored", alreadyScored);
        model.addAttribute("inCollection", inCollection);
        model.addAttribute("selectedn", selectedn);
        model.addAttribute("selected", selected);
        model.addAttribute("comments", comments);
        model.addAttribute("id", id);
        model.addAttribute("filmScore", score);

        return "about";
        }
        catch (RuntimeException ex){
            model.addAttribute("message","Данного фильма не существует");
            return  "filmlist";
        }
    }


    @PostMapping("addComment")
    public String AddMessage(@AuthenticationPrincipal User user,
                             @RequestParam String fn,                   //film name
                             @RequestParam String comment,
                             @RequestParam String page){

        Comment message = new Comment(comment,fn,user.getUsername());
        Date date = new Date();
        message.setDate(date.toString());
        commentRepo.save(message);

        return "redirect:/film"+page;
    }


    @PostMapping("score")
    public String SetScore(@AuthenticationPrincipal User user,
                              @RequestParam Integer id,         //film id
                              @RequestParam Integer select,     //select score
                              Model model){

        Film film = filmRepo.findById(id).orElseThrow();
        List<UserFilm> userFilm = userFilmRepo.findByFilmAndUser(film,user);

        for (UserFilm usrfilm: userFilm
        ) {
            UserFilm fr = userFilmRepo.findById(usrfilm.getId()).orElseThrow();
            if(fr.getScore()==null)
            {
                fr.setScore(select);
                film.AddScore(select);
            }
            else
            {
                film.DeleteScore(fr.getScore());
                fr.setScore(select);
                film.AddScore(select);
            }
            userFilmRepo.save(fr);
        }

        filmRepo.save(film);
        model.addAttribute("user",user);

        return "redirect:/film"+id;
    }


    @PostMapping("addUserFilm")
    public String AddUserFilm(@AuthenticationPrincipal User user,
                              @RequestParam String film,
                              @RequestParam String select){

        Film flm = filmRepo.findById(Integer.parseInt(film)).orElseThrow();
        List<UserFilm> userFilms = userFilmRepo.findByFilmAndUser(flm,user);
        UserFilm userFilm;

        if(userFilms.isEmpty()) userFilm = new UserFilm(user,flm);
        else userFilm=userFilms.get(0);
        userFilm.setMarker(select);

        userFilmRepo.save(userFilm);

        return "redirect:/film"+film;
    }


    @PostMapping("deleteCurrentFilm")
    private String deleteFilm(@RequestParam Integer filmid){

        System.out.println(filmid);
        Optional<Film> flm = filmRepo.findByid(filmid);

        if(flm  != null){
            List<UserFilm> list = userFilmRepo.findByFilm(flm.get());
            for (UserFilm uf: list
            ) {
                userFilmRepo.delete(uf);
            }
        }

        filmRepo.delete(flm.get());

        return "redirect:/filmlist";
    }
}
