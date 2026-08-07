package infrastructure.persistence.entity;

import domain.model.Player;
import domain.model.vo.Email;
import domain.model.vo.EncodedPassword;
import domain.model.vo.Nickname;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
  name = "players",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_players_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_players_nickname", columnNames = "nickname")
  }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "encoded_password", nullable = false)
  private String encodedPassword;

  public static PlayerEntity create(Player player) {
    PlayerEntity entity = new PlayerEntity();
    entity.nickname = player.getNickname().value();
    entity.email = player.getEmail().value();
    entity.encodedPassword = player.getEncodedPassword().value();
    return entity;
  }

  public Player toDomain() {
    return Player.create(
      new Nickname(nickname),
      new Email(email),
      new EncodedPassword(encodedPassword)
    );
  }
}
