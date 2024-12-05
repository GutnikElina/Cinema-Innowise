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

/**
 * Utility class for interacting with the OMDB API.
 * Provides methods to fetch and search movie details.
 * Uses OMDB API for retrieving data in JSON format and parses it into {@link Movie} objects.
 */
@Slf4j
public class OmdbApiUtil {

    private static final String API_KEY = "35345cc8";
    private static final String BASE_URL = "https://www.omdbapi.com/";

    /**
     * Fetches movie details based on the given title.
     *
     * @param title the title of the movie to fetch details for.
     * @return a {@link Movie} object containing the details of the movie.
     * @throws IllegalArgumentException if no movie is found with the given title.
     * @throws RuntimeException if there is an error communicating with the OMDB API or parsing the response.
     */
    public static Movie getMovie(String title) {
        log.debug("Fetching movie details for title: {}", title);
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?t=" + encodedTitle + "&apikey=" + API_KEY;
            log.debug("Constructed URL for movie lookup: {}", urlString);

            String response = fetchApiResponse(urlString);
            log.debug("Response received for movie lookup: {}", response);

            Movie movie = new Gson().fromJson(response, Movie.class);
            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                log.info("Movie found: {}", movie.getTitle());
                return movie;
            } else {
                log.warn("No movie found for title: {}", title);
                throw new IllegalArgumentException("Movie not found with the given title: " + title);
            }
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON response for movie title: {}", title, e);
            throw new RuntimeException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching movie details for title: {}", title, e);
            throw new RuntimeException("Unexpected error occurred while fetching movie details", e);
        }
    }

    /**
     * Searches for movies based on the given title.
     * Returns a list of movies that match the search query.
     *
     * @param title the search query for the movie titles.
     * @return a {@link List} of {@link Movie} objects matching the search query.
     * @throws IllegalArgumentException if no movies are found for the given title.
     * @throws RuntimeException if there is an error communicating with the OMDB API or parsing the response.
     */
    public static List<Movie> searchMovies(String title) {
        log.debug("Starting movie search for title: {}", title);
        List<Movie> movieList = new ArrayList<>();
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?s=" + encodedTitle + "&apikey=" + API_KEY;
            log.debug("Constructed URL for movie search: {}", urlString);

            String response = fetchApiResponse(urlString);
            log.debug("Response received for movie search: {}", response);

            JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
            if ("True".equalsIgnoreCase(jsonResponse.get("Response").getAsString())) {
                JsonArray searchResults = jsonResponse.getAsJsonArray("Search");
                log.info("Number of movies found: {}", searchResults.size());

                for (int i = 0; i < searchResults.size(); i++) {
                    JsonObject movieObject = searchResults.get(i).getAsJsonObject();
                    String movieId = movieObject.get("imdbID").getAsString();
                    log.debug("Fetching details for movie ID: {}", movieId);

                    Movie movie = getMovieDetails(movieId);
                    if (movie != null) {
                        movieList.add(movie);
                        log.debug("Movie added to list: {}", movie.getTitle());
                    }
                }
            } else {
                log.warn("No movies found for search query: {}", title);
                throw new IllegalArgumentException("No movies found for the given title: " + title);
            }
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON response for movie search: {}", title, e);
            throw new RuntimeException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while searching movies for title: {}", title, e);
            throw new RuntimeException("Unexpected error occurred while searching for movies", e);
        }
        log.info("Movie search completed. Total movies found: {}", movieList.size());
        return movieList;
    }

    /**
     * Fetches detailed movie information using the movie's IMDb ID.
     *
     * @param movieId the IMDb ID of the movie to fetch details for.
     * @return a {@link Movie} object containing detailed information about the movie.
     * @throws IllegalArgumentException if the movie details are not found or input is invalid.
     * @throws RuntimeException if there is an error communicating with the OMDB API or parsing the response.
     */
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

    /**
     * Sends a GET request to the given URL and retrieves the API response.
     * Handles connection setup, response code checking, and reading the response content.
     *
     * @param urlString the URL to send the API request to.
     * @return the raw response as a {@link String}.
     * @throws RuntimeException if the URL is malformed, the API returns an error response,
     *                          or there is an error in reading the response.
     */
    private static String fetchApiResponse(String urlString) {
        log.debug("Sending API request to URL: {}", urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            log.debug("Received HTTP response code: {}", responseCode);

            if (responseCode != 200) {
                log.error("OMDB API returned error: HTTP {}", responseCode);
                throw new IOException("OMDB API returned error: HTTP " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                log.info("API response successfully retrieved from URL: {}", urlString);
                log.debug("API response content: {}", responseBuilder);
                return responseBuilder.toString();
            }
        } catch (MalformedURLException e) {
            log.error("Invalid URL format: {}", urlString, e);
            throw new RuntimeException("Error forming URL for OMDB API request", e);
        } catch (IOException e) {
            log.error("IO error while communicating with OMDB API: {}", urlString, e);
            throw new RuntimeException("Error fetching response from OMDB API", e);
        }
    }
}
