package domain.model;

import domain.exception.InvalidTryCountException;
import domain.vo.TryCount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class TryCountTest {

  @Test
  @DisplayName("시도 횟수는 0부터 최대 기회까지 생성할 수 있다")
  void createTryCountInRange() {
    assertAll(
      () -> assertThat(new TryCount(0).value())
        .isEqualTo(0),
      () -> assertThat(new TryCount(GameBoard.MAX_CHANCE).value())
        .isEqualTo(GameBoard.MAX_CHANCE)
    );
  }

  @Test
  @DisplayName("시도 횟수가 범위를 벗어나면 예외가 발생한다")
  void cannotCreateTryCountOutOfRange() {
    assertAll(
      () -> assertThatThrownBy(() -> new TryCount(-1))
        .isInstanceOf(InvalidTryCountException.class),
      () -> assertThatThrownBy(() ->
        new TryCount(GameBoard.MAX_CHANCE + 1)
      ).isInstanceOf(InvalidTryCountException.class)
    );
  }
}
