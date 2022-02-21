package com.example.cinema.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class UserFilm {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Integer score;
    private String marker;


    @ManyToOne(optional=false,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;


    @ManyToOne(optional=false,fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name="film_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Film film;


    public UserFilm() {
    }

    public UserFilm(User user, Film film) {
        this.user = user;
        this.film = film;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}
