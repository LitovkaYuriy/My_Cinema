package com.example.cinema.controllers;

import com.example.cinema.models.*;
import com.example.cinema.repositories.FilmRepo;
import com.example.cinema.repositories.TagRepo;
import com.example.cinema.repositories.UserFilmRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PreAuthorize("hasAuthority('ADMIN')")
@Controller
public class EditFilmController {

    @Autowired
    public FilmRepo filmRepo;
    @Autowired
    private TagRepo tagRepo;
    @Autowired
    private UserFilmRepo userFilmRepo;

    @Value("${upload.path}")
    private String uploadPath;


    @GetMapping("/edit{id}")
    public String EditFilm(@PathVariable String id, @AuthenticationPrincipal User user, Model model){
        //for header
        model.addAttribute("user",user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);

        Film film =filmRepo.findById(Integer.parseInt(id)).orElseThrow();
        List<Tag> tags = (List<Tag>) tagRepo.findAll();

        model.addAttribute("tags",tags);
        model.addAttribute("film",film);

        return "editFilm";
    }


    @PostMapping("/edit{id}")
    public String UpdateFilm(@RequestParam Integer id,
                             @RequestParam String filmName,
                             @RequestParam String series,
                             @RequestParam String prevText,
                             @RequestParam String fullText,
                             @RequestParam("fileName") MultipartFile file,
                             @RequestParam(value = "tags",required = false) String[] selected
                             ) throws IOException {

        Film film = filmRepo.findById(id).orElseThrow();
        film.setFilmName(filmName);
        film.setSeries(series);
        film.setPrevText(prevText);
        film.setFullText(fullText);
        ArrayList<Tag> list = new ArrayList<>();
        if(selected != null){
            for (String num: selected
                 ) {
                list.add(tagRepo.findById(Integer.parseInt(num)).orElseThrow());
            }
        }
        film.setTags(list);
        String resultFilename="";

        if(!file.isEmpty()){
            File uploadDir = new File(uploadPath);

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            resultFilename = uuidFile + '.' + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/"  +resultFilename));
            film.setFilename(resultFilename);
        }

        filmRepo.save(film);

        return "redirect:/film"+id;
    }

}
