/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.movie.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private NetworkUtils() {

    }

    /**
     * Builds the URL used to query moviedb
     * <p>
     * * @return The URL to use to query moviedb
     */
    public static URL buildPopularUrl() {
        String movieDbPopularUrl = "http://api.themoviedb.org/3/movie/popular?[API_KEY]";
        return getUrl(movieDbPopularUrl);
    }

    public static URL buildTrailerUrl(int movieId) {
        //TODO: define constant
        String trailerUrl = "http://api.themoviedb.org/3/movie/"+movieId+"/videos?[API_KEY]";
        return getUrl(trailerUrl);
    }

    private static URL getUrl(String queryUrl) {
        Uri uri = Uri.parse(queryUrl).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            if (e.getMessage() != null) {
               // Log.d("buildPopularUrl", "#" + e.getMessage());
            }
        }
        return url;
    }

    public static URL buildReviewUrl(int movieId) {
        //TODO: define constant
        String reviewUrl = "http://api.themoviedb.org/3/movie/"+movieId+"/reviews?api_key=[API_KEY]";
        return getUrl(reviewUrl);
    }


    public static URL buildRatingUrl() {
        String movieDbRatingUrl = "http://api.themoviedb.org/3/movie/top_rated?api_key=[API_KEY]";
        return getUrl(movieDbRatingUrl);
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Network n = Objects.requireNonNull(cm).getActiveNetwork();
        if (n != null) {
            final NetworkCapabilities nc = cm.getNetworkCapabilities(n);
            return (Objects.requireNonNull(nc).hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        }
        return false;
    }

}
