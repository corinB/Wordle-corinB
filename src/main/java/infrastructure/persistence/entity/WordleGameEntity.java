package infrastructure.persistence.entity;

import domain.model.WordleGame;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wordle_games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordleGameEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "correct_word_id", nullable = false)
  private WordEntity correct;

  @Column(name = "start_at", nullable = false, unique = true)
  private LocalDateTime startAt;

  @Column(name = "end_at", nullable = false, unique = true)
  private LocalDateTime endAt;

  public static WordleGameEntity create(
    WordleGame wordleGame,
    WordEntity correctEntity
  ) {
    WordleGameEntity entity = new WordleGameEntity();
    entity.correct = correctEntity;
    entity.startAt = wordleGame.getStart();
    entity.endAt = wordleGame.getEnd();
    return entity;
  }

  public WordleGame toDomain() {
    return WordleGame.restore(
      correct.toDomain(),
      startAt,
      endAt
    );
  }
}
