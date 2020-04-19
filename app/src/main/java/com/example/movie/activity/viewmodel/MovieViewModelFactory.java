package com.example.movie.activity.viewmodel;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// COMPLETED (1) Make this class extend ViewModel ViewModelProvider.NewInstanceFactory
public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    // COMPLETED (2) Add two member variables. One for the database and one for the taskId
    private final Application application;

    private final int mMovieId;

    // COMPLETED (3) Initialize the member variables in the constructor with the parameters received
    public MovieViewModelFactory(Application mdB,int movieId) {
        application = mdB;
        mMovieId= movieId;
    }

    // COMPLETED (4) Uncomment the following method
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieViewModel(application,mMovieId);
    }
}
