package com.example.cinema.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(length = 1000000)
    private String prevText, fullText;
    @Column(unique = true)
    private String filmName;
    private String filename, series;
    private double score = 0.00;

    @ElementCollection
    private List<Integer> scores;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="film_tags",
            joinColumns = @JoinColumn(name="film_id"),
                inverseJoinColumns = @JoinColumn(name="tag_id")
    )
    private List<Tag> tags = new ArrayList<>();


    @OneToMany(mappedBy = "film",orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserFilm> userFilm= new ArrayList<>();


    public Film() {}

    public Film(String prevText, String fullText, String filmName, String filename, String series) {
        this.prevText = prevText;
        this.fullText = fullText;
        this.filmName = filmName;
        this.filename = filename;
        this.series = series;
    }

    public Film(String prevText, String fullText, String filmName, String filename, String series, List<Tag> tags) {
        this.prevText = prevText;
        this.fullText = fullText;
        this.filmName = filmName;
        this.filename = filename;
        this.series = series;
        this.tags = tags;
    }

    public List<Integer> getTagsId(){
        ArrayList<Integer> tagsId = new ArrayList<>();
        for (Tag tag: tags
             ) {
            tagsId.add(tag.getId());
        }
        return tagsId;
    }

    public void DeleteScore(Integer score){
        for (Integer i : scores
        ) {
            if( i == score){
                scores.remove(i);
                break;
            }
        }
        return;
    }

    public void RemoveUserFilm(UserFilm userFilm){
        this.userFilm.remove(userFilm);
        return;
    }

    public double Average(){
        double sum = 0;
        for (double el : scores
        ) {
            sum+=el;
        }
        double v = sum/scores.size();
        v = (Double)Math.floor(v*100)/100.0;
        return (double) v ;
    }

    public void AddScore(Integer score){
        scores.add(score);
        return;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrevText() {
        return prevText;
    }

    public void setPrevText(String prevText) {
        this.prevText = prevText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<UserFilm> getUserFilm() {
        return userFilm;
    }

    public void setUserFilm(List<UserFilm> userFilm) {
        this.userFilm = userFilm;
    }
}
