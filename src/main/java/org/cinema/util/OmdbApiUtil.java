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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
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

    /**
     * Searches for movies by title using the OMDB API.
     * Sends a request to the OMDB API, parses the results into {@link MovieAPI} objects.
     * Throws exceptions if no data is found or an error occurs.
     *
     * @param title the movie title to search for
     * @return a list of {@link MovieAPI} objects with search results
     * @throws OmdbApiException if there's an error during the request or data processing
     * @throws NoDataFoundException if no movies are found for the given title
     */
    public static List<MovieAPI> searchMovies(String title) {
        log.debug("Starting movie search for title: {}", title);
        ValidationUtil.validateNotBlank(title, "movie_title");

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

    /**
     * Fetches movie details from the OMDB API using the provided movie ID.
     *
     * @param movieId the ID of the movie to fetch details for
     * @return the {@link MovieAPI} object containing movie details
     * @throws NoDataFoundException if no data is found for the given movie ID
     * @throws OmdbApiException if an error occurs while fetching or parsing the movie details
     */
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

    /**
     * Sends an HTTP request to the OMDB API to fetch the response from the given URL.
     *
     * @param urlString the URL to send the GET request to
     * @return the response body as a string
     * @throws OmdbApiException if the API request fails or the response status code is not 200
     */
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

    /**
     * Builds the URL for the OMDB API request with the specified parameter name and value.
     *
     * @param paramName the name of the parameter (e.g., "s" for search, "i" for movie details)
     * @param paramValue the value of the parameter (e.g., movie title or ID)
     * @return the formatted URL string for the API request
     * @throws IllegalArgumentException if the parameter name or value is blank
     */
    private static String buildUrl(String paramName, String paramValue) {
        ValidationUtil.validateNotBlank(paramName, "Parameter name");
        ValidationUtil.validateNotBlank(paramValue, "Parameter value");
        return String.format("%s?%s=%s&apikey=%s", BASE_URL, paramName, paramValue, API_KEY);
    }

    /**
     * Parses the JSON response from the search API and converts it into a list of {@link MovieAPI} objects.
     *
     * @param response the JSON response body as a string
     * @param title the title of the movie being searched
     * @return a list of {@link MovieAPI} objects corresponding to the search results
     * @throws IOException if an error occurs during JSON parsing
     * @throws NoDataFoundException if no search results are found
     */
    private static List<MovieAPI> parseSearchResponse(String response, String title) throws IOException {
        JsonNode jsonResponse = objectMapper.readTree(response);
        if ("True".equalsIgnoreCase(jsonResponse.get("Response").asText())) {
            JsonNode searchResults = jsonResponse.get("Search");
            
            List<CompletableFuture<MovieAPI>> futures = new ArrayList<>();
            
            for (JsonNode node : searchResults) {
                CompletableFuture<MovieAPI> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return getMovieDetails(node.get("imdbID").asText());
                    } catch (Exception e) {
                        log.error("Error fetching details for movie ID: {}", node.get("imdbID").asText(), e);
                        throw new CompletionException(e);
                    }
                });
                futures.add(future);
            }

            List<MovieAPI> movieList = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            log.info("Movie search completed. Total movies found: {}", movieList.size());
            return movieList;
        } else {
            throw new NoDataFoundException("No movies found for the given title: " + title);
        }
    }

    /**
     * Parses the JSON response for a single movie and converts it into a {@link MovieAPI} object.
     *
     * @param response the JSON response body as a string
     * @param movieId the ID of the movie being parsed
     * @return the {@link MovieAPI} object representing the movie details
     * @throws IOException if an error occurs during JSON parsing
     * @throws NoDataFoundException if no details are found for the movie ID
     */
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
