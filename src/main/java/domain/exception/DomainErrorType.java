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
  GAME_REQUIRED(
    "게임은 필수입니다.",
    IllegalArgumentException::new
  ),
  PLAYER_REQUIRED(
    "플레이어는 필수입니다.",
    IllegalArgumentException::new
  ),
  PLAYER_ID_REQUIRED(
    "플레이어 ID는 필수입니다.",
    IllegalArgumentException::new
  ),
  WORDLE_GAME_ID_REQUIRED(
    "워들 게임 ID는 필수입니다.",
    IllegalArgumentException::new
  ),
  CORRECT_WORD_ID_REQUIRED(
    "정답 단어 ID는 필수입니다.",
    IllegalArgumentException::new
  ),
  CORRECT_WORD_REQUIRED(
    "정답 단어는 필수입니다.",
    IllegalArgumentException::new
  ),
  GAME_END_TIME_REQUIRED(
    "게임 종료 시각은 필수입니다.",
    IllegalArgumentException::new
  ),
  ANSWER_REQUIRED(
    "답안은 필수입니다.",
    IllegalArgumentException::new
  ),
  CURRENT_TIME_REQUIRED(
    "현재 시간은 필수입니다.",
    IllegalArgumentException::new
  ),
  INVALID_TRY_COUNT(
    "시도 횟수가 올바르지 않습니다.",
    InvalidTryCountException::new
  ),
  NICKNAME_REQUIRED(
    "닉네임은 필수입니다.",
    IllegalArgumentException::new
  ),
  INVALID_NICKNAME_LENGTH(
    "닉네임은 2자 이상 10자 이하이어야 합니다.",
    IllegalArgumentException::new
  ),
  INVALID_NICKNAME_FORMAT(
    "닉네임은 영문, 숫자, !, ?, _, -, *, /만 사용할 수 있습니다.",
    IllegalArgumentException::new
  ),
  EMAIL_REQUIRED(
    "이메일은 필수입니다.",
    IllegalArgumentException::new
  ),
  INVALID_EMAIL_FORMAT(
    "올바른 이메일 형식이 아닙니다.",
    IllegalArgumentException::new
  ),
  PASSWORD_REQUIRED(
    "비밀번호는 필수입니다.",
    IllegalArgumentException::new
  ),
  ENCODED_PASSWORD_REQUIRED(
    "암호화된 비밀번호는 필수입니다.",
    IllegalArgumentException::new
  ),
  PASSWORD_MATCHER_REQUIRED(
    "비밀번호 비교기가 필요합니다.",
    IllegalArgumentException::new
  );

  private final String message;
  private final Function<String, RuntimeException> exceptionFactory;

  DomainErrorType(String message, Function<String, RuntimeException> exceptionFactory) {
    this.message = message;
    this.exceptionFactory = exceptionFactory;
  }

  public RuntimeException createException() {
    return exceptionFactory.apply(message);
  }

  public String getMessage() {
    return message;
  }
}
