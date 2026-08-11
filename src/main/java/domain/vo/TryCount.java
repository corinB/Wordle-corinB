package domain.vo;

import domain.model.GameBoard;

import static domain.exception.DomainErrorType.INVALID_TRY_COUNT;

// 시도 횟수
public record TryCount(int value) {

  public TryCount {
    if (value < 0 || value > GameBoard.MAX_CHANCE) {
      throw INVALID_TRY_COUNT.createException();
    }
  }
}
