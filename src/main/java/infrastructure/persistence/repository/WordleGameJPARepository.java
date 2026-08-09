package infrastructure.persistence.repository;

import infrastructure.persistence.entity.WordleGameEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WordleGameJPARepository
  extends JpaRepository<WordleGameEntity, Long> {

  @EntityGraph(attributePaths = "correct")
  Optional<WordleGameEntity> findByStartAt(LocalDateTime startAt);
}
