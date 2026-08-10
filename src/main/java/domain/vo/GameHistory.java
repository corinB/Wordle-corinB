package domain.vo;

import domain.model.GameBoardStatus;
import domain.model.Player;
import domain.model.WordleGame;

public record GameHistory(
  WordleGame wordleGame,
  Player player,
  TryCount tryCount,
  GameBoardStatus status) {
}
