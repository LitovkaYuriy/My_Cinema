package com.example.cinema.controllers;

import com.example.cinema.models.Film;
import com.example.cinema.models.Role;
import com.example.cinema.models.Tag;
import com.example.cinema.models.User;
import com.example.cinema.repositories.FilmRepo;
import com.example.cinema.repositories.TagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PreAuthorize("hasAuthority('ADMIN')")
@Controller
public class AddFilmController {
    @Autowired
    private TagRepo tagRepo;
    @Autowired
    private FilmRepo filmRepo;

    @Value("${upload.path}")
    private String uploadPath;


    @GetMapping("/addFilm")
    public String add(@AuthenticationPrincipal User user, Model model){
        //for header
        model.addAttribute("user",user);
        Role[] roles = {Role.ADMIN};
        model.addAttribute("roles", roles);

        Iterable<Tag> tags = tagRepo.findAll();
        model.addAttribute("tags",tags);

        return "addFilm";
    }


    @PostMapping("/addFilm")
    public String addFilm(@RequestParam String filmName,
                           @RequestParam String prevText,
                           @RequestParam String fullText,
                           @RequestParam String series,
                           @RequestParam("fileName") MultipartFile file,
                           @RequestParam(value = "tags",required = false) List<String> selected)
                           throws IOException {

        if(filmRepo.findByfilmName(filmName)!=null){
            return "redirect:film"+filmRepo.findByfilmName(filmName).getId();
        }
        String resultFilename = "";

        if(!file.isEmpty()){
            File uploadDir = new File(uploadPath);

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            resultFilename = uuidFile + '.' + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
        }

        Film film = new Film(prevText,fullText,filmName,resultFilename,series);
        List<Tag> tagList= new ArrayList<>();

        if(selected != null && selected.size() != 0){
            for (String str: selected) {
                Optional<Tag> newtag = tagRepo.findById(Integer.parseInt(str));
                Tag tag = newtag.orElse(new Tag());
                tagList.add(tag);
            }
            film.setTags(tagList);
        }

        filmRepo.save(film);

        return "redirect:addFilm";
    }


    @PostMapping("/deleteFilm")
    public String deleteFilm(@RequestParam String filmName){      //Mistake in delete parent row

        Film delFilm = filmRepo.findByfilmName(filmName);
        filmRepo.delete(delFilm);

        return "redirect:addFilm";
    }


    @PostMapping("/addTag")
    public String addTag(@RequestParam String tagName, HttpServletRequest request){

        Iterable<Tag> tags = tagRepo.findAll();

        for (Tag tag : tags) {
            if(tag.getName().contentEquals(tagName)) return "addFilm";
        }

        Tag tag = new Tag(tagName);
        tagRepo.save(tag);

        return "redirect:"+request.getHeader("referer");
    }


    @PostMapping("/deleteTag")
    public String deleteTag(@RequestParam String tagNameForDel, HttpServletRequest request){

        Iterable<Tag> tags = tagRepo.findAll();

        for (Tag tag : tags) {
            if(tag.getName().contentEquals(tagNameForDel)){
                tagRepo.delete(tag);
            }
        }

        return "redirect:"+request.getHeader("referer");
    }
}
