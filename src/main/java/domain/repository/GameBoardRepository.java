package domain.repository;

import domain.model.GameBoard;
import domain.model.Player;
import domain.model.WordleGame;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameBoardRepository {

  GameBoard save(GameBoard gameBoard);

  void saveAll(List<GameBoard> gameBoards);

  Optional<GameBoard> findByPlayerAndGame(
    Player player,
    WordleGame game
  );

  List<GameBoard> findAllPlayingBoardsEndedBefore(
    LocalDateTime currentTime
  );
}
