package infrastructure.persistence.repository;

import infrastructure.persistence.entity.WordleGameEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WordleGameJPARepository
  extends JpaRepository<WordleGameEntity, Long> {

  @EntityGraph(attributePaths = "correct")
  Optional<WordleGameEntity> findByStartAt(LocalDateTime startAt);

  @EntityGraph(attributePaths = "correct")
  Optional<WordleGameEntity> findByEndAt(LocalDateTime endAt);

  @EntityGraph(attributePaths = "correct")
  List<WordleGameEntity> findAllByEndAtLessThanEqual(LocalDateTime endAt);
}
