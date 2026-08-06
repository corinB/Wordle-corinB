package domain.repository;
import domain.model.Player;
import java.util.Optional;

public interface PlayerRepository {

  Player save(Player player);

  Optional<Player> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByNick(String nick);
}
