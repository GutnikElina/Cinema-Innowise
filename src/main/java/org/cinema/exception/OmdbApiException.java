package org.cinema.exception;

/**
 * Exception thrown for errors related to the OMDB API.
 */
public class OmdbApiException extends RuntimeException {
  public OmdbApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public OmdbApiException(String message) {
    super(message);
  }
}
