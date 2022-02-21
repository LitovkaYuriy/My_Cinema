package com.example.cinema.repositories;

import com.example.cinema.models.Film;
import com.example.cinema.models.User;
import com.example.cinema.models.UserFilm;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserFilmRepo extends CrudRepository<UserFilm,Long> {
    public List<UserFilm> findByFilmAndUser(Film name,User user);
    public List<UserFilm> findByUser(User user);
    public List<UserFilm> findByFilm(Film film);
}
