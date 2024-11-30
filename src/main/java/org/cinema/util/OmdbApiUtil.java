package org.cinema.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import org.cinema.model.Movie;

public class OmdbApiUtil {
    private static final String API_KEY = "35345cc8";
    private static final String BASE_URL = "https://www.omdbapi.com/";

    public static Movie getMovie(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?t=" + encodedTitle + "&apikey=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            String response = responseBuilder.toString();
            Movie movie = new Gson().fromJson(response, Movie.class);

            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                return movie;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
