package infrastructure.persistence.entity;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.vo.Round;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
  name = "game_boards",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_game_boards_player_game",
      columnNames = {"player_id", "wordle_game_id"}
    )
  }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameBoardEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "player_id", nullable = false)
  private Long playerId;

  @Column(name = "wordle_game_id", nullable = false)
  private Long wordleGameId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GameBoardStatus status;

  public static GameBoardEntity create(GameBoard gameBoard) {
    GameBoardEntity entity = new GameBoardEntity();
    entity.playerId = gameBoard.getPlayerId();
    entity.wordleGameId = gameBoard.getWordleGameId();
    entity.update(gameBoard);
    return entity;
  }

  public void update(GameBoard gameBoard) {
    this.status = gameBoard.getStatus();
  }

  public Long getId() {
    return id;
  }

  public Long getPlayerId() {
    return playerId;
  }

  public Long getWordleGameId() {
    return wordleGameId;
  }

  public GameBoard toDomain(List<Round> rounds) {
    return GameBoard.restore(
      playerId,
      wordleGameId,
      status,
      rounds
    );
  }
}
