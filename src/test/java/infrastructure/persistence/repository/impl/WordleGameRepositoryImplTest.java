package infrastructure.persistence.repository.impl;

import application.WordleApplication;
import domain.model.WordleGame;
import domain.vo.Word;
import domain.repository.WordleGameRepository;
import infrastructure.persistence.entity.WordEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
@Import(WordleGameRepositoryImpl.class)
@ContextConfiguration(classes = WordleApplication.class)
class WordleGameRepositoryImplTest {

  @Autowired
  private WordleGameRepository wordleGameRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("WordleGame을 저장하고 시작 시간으로 조회한다")
  void saveAndFindByStartAt() {
    Word correct = new Word("apple");
    LocalDateTime startAt = LocalDateTime.of(2026, 8, 9, 0, 0);
    LocalDateTime endAt = startAt.plusDays(1);

    entityManager.persist(WordEntity.create(correct));
    wordleGameRepository.save(WordleGame.restore(correct, startAt, endAt));
    entityManager.flush();
    entityManager.clear();

    Optional<WordleGame> foundGame =
      wordleGameRepository.findByStartAt(startAt);

    assertThat(foundGame)
      .isPresent();

    WordleGame savedGame = foundGame.get();

    assertAll(
      () -> assertThat(savedGame.getCorrect()).isEqualTo(correct),
      () -> assertThat(savedGame.getStart()).isEqualTo(startAt),
      () -> assertThat(savedGame.getEnd()).isEqualTo(endAt)
    );
  }

  @Test
  @DisplayName("정답 단어가 DB에 없으면 WordleGame을 저장할 수 없다")
  void cannotSaveWithoutCorrectWord() {
    Word correct = new Word("apple");
    LocalDateTime startAt = LocalDateTime.of(2026, 8, 9, 0, 0);
    WordleGame wordleGame =
      WordleGame.restore(correct, startAt, startAt.plusDays(1));

    assertThatThrownBy(() -> wordleGameRepository.save(wordleGame))
      .isInstanceOf(IllegalArgumentException.class);
  }
}
