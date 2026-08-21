package infrastructure.persistence.repository;

import domain.model.GameBoardStatus;
import infrastructure.persistence.entity.GameBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameBoardJPARepository
  extends JpaRepository<GameBoardEntity, Long> {

  Optional<GameBoardEntity> findByPlayerIdAndWordleGameId(
    Long playerId,
    Long wordleGameId
  );

  List<GameBoardEntity> findAllByStatusAndWordleGameIdIn(
    GameBoardStatus status,
    List<Long> wordleGameIds
  );
}
