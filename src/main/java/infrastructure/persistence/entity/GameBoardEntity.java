package infrastructure.persistence.entity;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.vo.Round;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
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
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", nullable = false)
  private PlayerEntity player;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wordle_game_id", nullable = false)
  private WordleGameEntity game;

  @Enumerated(EnumType.STRING)
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
    entity.player = player;
    entity.game = game;
    entity.update(gameBoard);
    return entity;
  }

  public void update(GameBoard gameBoard) {
    this.status = gameBoard.getStatus();
    appendNewRound(gameBoard.getRounds());
  }

  private void appendNewRound(List<Round> domainRounds) {
    if (rounds.size() < domainRounds.size()) {
      appendNewRound(domainRounds.getLast());
    }
  }

  private void appendNewRound(Round newRound) {
    rounds.add(GameBoardRoundEntity.create(this, newRound));
  }

  public GameBoard toDomain() {
    List<Round> domainRounds = rounds.stream()
      .map(GameBoardRoundEntity::toDomain)
      .toList();

    return GameBoard.restore(
      player.toDomain(),
      game.toDomain(),
      domainRounds,
      status
    );
  }
}
