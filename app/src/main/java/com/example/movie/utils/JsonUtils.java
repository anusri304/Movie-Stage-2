package com.example.movie.utils;

import android.util.Log;
import com.example.movie.activity.bean.MoviePresentationBean;
import com.example.movie.activity.bean.Review;
import com.example.movie.activity.bean.Trailer;
import com.example.movie.transport.ReviewTransportBean;
import com.example.movie.transport.TrailerTransportBean;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<MoviePresentationBean> parseMovieJson(String json) {
        List<MoviePresentationBean> movies = new ArrayList<>();
        MoviePresentationBean movie;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (json != null) {
                JSONObject obj = new JSONObject(json);
                JSONArray results = obj.optJSONArray("results");
                if (results != null) {
                    for (int i = 0; i < results.length(); i++) {
                        movie = new MoviePresentationBean();
                        JSONObject jsonObject = results.getJSONObject(i);
                        String posterPath = jsonObject.optString(ApplicationConstants.POSTER_PATH);
                        String movieTitle = jsonObject.optString(ApplicationConstants.TITLE);
                        int id = jsonObject.optInt(ApplicationConstants.ID);
                        String overview = jsonObject.optString(ApplicationConstants.OVERVIEW);
                        double voteAverage = jsonObject.optDouble(ApplicationConstants.VOTE_AVERAGE);
                        String releaseDate = jsonObject.optString(ApplicationConstants.RELEASE_DATE);
                        if (!releaseDate.equals("")) {
                            releaseDate = releaseDate.substring(0, 4);
                        }
                        stringBuilder.append(ApplicationConstants.IMAGE_URL_PREFIX).append(posterPath);
                        movie.setId(id);
                        movie.setImage(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        stringBuilder.append(ApplicationConstants.IMAGE_URL_THUMBNAIL_PREFIX).append(posterPath);

                        movie.setThumbNailImage(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        movie.setTitle(movieTitle);
                        movie.setOverview(overview);
                        movie.setVoteAverage(voteAverage);
                        movie.setReleaseDate(releaseDate);
                        stringBuilder.delete(0, stringBuilder.length());
                        movies.add(movie);
                    }
                }
            }
        } catch (JSONException e) {
            Log.i("Exception", "JSON Exception" + Objects.requireNonNull(e.getMessage()));
        }
        return movies;
    }

    public static List<Trailer> parseTrailerJson(String json) {
        Gson gson = new Gson();
        TrailerTransportBean movieTransportBean = gson.fromJson(json, TrailerTransportBean.class);
        return movieTransportBean.getTrailers();

    }

    public static List<Review> parseReviewJson(String json) {
        Gson gson = new Gson();
        ReviewTransportBean reviewTransportBean = gson.fromJson(json, ReviewTransportBean.class);
        return reviewTransportBean.getReviews();

    }
}
