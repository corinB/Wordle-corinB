package infrastructure.persistence.repository;

import infrastructure.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerJPARepository extends JpaRepository<PlayerEntity, Long> {

  Optional<PlayerEntity> findByEmail(String email);

  Optional<PlayerEntity> findByNickname(String nickname);
}
