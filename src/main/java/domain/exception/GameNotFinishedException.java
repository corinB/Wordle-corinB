package domain.exception;

public class GameNotFinishedException extends RuntimeException {

  public GameNotFinishedException(String message) {
    super(message);
  }
}
