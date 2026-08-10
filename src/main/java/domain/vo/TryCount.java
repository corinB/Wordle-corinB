package domain.vo;

import domain.model.GameBoard;

import static domain.exception.DomainErrorType.INVALID_TRY_COUNT;

public record TryCount(int value) {

  public TryCount {
    if (value < 0 || value > GameBoard.MAX_CHANCE) {
      throw INVALID_TRY_COUNT.createException();
    }
  }
}
