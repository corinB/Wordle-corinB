package domain.repository;
import domain.model.Player;
import domain.vo.Email;
import domain.vo.Nickname;

import java.util.Optional;

public interface PlayerRepository {

  Player save(Player player);

  Optional<Player> findById(Long id);

  Optional<Player> findByEmail(Email email);

  Optional<Player> findByNickname(Nickname nickname);
}
