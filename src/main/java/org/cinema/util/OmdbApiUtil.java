package org.cinema.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Movie;

@Slf4j
public class OmdbApiUtil {

    private static final String API_KEY = "35345cc8";
    private static final String BASE_URL = "https://www.omdbapi.com/";

    public static Movie getMovie(String title) {
        try {
            log.debug("Fetching movie details for title: {}", title);
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?t=" + encodedTitle + "&apikey=" + API_KEY;
            String response = fetchApiResponse(urlString);

            Movie movie = new Gson().fromJson(response, Movie.class);
            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                log.info("Movie found: {}", movie.getTitle());
                return movie;
            } else {
                log.warn("No movie found for title: {}", title);
                throw new IllegalArgumentException("Movie not found with the given title: " + title);
            }
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON response for movie title: {}", title);
            throw new RuntimeException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching movie details: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching movie details", e);
        }
    }

    public static List<Movie> searchMovies(String title) {
        List<Movie> movieList = new ArrayList<>();
        try {
            log.debug("Searching movies for title: {}", title);
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?s=" + encodedTitle + "&apikey=" + API_KEY;
            String response = fetchApiResponse(urlString);

            JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
            if ("True".equalsIgnoreCase(jsonResponse.get("Response").getAsString())) {
                JsonArray searchResults = jsonResponse.getAsJsonArray("Search");

                for (int i = 0; i < searchResults.size(); i++) {
                    JsonObject movieObject = searchResults.get(i).getAsJsonObject();
                    String movieId = movieObject.get("imdbID").getAsString();

                    Movie movie = getMovieDetails(movieId);
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
            } else {
                log.warn("No movies found for search query: {}", title);
                throw new IllegalArgumentException("No movies found for the given title: " + title);
            }
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON response for movie search: {}", title);
            throw new RuntimeException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while searching movies: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while searching for movies", e);
        }
        return movieList;
    }

    private static Movie getMovieDetails(String movieId) {
        try {
            log.debug("Fetching movie details for ID: {}", movieId);
            String urlString = BASE_URL + "?i=" + movieId + "&apikey=" + API_KEY;
            String response = fetchApiResponse(urlString);

            Movie movie = new Gson().fromJson(response, Movie.class);
            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                log.info("Movie details retrieved for ID: {}", movieId);
                return movie;
            } else {
                log.warn("No movie details found for ID: {}", movieId);
                throw new IllegalArgumentException("Movie details not found for ID: " + movieId);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid input or movie details not found: {}", e.getMessage());
            throw e;
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON response for movie ID: {}", movieId);
            throw new RuntimeException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching movie details: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching movie details", e);
        }
    }

    private static String fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("OMDB API returned error: HTTP " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                log.debug("API response successfully retrieved.");
                return responseBuilder.toString();
            }
        } catch (MalformedURLException e) {
            log.error("Invalid URL format: {}", urlString);
            throw new RuntimeException("Error forming URL for OMDB API request", e);
        } catch (IOException e) {
            log.error("IO error while communicating with OMDB API: {}", urlString);
            throw new RuntimeException("Error fetching response from OMDB API", e);
        }
    }
}
