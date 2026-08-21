package infrastructure.persistence.entity;

import domain.vo.Round;
import domain.vo.Word;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
  name = "game_board_rounds",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_game_board_rounds_board_index",
      columnNames = {"game_board_id", "round_index"}
    )
  }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameBoardRoundEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(
    name = "game_board_id",
    nullable = false,
    insertable = false,
    updatable = false
  )
  @Getter
  private Long gameBoardId;

  @Column(name = "round_index", nullable = false)
  private int roundIndex;

  @Column(name = "answer", nullable = false)
  private String answer;

  @Column(name = "compare", nullable = false)
  private String compare;

  public static GameBoardRoundEntity create(Round round) {
    GameBoardRoundEntity entity = new GameBoardRoundEntity();
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
