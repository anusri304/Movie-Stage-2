package com.example.movie.activity.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.movie.domain.Movie;
import com.example.movie.repository.MovieRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private MovieRepository movieRepository;

    LiveData<List<Movie>> movies;

    LiveData<Movie> movie;

    int movieId;

    public MovieViewModel(@NonNull Application application,int mMovieId) {
        movieRepository = new MovieRepository(application);
        movieId = mMovieId;
        movies = movieRepository.getAllFavoriteMovies();
        movie = movieRepository.getMovie(movieId);
    }

    public  LiveData<List<Movie>> getAllFavoriteMovies() {
        return movies;
    }

    public void updateMovie(int movieId,boolean isFavorite){
        movieRepository.updateMovie(movieId,isFavorite);
    }

    public void insertMovie(Movie movie){
        movieRepository.insertMovie(movie);
    }

    public LiveData<Movie> getMovie(){
        return movie;
    }
}
