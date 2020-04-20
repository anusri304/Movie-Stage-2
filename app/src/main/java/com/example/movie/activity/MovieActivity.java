package com.example.movie.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.movie.R;
import com.example.movie.activity.bean.MoviePresentationBean;
import com.example.movie.activity.viewmodel.MovieViewModel;
import com.example.movie.activity.viewmodel.MovieViewModelFactory;
import com.example.movie.adapter.MovieRecyclerViewAdapter;
import com.example.movie.databinding.ActivityMainBinding;
import com.example.movie.domain.Movie;
import com.example.movie.utils.ApplicationConstants;
import com.example.movie.utils.JsonUtils;
import com.example.movie.utils.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.movie.utils.ApplicationConstants.*;

public class MovieActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ListItemClickListener {

    List<MoviePresentationBean> moviePresentationBeans = new ArrayList<>();
    List<Movie> movies = new ArrayList<>();
    // Action variable to indicate whether to refresh the adapter
    static String action = "";
    MovieViewModel viewModel;
    static String tabSelected = "";
    ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (savedInstanceState == null) {
            if (NetworkUtils.isNetworkConnected(this)) {
                queryPopularMoviedb();
                queryTopRatedMoviedb();
            } else {
               showMessage(NO_INTERNET_MESSAGE);
            }
        } else {
            restorePreviousState(savedInstanceState); // Restore data found in the Bundle
        }

        MovieViewModelFactory factory = new MovieViewModelFactory((Application) getApplicationContext(), 0);
        viewModel = new ViewModelProvider(this, factory).get(MovieViewModel.class);

        viewModel.getAllFavoriteMovies().observe(this, objMovies -> {
            movies = objMovies;
            if (tabSelected.equalsIgnoreCase(getString(R.string.favorite))) {
                if (!movies.isEmpty()) {
                    setData(getMoviePresentationBeans(movies));
                } else {
                    moviePresentationBeans.clear();
                    setData(moviePresentationBeans);
                    showMessage(NO_FAVORITE_MESSAGE);
                }
            }
        });
    }

    private void queryPopularMoviedb() {
        URL moviedbPopularUrl = NetworkUtils.buildPopularUrl();
        new MoviedbPopularQueryTask().execute(moviedbPopularUrl);
    }

    private void queryTopRatedMoviedb() {
        URL moviedbRatedUrl = NetworkUtils.buildRatingUrl();
        new MoviedbPopularQueryTask().execute(moviedbRatedUrl);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        MoviePresentationBean movie = moviePresentationBeans.get(clickedItemIndex);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(ApplicationConstants.MOVIE, movie);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    public class MoviedbPopularQueryTask extends AsyncTask<URL, Void, String> {
        URL searchUrl;

        @Override
        protected String doInBackground(URL... urls) {
            searchUrl = urls[0];
            String movieDbResults = null;
            try {
                movieDbResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                if (e.getMessage() != null) {
                    //Log.d("MoviedbPopularQueryTask", e.getMessage());
                }
            }
            return movieDbResults;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // The list will be empty when popularDB endpoint is hit for the first time. We have to wait
            //for the results from both the endpoints before setting the adapter list
            if (!moviePresentationBeans.isEmpty() || tabSelected.equalsIgnoreCase(getString(R.string.most_popular)) || tabSelected.equalsIgnoreCase(getString(R.string.top_rated))) {
                action = ApplicationConstants.UPDATE_ADAPTER;
            }
            if (searchUrl.toString().contains("popular")) {
                processDataFromPopularMovieDb(s);
            } else {
                processDataFromRatedMovieDb(s);
            }
            if (action.equalsIgnoreCase(ApplicationConstants.UPDATE_ADAPTER)) {
                setData(moviePresentationBeans);
            }
        }

        private void processDataFromPopularMovieDb(String sResult) {
            if(sResult!=null)
            moviePresentationBeans.addAll(JsonUtils.parseMovieJson(sResult));
        }

        private void processDataFromRatedMovieDb(String sResult) {
            if(sResult!=null)
            moviePresentationBeans.addAll(JsonUtils.parseMovieJson(sResult));
        }
    }


    private void setData(List<MoviePresentationBean> moviePresentationBeans) {
        MovieRecyclerViewAdapter adapter = new MovieRecyclerViewAdapter(this, moviePresentationBeans, this);
        activityMainBinding.rvMovies.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        activityMainBinding.rvMovies.setLayoutManager(manager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private List<MoviePresentationBean> getMoviePresentationBeans(List<Movie> movies) {
        moviePresentationBeans.clear();
        for (Movie movie : movies) {
            MoviePresentationBean moviePresentationBean = new MoviePresentationBean();
            moviePresentationBean.setId(movie.getId());
            moviePresentationBean.setImage(movie.getImage());
            moviePresentationBean.setOverview(movie.getOverview());
            moviePresentationBean.setReleaseDate(movie.getReleaseDate());
            moviePresentationBean.setTitle(movie.getTitle());
            moviePresentationBean.setVoteAverage(movie.getVoteAverage());
            moviePresentationBean.setThumbNailImage(movie.getThumbNailImage());
            moviePresentationBeans.add(moviePresentationBean);
        }
        return moviePresentationBeans;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        if (itemSelected == R.id.action_most_popular) {
            if (NetworkUtils.isNetworkConnected(this)) {
                tabSelected = getString(R.string.most_popular);
                moviePresentationBeans.clear();
                // The adapter will be set as the action is update
                queryPopularMoviedb();
            }
            else {
                showMessage(NO_INTERNET_MESSAGE);
            }

        } else if (itemSelected == R.id.action_top_rated) {
            if (NetworkUtils.isNetworkConnected(this)) {
                tabSelected = getString(R.string.top_rated);
                moviePresentationBeans.clear();
                // The adapter will be set as the action is update
                queryTopRatedMoviedb();
            }
            else {
                showMessage(NO_INTERNET_MESSAGE);
            }
        } else if (itemSelected == R.id.action_favorite) {
            tabSelected = getString(R.string.favorite);
            if (!movies.isEmpty()) {
                setData(getMoviePresentationBeans(movies));
            } else {
                moviePresentationBeans.clear();
                setData(moviePresentationBeans);
                showMessage(NO_FAVORITE_MESSAGE);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar
                .make(activityMainBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state

        Parcelable listState = Objects.requireNonNull(activityMainBinding.rvMovies.getLayoutManager()).onSaveInstanceState();
        // putting recyclerview position
        savedInstanceState.putParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID, listState);
        // putting recyclerview items
        savedInstanceState.putParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID, new ArrayList<>(moviePresentationBeans));
        super.onSaveInstanceState(savedInstanceState);
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable listState = savedInstanceState.getParcelable(SAVED_RECYCLER_VIEW_STATUS_ID);
        // getting recyclerview items
        moviePresentationBeans = savedInstanceState.getParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID);
        // Restoring adapter items
        setData(moviePresentationBeans);
        // Restoring recycler view position
        Objects.requireNonNull(activityMainBinding.rvMovies.getLayoutManager()).onRestoreInstanceState(listState);
    }

}
