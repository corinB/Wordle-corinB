package domain.repository;

import domain.model.GameBoard;
import java.util.List;
import java.util.Optional;

public interface GameBoardRepository {

  GameBoard save(GameBoard gameBoard);

  void saveAll(List<GameBoard> gameBoards);

  Optional<GameBoard> findByPlayerIdAndWordleGameId(
    Long playerId,
    Long wordleGameId
  );

  List<GameBoard> findAllPlayingByWordleGameIds(
    List<Long> wordleGameIds
  );
}
