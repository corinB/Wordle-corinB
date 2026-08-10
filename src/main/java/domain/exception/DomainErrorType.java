package domain.exception;

import java.util.function.Function;

public enum DomainErrorType {

  INVALID_WORD(
    "5글자 영어 알파벳만 입력할 수 있습니다.",
    InvalidWordException::new
  ),
  GAME_ALREADY_FINISHED(
    "끝난 게임입니다.",
    GameAlreadyFinishedException::new
  ),
  GAME_NOT_FINISHED(
    "아직 끝나지 않은 게임입니다.",
    GameNotFinishedException::new
  ),
  INVALID_TRY_COUNT(
    "시도 횟수가 올바르지 않습니다.",
    InvalidTryCountException::new
  );

  private final String message;
  private final Function<String, RuntimeException> exceptionFactory;

  DomainErrorType(String message, Function<String, RuntimeException> exceptionFactory) {
    this.message = message;
    this.exceptionFactory = exceptionFactory;
  }

  // 에러 메시지와 예외 생성을 enum에서 함께 관리한다.
  public RuntimeException createException() {
    return exceptionFactory.apply(message);
  }

  public String getMessage() {
    return message;
  }
}
