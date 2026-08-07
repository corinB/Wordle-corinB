package infrastructure.persistence.repository;

import infrastructure.persistence.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordJPARepository extends JpaRepository<WordEntity, Long> {

  Optional<WordEntity> findByValue(String value);

  void deleteByValue(String value);

}
