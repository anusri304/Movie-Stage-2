package com.example.movie.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.movie.R;
import com.example.movie.activity.bean.MoviePresentationBean;
import com.example.movie.activity.bean.Review;
import com.example.movie.activity.bean.Trailer;
import com.example.movie.activity.viewmodel.MovieViewModel;
import com.example.movie.activity.viewmodel.MovieViewModelFactory;
import com.example.movie.adapter.ReviewRecyclerViewAdapter;
import com.example.movie.adapter.TrailerRecyclerViewAdapter;
import com.example.movie.databinding.ActivityMovieDetailBinding;
import com.example.movie.domain.Movie;
import com.example.movie.executors.AppExecutors;
import com.example.movie.utils.ApplicationConstants;
import com.example.movie.utils.JsonUtils;
import com.example.movie.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.movie.utils.ApplicationConstants.*;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {
    MoviePresentationBean movie = null;
    MovieViewModel viewModel;

    private static final int TRAILER_SEARCH_LOADER = 22;
    private static final int REVIEW_SEARCH_LOADER = 23;

    private static final String TRAILER_SEARCH_QUERY_URL_EXTRA = "trailerquery";
    private static final String REVIEW_SEARCH_QUERY_URL_EXTRA = "reviewquery";

    ActivityMovieDetailBinding activityMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        movie = getIntent().getParcelableExtra(ApplicationConstants.MOVIE);
        if (savedInstanceState == null) {
            if (NetworkUtils.isNetworkConnected(this)) {
                queryTrailer(movie);
                queryReview(movie);
            } else {
                Toast.makeText(this, NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        } else {
            restorePreviousState(savedInstanceState); // Restore data found in the Bundle
        }
        setTitle(Objects.requireNonNull(movie).getTitle());

        LoaderManager.getInstance(this).initLoader(TRAILER_SEARCH_LOADER, null, this);
        LoaderManager.getInstance(this).initLoader(REVIEW_SEARCH_LOADER, null, this);
        initButton(movie);
        populateUI(movie);

    }

    private void initButton(MoviePresentationBean movie) {
        MovieViewModelFactory factory = new MovieViewModelFactory((Application) getApplicationContext(), movie.getId());
        viewModel
                = new ViewModelProvider(this, factory).get(MovieViewModel.class);


        viewModel.getMovie().observe(this, movie1 -> {
            if (movie1 != null) {
                activityMovieDetailBinding.toggleButton.setChecked(movie1.isFavourite());
            }
        });
        activityMovieDetailBinding.toggleButton.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                insertOrUpdateMovie(movie.getId(), true);
            } else {
                insertOrUpdateMovie(movie.getId(), false);
            }
        });
    }


    private void updateMovie(int movieId, boolean isFavorite) {
        AppExecutors.getInstance().diskIO().execute(() -> viewModel.updateMovie(movieId, isFavorite));

    }


    private void insertOrUpdateMovie(int movieId, boolean isFavorite) {

        AppExecutors.getInstance().diskIO().execute(() -> {
            LiveData<Movie> objMovie = viewModel.getMovie();
            if (objMovie.getValue() == null) {
                saveMovie(movie);
            } else {
                updateMovie(movieId, isFavorite);
            }
        });


    }

    private void saveMovie(MoviePresentationBean moviePresentationBean) {
        try {

            Movie movie = new Movie();
            movie.setId(moviePresentationBean.getId());
            movie.setTitle(moviePresentationBean.getTitle());
            AppExecutors.getInstance().diskIO().execute(() -> {
                // When movie is liked for first time it will be in favourite
                movie.setFavourite(true);
                movie.setImage(moviePresentationBean.getImage());
                movie.setThumbNailImage(moviePresentationBean.getThumbNailImage());
                movie.setOverview(moviePresentationBean.getOverview());
                movie.setReleaseDate(moviePresentationBean.getReleaseDate());
                movie.setVoteAverage(moviePresentationBean.getVoteAverage());
                viewModel.insertMovie(movie);
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTrailerData(List<Trailer> trailers) {
        TrailerRecyclerViewAdapter adapter = new TrailerRecyclerViewAdapter(this, trailers);
        activityMovieDetailBinding.rvTrailers.setAdapter(adapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activityMovieDetailBinding.rvTrailers.setLayoutManager(layoutManager);

    }

    private void setReviewData(List<Review> reviews) {
        ReviewRecyclerViewAdapter adapter = new ReviewRecyclerViewAdapter(this, reviews);
        activityMovieDetailBinding.rvReviews.setAdapter(adapter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activityMovieDetailBinding.rvReviews.setLayoutManager(layoutManager);

    }

    private void queryTrailer(MoviePresentationBean movie) {
        URL trailerUrl = NetworkUtils.buildTrailerUrl(movie.getId());
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<String> trailerSearchLoader = loaderManager.getLoader(TRAILER_SEARCH_LOADER);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(TRAILER_SEARCH_QUERY_URL_EXTRA, trailerUrl.toString());
        if (trailerSearchLoader == null) {
            loaderManager.initLoader(TRAILER_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(TRAILER_SEARCH_LOADER, queryBundle, this);
        }
    }

    private void queryReview(MoviePresentationBean movie) {
        URL reviewUrl = NetworkUtils.buildReviewUrl(movie.getId());
        Loader<String> reviewSearchLoader = LoaderManager.getInstance(this).getLoader(REVIEW_SEARCH_LOADER);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(REVIEW_SEARCH_QUERY_URL_EXTRA, reviewUrl.toString());
        if (reviewSearchLoader == null) {
            LoaderManager.getInstance(this).initLoader(REVIEW_SEARCH_LOADER, queryBundle, this);
        } else {
            LoaderManager.getInstance(this).restartLoader(REVIEW_SEARCH_LOADER, queryBundle, this);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(MoviePresentationBean movie) {
        Glide.with(this)
                .load(movie.getThumbNailImage())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(activityMovieDetailBinding.imageIv);
        activityMovieDetailBinding.plotSynopsis.setText(movie.getOverview());
        activityMovieDetailBinding.userRating.setText(String.valueOf(movie.getVoteAverage()).concat("/10"));
        activityMovieDetailBinding.releaseDate.setText(movie.getReleaseDate());

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            // COMPLETED (5) Override onStartLoading
            @Override
            protected void onStartLoading() {

                // COMPLETED (6) If args is null, return.
                /* If no arguments were passed, we don't have a query to perform. Simply return. */
                if (args == null) {
                    return;
                }

                // COMPLETED (7) Show the loading indicator
                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */
                activityMovieDetailBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);

                // COMPLETED (8) Force a load
                forceLoad();
            }

            // COMPLETED (9) Override loadInBackground
            @Override
            public String loadInBackground() {
                String searchQueryUrlString;

                // COMPLETED (10) Get the String for our URL from the bundle passed to onCreateLoader
                /* Extract the search query from the args using our constant */
                if (args.getString(TRAILER_SEARCH_QUERY_URL_EXTRA) != null) {
                    searchQueryUrlString = args.getString(TRAILER_SEARCH_QUERY_URL_EXTRA);
                } else {
                    searchQueryUrlString = args.getString(REVIEW_SEARCH_QUERY_URL_EXTRA);
                }

                // COMPLETED (11) If the URL is null or empty, return null
                /* If the user didn't enter anything, there's nothing to search for */
                if (TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                // COMPLETED (12) Copy the try / catch block from the AsyncTask's doInBackground method
                /* Parse the URL from the passed in String and perform the search */
                try {
                    URL url = new URL(searchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        activityMovieDetailBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        if (null == data) {
            Toast.makeText(this, NO_DATA_MESSAGE, Toast.LENGTH_SHORT).show();
        } else {
            switch (loader.getId()) {
                case 22:
                    List<Trailer> trailers = JsonUtils.parseTrailerJson(data);
                    trailers = trailers.stream().filter(trailer -> trailer.getType().equalsIgnoreCase("Trailer")).collect(Collectors.toList());
                    setTrailerData(trailers);
                    break;
                case 23:
                    List<Review> reviews = JsonUtils.parseReviewJson(data);
                    setReviewData(reviews);
                    break;

            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(SAVED_MOVIE, movie);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        movie = savedInstanceState.getParcelable(SAVED_MOVIE);
        populateUI(movie);
    }
}
