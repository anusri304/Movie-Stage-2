package com.example.movie.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.movie.activity.bean.MoviePresentationBean;
import com.example.movie.domain.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Delete
    void delete(Movie movie);

    @Query("SELECT * from Movie where isFavourite =1")
    LiveData<List<Movie>>  getAllFavoriteMovies();

    @Query("update Movie set isFavourite=:isFavorite where  id=:movieId")
    void updateMovie(int movieId,boolean isFavorite);

    @Insert
    void insertMovie(Movie movie);

    @Query("SELECT * from Movie where id=:movieId")
    LiveData<Movie> getMovie(int movieId);
}
