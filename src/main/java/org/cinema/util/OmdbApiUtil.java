package org.cinema.util;

import org.cinema.model.Movie;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OmdbApiUtil {
    private static final String API_KEY = "35345cc8";
    private static final String BASE_URL = "http://www.omdbapi.com/";

    public static Movie getMovie(String title) {
        try {
            String urlString = BASE_URL + "?t=" + title + "&apikey=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Movie movie = new Gson().fromJson(reader, Movie.class);

            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                return movie;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
