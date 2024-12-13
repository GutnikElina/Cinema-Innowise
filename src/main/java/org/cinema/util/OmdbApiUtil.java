package org.cinema.util;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.model.MovieAPI;
import org.cinema.util.ValidationUtil;

/**
 * Utility class for interacting with the OMDB API.
 * Provides methods to fetch and search movie details.
 * Uses OMDB API for retrieving data in JSON format and parses it into {@link MovieAPI} objects.
 */
@Slf4j
@NoArgsConstructor
public class OmdbApiUtil {
    private static final Properties properties = new Properties();
    private static final String API_KEY;
    private static final String BASE_URL;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    static {
        try {
            properties.load(OmdbApiUtil.class.getClassLoader().getResourceAsStream("application.properties"));
            API_KEY = properties.getProperty("omdb.api.key");
            BASE_URL = properties.getProperty("omdb.api.url");
        } catch (IOException e) {
            log.error("Failed to load application.properties: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static List<MovieAPI> searchMovies(String title) {
        log.debug("Starting movie search for title: {}", title);
        ValidationUtil.validateTitle(title);
        
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlString = buildUrl("s", encodedTitle);
            
            String response = fetchApiResponse(urlString);
            return parseSearchResponse(response, title);
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
        ValidationUtil.validateNotBlank(movieId, "Movie ID");
        
        try {
            log.debug("Fetching movie details for ID: {}", movieId);
            String urlString = buildUrl("i", movieId);
            String response = fetchApiResponse(urlString);

            return parseMovieResponse(response, movieId);
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
        ValidationUtil.validateNotBlank(urlString, "URL");
        log.debug("Sending API request to URL: {}", urlString);
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new OmdbApiException("API request failed with status code: " + response.statusCode());
            }
            
            return response.body();
        } catch (Exception e) {
            log.error("Failed to fetch API response from URL: {}", urlString);
            throw new OmdbApiException("Failed to fetch data from OMDB API", e);
        }
    }

    private static String buildUrl(String paramName, String paramValue) {
        ValidationUtil.validateNotBlank(paramName, "Parameter name");
        ValidationUtil.validateNotBlank(paramValue, "Parameter value");
        return String.format("%s?%s=%s&apikey=%s", BASE_URL, paramName, paramValue, API_KEY);
    }

    private static List<MovieAPI> parseSearchResponse(String response, String title) throws IOException {
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
    }

    private static MovieAPI parseMovieResponse(String response, String movieId) throws IOException {
        MovieAPI movie = objectMapper.readValue(response, MovieAPI.class);
        if (movie != null && "True".equalsIgnoreCase(movie.getResponse())) {
            log.info("Movie details retrieved for ID: {}", movieId);
            return movie;
        } else {
            log.warn("No movie details found for ID: {}", movieId);
            throw new NoDataFoundException("Movie details not found for ID: " + movieId);
        }
    }
}
