package infrastructure.persistence.entity;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.vo.Nickname;
import domain.vo.Round;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
  private Long id;

  @Column(name = "player_id", nullable = false)
  private Long playerId;

  @Column(name = "wordle_game_id", nullable = false)
  private Long wordleGameId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GameBoardStatus status;

  @OneToMany(
    mappedBy = "gameBoard",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @OrderBy("roundIndex ASC")
  private List<GameBoardRoundEntity> rounds = new ArrayList<>();

  public static GameBoardEntity create(
    PlayerEntity player,
    WordleGameEntity game,
    GameBoard gameBoard
  ) {
    GameBoardEntity entity = new GameBoardEntity();
    entity.playerId = player.getId();
    entity.wordleGameId = game.getId();
    entity.update(gameBoard);
    return entity;
  }

  public void update(GameBoard gameBoard) {
    this.status = gameBoard.getStatus();
    appendNewRound(gameBoard.getRounds());
  }

  private void appendNewRound(List<Round> domainRounds) {
    if (rounds.size() >= domainRounds.size()) {
      return;
    }

    domainRounds.stream()
      .skip(rounds.size())
      .forEach(this::appendNewRound);
  }

  private void appendNewRound(Round newRound) {
    rounds.add(GameBoardRoundEntity.create(this, newRound));
  }

  public Long getPlayerId() {
    return playerId;
  }

  public Long getWordleGameId() {
    return wordleGameId;
  }

  public GameBoard toDomain(
    PlayerEntity player,
    WordleGameEntity game
  ) {
    return GameBoard.restore(
      game.getEndAt(),
      status,
      rounds.stream()
        .map(GameBoardRoundEntity::toDomain)
        .toList(),
      game.getCorrect().toDomain(),
      new Nickname(player.getNickname())
    );
  }
}
