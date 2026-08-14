package domain.vo;

import domain.model.GameBoardStatus;

public record GameHistory(
  Word correct,
  Nickname player,
  TryCount tryCount,
  GameBoardStatus status) {
}
