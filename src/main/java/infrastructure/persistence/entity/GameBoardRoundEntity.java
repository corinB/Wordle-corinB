package infrastructure.persistence.entity;

import domain.vo.Round;
import domain.vo.Word;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
  name = "game_board_rounds",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_game_board_rounds_board_index",
      columnNames = {"game_board_id", "round_index"} //게임보드 내에서 리운드의 인덱스는 유일하다
    )
  }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameBoardRoundEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_board_id", nullable = false)
  private GameBoardEntity gameBoard;

  @Column(name = "round_index", nullable = false)
  private int roundIndex;

  @Column(name = "answer", nullable = false)
  private String answer;

  @Column(name = "compare", nullable = false)
  private String compare;

  public static GameBoardRoundEntity create(
    GameBoardEntity gameBoard,
    Round round
  ) {
    GameBoardRoundEntity entity = new GameBoardRoundEntity();
    entity.gameBoard = gameBoard;
    entity.roundIndex = round.index();
    entity.answer = round.answer().value();
    entity.compare = round.compare();
    return entity;
  }

  public Round toDomain() {
    return new Round(
      roundIndex,
      new Word(answer),
      compare
    );
  }
}
