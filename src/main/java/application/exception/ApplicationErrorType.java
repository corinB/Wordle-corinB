package application.exception;

import lombok.Getter;

public enum ApplicationErrorType {

  PASSWORD_DOES_NOT_MATCH("비밀번호가 일치하지 않습니다."),
  PLAYER_NOT_FOUND("플레이어를 찾을 수 없습니다."),
  DUPLICATED_NICKNAME("이미 사용 중인 닉네임입니다."),
  DUPLICATED_EMAIL("이미 사용 중인 이메일입니다."),
  GAME_BOARD_NOT_FOUND("게임 보드를 찾을 수 없습니다."),
  TODAY_WORDLE_GAME_NOT_FOUND("오늘 게임을 찾을 수 없습니다."),
  WORDLE_GAME_NOT_FOUND("워들 게임을 찾을 수 없습니다."),
  WORD_NOT_FOUND("단어를 찾을 수 없습니다.");

  @Getter
  private final String message;

  ApplicationErrorType(String message) {
    this.message = message;
  }

  public IllegalArgumentException createException() {
    return new IllegalArgumentException(message);
  }
}
