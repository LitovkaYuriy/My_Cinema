package com.example.cinema.controllers;

import com.example.cinema.models.Film;
import com.example.cinema.models.Role;
import com.example.cinema.models.Tag;
import com.example.cinema.models.User;
import com.example.cinema.repositories.FilmRepo;
import com.example.cinema.repositories.TagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainPageController {

    @Autowired
    private FilmRepo filmRepo;
    @Autowired
    private TagRepo tagRepo;


    @GetMapping("/filmlist")
    public String FilmList(@AuthenticationPrincipal User user, Model model) {
        //for header
        model.addAttribute("user",user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);

       Iterable<Film> films = filmRepo.findAll();
       for (Film filminlist: films
             ) {
            if(filminlist.getScores().size()==0) filminlist.setScore(0.0);
            else filminlist.setScore(filminlist.Average());
            filmRepo.save(filminlist);
        }

        model.addAttribute("tags",tagRepo.findAll());
        model.addAttribute("films",films);

        return "filmlist";
    }


    @GetMapping("/")
    public String greeting() {
        return "redirect:/filmlist";
    }


    @PostMapping("findFilm")
    public String FindFilm(@AuthenticationPrincipal User user,
                           @RequestParam String filmName,
                           @RequestParam(required = false) List<Integer> tags,
                           Model model){
        //for header
        model.addAttribute("user",user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);

        int matched;
        List<Film> filmList = filmRepo.findByfilmNameContaining(filmName);
        ArrayList<Film> finalList = new ArrayList<>();
        if(tags != null){
            for (Film film: filmList
                 ) {
                matched = 0;
                for (Integer tagid: tags
                     ) {
                    if(film.getTagsId().contains(tagid)){
                        matched++;
                        if(matched == tags.size()){
                            finalList.add(film);
                        }
                    }
                    else break;
                }
            }
        }
        else finalList= (ArrayList<Film>) filmList;
        model.addAttribute("selectedtags",tags);
        model.addAttribute("find",filmName);
        model.addAttribute("tags",tagRepo.findAll());
        model.addAttribute("films",finalList);

        return "filmlist";
    }


    /*@PostMapping("findByTag")
    public String FindByTag(@AuthenticationPrincipal User user,
                            @RequestParam(required = false) List<Integer> tags,
                            Model model){
        //for header
        model.addAttribute("user",user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);

        ArrayList<Film> filmList = new ArrayList<>();
        int matched = 0;
        List<Film> allFilms = (List<Film>) filmRepo.findAll();

        if (tags != null){
            for (Film film: allFilms
                 ) {
                matched = 0;
                for (Tag tag: film.getTags()
                     ) {
                        if(tags.contains(tag.getId())) matched++;
                        if(matched==tags.size()){
                            filmList.add(film);
                            break;
                        }
                }
            }
            model.addAttribute("films",filmList);
        }
        else model.addAttribute("films",allFilms);

        model.addAttribute("tags",tagRepo.findAll());

        return "filmlist";
    }*/


    @PostMapping("sortByScore")
    public String SortByScore(@AuthenticationPrincipal User user,
                              @RequestParam(required = false) List<Integer> selectedFilms,
                              Model model){
        //for header
        model.addAttribute("user",user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);

        ArrayList<Film> list =new ArrayList<>();
        ArrayList<Film> finalList= new ArrayList<>();

        if(selectedFilms!=null){
            for (Integer i: selectedFilms
                 ) {
                list.add(filmRepo.findById(i).orElseThrow());
            }
            for (double i=10.0;i>=0.0;i-=0.1){
                for (Film fim: list
                     ) {
                    if(fim.getScore() <= i  &&  fim.getScore() > (i-0.1)) finalList.add(fim);
                }
            }
            model.addAttribute("films",finalList);
        }

        model.addAttribute("tags",tagRepo.findAll());

        return "filmlist";
    }
}
