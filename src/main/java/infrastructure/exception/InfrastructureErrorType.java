package infrastructure.exception;

import lombok.Getter;

import java.util.function.Function;

public enum InfrastructureErrorType {

  WORD_FILE_NOT_FOUND("단어 파일을 찾을 수 없습니다: %s", WordFileException::new),
  WORD_FILE_READ_FAILED("단어 파일을 읽지 못했습니다.", WordFileException::new),
  PLAYER_ENTITY_NOT_FOUND("플레이어 엔티티를 찾을 수 없습니다.", IllegalArgumentException::new),
  WORDLE_GAME_ENTITY_NOT_FOUND("워들 게임 엔티티를 찾을 수 없습니다.", IllegalArgumentException::new),
  CORRECT_WORD_ENTITY_NOT_FOUND("정답 단어 엔티티를 찾을 수 없습니다.", IllegalArgumentException::new);

  @Getter
  private final String message;
  private final Function<String, RuntimeException> exceptionFactory;

  InfrastructureErrorType(String message, Function<String, RuntimeException> exceptionFactory) {
    this.message = message;
    this.exceptionFactory = exceptionFactory;
  }

  public RuntimeException createException() {
    return exceptionFactory.apply(message);
  }

  public WordFileException createException(String value) {
    return new WordFileException(String.format(message, value));
  }

  public WordFileException createException(Throwable cause) {
    return new WordFileException(message, cause);
  }
}
