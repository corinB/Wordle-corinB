package infrastructure.persistence.entity;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.vo.Round;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
  @Getter
  private Long id;

  @Column(name = "player_id", nullable = false)
  @Getter
  private Long playerId;

  @Column(name = "wordle_game_id", nullable = false)
  @Getter
  private Long wordleGameId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GameBoardStatus status;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "game_board_id", nullable = false)
  @OrderBy("roundIndex ASC")
  private final List<GameBoardRoundEntity> rounds = new ArrayList<>();

  public static GameBoardEntity create(GameBoard gameBoard) {
    GameBoardEntity entity = new GameBoardEntity();
    entity.playerId = gameBoard.getPlayerId();
    entity.wordleGameId = gameBoard.getWordleGameId();
    entity.update(gameBoard);
    return entity;
  }

  public void update(GameBoard gameBoard) {
    this.status = gameBoard.getStatus();
    gameBoard.getRounds().stream()
      .skip(rounds.size())
      .map(GameBoardRoundEntity::create)
      .forEach(rounds::add);
  }

  public GameBoard toDomain() {
    return GameBoard.restore(
      playerId,
      wordleGameId,
      status,
      rounds.stream().map(GameBoardRoundEntity::toDomain).toList()
    );
  }
}
