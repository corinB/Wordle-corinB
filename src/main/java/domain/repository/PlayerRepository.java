package domain.repository;
import domain.model.Player;
import domain.model.vo.Email;
import domain.model.vo.Nickname;

import java.util.Optional;

public interface PlayerRepository {

  Player save(Player player);

  Optional<Player> findByEmail(String email);

  Optional<Player> findByEmail(Email email);

  boolean existsByEmail(String email);

  boolean existsByEmail(Email email);

  boolean existsByNick(String nick);

  boolean existsByNick(Nickname nick);
}
