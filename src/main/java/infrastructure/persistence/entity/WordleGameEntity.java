package infrastructure.persistence.entity;

import domain.model.WordleGame;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wordle_games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordleGameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "correct_word_id", nullable = false)
  private Long correctWordId;

  @Column(name = "start_at", nullable = false, unique = true)
  private LocalDateTime startAt;

  @Column(name = "end_at", nullable = false, unique = true)
  private LocalDateTime endAt;

  @OneToMany
  @JoinColumn(name = "wordle_game_id", referencedColumnName = "id")
  private List<GameBoardEntity> gameBoards = new ArrayList<>();

  public static WordleGameEntity create(
    WordleGame wordleGame
  ) {
    WordleGameEntity entity = new WordleGameEntity();
    entity.correctWordId = wordleGame.getCorrectWordId();
    entity.startAt = wordleGame.getStart();
    entity.endAt = wordleGame.getEnd();
    return entity;
  }

  public WordleGame toDomain() {
    return WordleGame.restore(
      id,
      correctWordId,
      startAt,
      endAt
    );
  }
}
