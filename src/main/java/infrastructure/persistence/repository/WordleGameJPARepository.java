package infrastructure.persistence.repository;

import infrastructure.persistence.entity.WordleGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WordleGameJPARepository
  extends JpaRepository<WordleGameEntity, Long> {

  Optional<WordleGameEntity> findByStartAt(LocalDateTime startAt);

  List<WordleGameEntity> findAllByEndAtLessThanEqual(LocalDateTime endAt);
}
