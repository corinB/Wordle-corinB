package domain.exception;

public class InvalidTryCountException extends RuntimeException {

  public InvalidTryCountException(String message) {
    super(message);
  }
}
