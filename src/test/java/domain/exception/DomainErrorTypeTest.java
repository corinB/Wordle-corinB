package domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DomainErrorTypeTest {

  @Test
  @DisplayName("잘못된 단어 에러는 InvalidWordException 생성")
  void invalidWordCreatesException() {
    RuntimeException exception =
      DomainErrorType.INVALID_WORD.createException();

    assertThat(exception)
      .isInstanceOf(InvalidWordException.class)
      .hasMessage("5글자 영어 알파벳만 입력할 수 있습니다.");
  }

  @Test
  @DisplayName("종료된 게임 에러는 GameAlreadyFinishedException 생성")
  void gameAlreadyFinishedCreatesException() {
    RuntimeException exception =
      DomainErrorType.GAME_ALREADY_FINISHED.createException();

    assertThat(exception)
      .isInstanceOf(GameAlreadyFinishedException.class)
      .hasMessage("끝난 게임입니다.");
  }
}
