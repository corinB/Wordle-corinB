package infrastructure.exception;

public class WordFileException extends RuntimeException {

  public WordFileException(String message) {
    super(message);
  }

  public WordFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
