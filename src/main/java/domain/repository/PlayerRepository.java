package domain.repository;
import domain.model.Player;
import domain.vo.Email;

import java.util.Optional;

public interface PlayerRepository {

  Player save(Player player);

  Optional<Player> findByEmail(Email email);
}
