package org.cinema.util;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.model.MovieAPI;

/**
 * Utility class for interacting with the OMDB API.
 * Provides methods to fetch and search movie details.
 * Uses OMDB API for retrieving data in JSON format and parses it into {@link MovieAPI} objects.
 */
@Slf4j
public class OmdbApiUtil {

    private static final String API_KEY = "35345cc8";
    private static final String BASE_URL = "https://www.omdbapi.com/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static List<MovieAPI> searchMovies(String title) {
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
                List<MovieAPI> movieList = new ArrayList<>();

                for (JsonNode node : searchResults) {
                    MovieAPI movie = getMovieDetails(node.get("imdbID").asText());
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

    private static MovieAPI getMovieDetails(String movieId) {
        try {
            log.debug("Fetching movie details for ID: {}", movieId);
            String urlString = BASE_URL + "?i=" + movieId + "&apikey=" + API_KEY;
            String response = fetchApiResponse(urlString);

            MovieAPI movie = objectMapper.readValue(response, MovieAPI.class);
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
            log.error("Error parsing JSON response for movie ID: {}", movieId);
            throw new OmdbApiException("Error parsing response from OMDB API", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching movie details for ID: {}", movieId);
            throw new OmdbApiException("Unexpected error occurred while fetching movie details", e);
        }
    }

    private static String fetchApiResponse(String urlString) {
        log.debug("Sending API request to URL: {}", urlString);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new OmdbApiException("OMDB API returned error: HTTP " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            log.error("Error while fetching response from OMDB API: {}", urlString);
            throw new OmdbApiException("Error fetching response from OMDB API", e);
        }
    }
}
