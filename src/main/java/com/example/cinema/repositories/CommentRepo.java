package com.example.cinema.repositories;

import com.example.cinema.models.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepo extends CrudRepository<Comment,Integer> {
    List<Comment> findByfilm(String filmname);
}
