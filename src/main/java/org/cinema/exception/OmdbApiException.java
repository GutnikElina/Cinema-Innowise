package org.cinema.exception;

public class OmdbApiException extends RuntimeException {
  public OmdbApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public OmdbApiException(String message) {
    super(message);
  }
}
