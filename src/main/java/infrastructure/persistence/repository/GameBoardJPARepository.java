package infrastructure.persistence.repository;

import domain.model.GameBoardStatus;
import infrastructure.persistence.entity.GameBoardEntity;
import infrastructure.persistence.entity.PlayerEntity;
import infrastructure.persistence.entity.WordleGameEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameBoardJPARepository
  extends JpaRepository<GameBoardEntity, Long> {

  @EntityGraph(attributePaths = {
    "player",
    "game",
    "game.correct",
    "rounds"
  })
  Optional<GameBoardEntity> findByPlayerAndGame(
    PlayerEntity player,
    WordleGameEntity game
  );

  @EntityGraph(attributePaths = {
    "player",
    "game",
    "game.correct",
    "rounds"
  })
  List<GameBoardEntity> findAllByStatusAndGame_EndAtLessThanEqual(
    GameBoardStatus status,
    LocalDateTime endAt
  );
}
