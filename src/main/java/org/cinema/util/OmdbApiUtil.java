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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.NoDataFoundException;
import org.cinema.error.OmdbApiException;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Movie getMovie(String title) {
        log.debug("Fetching movie details for title: {}", title);
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?t=" + encodedTitle + "&apikey=" + API_KEY;
            log.debug("Constructed URL for movie lookup: {}", urlString);

            String response = fetchApiResponse(urlString);
            log.debug("Response received for movie lookup: {}", response);

            Movie movie = objectMapper.readValue(response, Movie.class);
            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                log.info("Movie found: {}", movie.getTitle());
                return movie;
            } else {
                throw new NoDataFoundException("Movie not found with the given title: " + title);
            }
        } catch (NoDataFoundException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error while parsing response for title: {}", title);
            throw new OmdbApiException("Failed to process movie data for title: " + title, e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching movie details for title: {}", title);
            throw new OmdbApiException("Unexpected error occurred while fetching movie details", e);
        }
    }

    public static List<Movie> searchMovies(String title) {
        log.debug("Starting movie search for title: {}", title);
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?s=" + encodedTitle + "&apikey=" + API_KEY;
            log.debug("Constructed URL for movie search: {}", urlString);

            String response = fetchApiResponse(urlString);
            log.debug("Response received for movie search: {}", response);

            JsonNode jsonResponse = objectMapper.readTree(response);
            if ("True".equalsIgnoreCase(jsonResponse.get("Response").asText())) {
                JsonNode searchResults = jsonResponse.get("Search");
                List<Movie> movieList = new ArrayList<>();

                for (JsonNode node : searchResults) {
                    Movie movie = getMovieDetails(node.get("imdbID").asText());
                    movieList.add(movie);
                }

                log.info("Movie search completed. Total movies found: {}", movieList.size());
                return movieList;
            } else {
                throw new NoDataFoundException("No movies found for the given title: " + title);
            }
        } catch (NoDataFoundException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error parsing JSON response for search title: {}", title);
            throw new OmdbApiException("Failed to process movie search data for title: " + title, e);
        } catch (Exception e) {
            log.error("Unexpected error while searching for movies for title: {}", title);
            throw new OmdbApiException("Unexpected error occurred while searching for movies", e);
        }
    }

    private static Movie getMovieDetails(String movieId) {
        try {
            log.debug("Fetching movie details for ID: {}", movieId);
            String urlString = BASE_URL + "?i=" + movieId + "&apikey=" + API_KEY;
            String response = fetchApiResponse(urlString);

            Movie movie = objectMapper.readValue(response, Movie.class);
            if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
                log.info("Movie details retrieved for ID: {}", movieId);
                return movie;
            } else {
                log.warn("No movie details found for ID: {}", movieId);
                throw new NoDataFoundException("Movie details not found for ID: " + movieId);
            }
        } catch (NoDataFoundException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error parsing JSON response for movie ID: {}", movieId, e);
            throw new OmdbApiException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching movie details for ID: {}", movieId, e);
            throw new OmdbApiException("Unexpected error occurred while fetching movie details", e);
        }
    }

    private static String fetchApiResponse(String urlString) {
        log.debug("Sending API request to URL: {}", urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            log.debug("Received HTTP response code: {}", responseCode);

            if (responseCode != 200) {
                throw new OmdbApiException("OMDB API returned error: HTTP " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                return responseBuilder.toString();
            }
        } catch (MalformedURLException e) {
            log.error("Invalid URL format: {}", urlString, e);
            throw new OmdbApiException("Invalid URL for OMDB API request", e);
        } catch (IOException e) {
            log.error("IO error while communicating with OMDB API: {}", urlString, e);
            throw new OmdbApiException("Error fetching response from OMDB API", e);
        }
    }
}
