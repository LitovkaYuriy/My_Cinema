package com.example.cinema.repositories;

import com.example.cinema.models.Film;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FilmRepo extends CrudRepository<Film,Integer> {
    Optional<Film> findByid(Integer id);
    Film findByfilmName(String name);
    List<Film> findByseries(String name);
    List<Film> findByfilmNameContaining(String name);

}
