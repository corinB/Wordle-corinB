package infrastructure.persistence.repository.impl;

import domain.model.Player;
import domain.vo.Email;
import domain.repository.PlayerRepository;
import domain.vo.Nickname;
import infrastructure.persistence.entity.PlayerEntity;
import infrastructure.persistence.repository.PlayerJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepository {

  private final PlayerJPARepository playerJPARepository;

  @Override
  public Player save(Player player) {
    return playerJPARepository.save(PlayerEntity.create(player)).toDomain();
  }

  @Override
  public Optional<Player> findByEmail(Email email) {
    return playerJPARepository.findByEmail(email.value())
      .map(PlayerEntity::toDomain);
  }

  @Override
  public Optional<Player> findByNickname(Nickname nickname) {
    return playerJPARepository.findByNickname(nickname.value())
      .map(PlayerEntity::toDomain);
  }
}
