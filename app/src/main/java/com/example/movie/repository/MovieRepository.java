package com.example.movie.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.movie.dao.MovieDao;
import com.example.movie.database.MovieRoomDatabase;
import com.example.movie.domain.Movie;

import java.util.List;

@SuppressWarnings("ALL")
public class MovieRepository {
    private MovieDao movieDao;

    public MovieRepository(Application application) {
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(application);
        movieDao = db.movieDao();
    }


    public LiveData<List<Movie>> getAllFavoriteMovies(){
        return movieDao.getAllFavoriteMovies();
    }

    public void updateMovie(int movieId,boolean isFavorite){
         movieDao.updateMovie(movieId,isFavorite);
    }

    public void insertMovie(Movie movie){
        movieDao.insertMovie(movie);
    }

    public LiveData<Movie> getMovie(int movieId) {
       return  movieDao.getMovie(movieId);
    }
}
