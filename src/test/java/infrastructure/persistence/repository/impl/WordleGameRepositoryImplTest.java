package infrastructure.persistence.repository.impl;

import application.WordleApplication;
import domain.model.WordleGame;
import domain.repository.WordleGameRepository;
import domain.vo.Word;
import infrastructure.persistence.entity.WordEntity;
import infrastructure.persistence.repository.WordJPARepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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
  @DisplayName("WordleGame을 정답 단어 ID와 시작 시간으로 저장하고 조회한다")
  void saveAndFindByStartAt() {
    Word correct = persistWord("apple");
    LocalDateTime startAt = LocalDateTime.of(2026, 8, 9, 0, 0);
    LocalDateTime endAt = startAt.plusDays(1);

    WordleGame saved = wordleGameRepository.save(
      WordleGame.restore(null, correct.getId(), startAt, endAt)
    );
    entityManager.flush();
    entityManager.clear();

    WordleGame foundGame = wordleGameRepository
      .findByStartAt(startAt)
      .orElseThrow();

    assertAll(
      () -> assertThat(foundGame.getId()).isEqualTo(saved.getId()),
      () -> assertThat(foundGame.getCorrectWordId()).isEqualTo(correct.getId()),
      () -> assertThat(foundGame.getStart()).isEqualTo(startAt),
      () -> assertThat(foundGame.getEnd()).isEqualTo(endAt)
    );
  }

  @Test
  @DisplayName("게임 저장소는 단어 JPA 저장소를 주입하지 않는다")
  void doesNotDependOnWordJpaRepository() {
    assertThat(WordleGameRepositoryImpl.class.getDeclaredFields())
      .extracting(Field::getType)
      .doesNotContain(WordJPARepository.class);
  }

  private Word persistWord(String value) {
    return entityManager.persist(WordEntity.create(new Word(value))).toDomain();
  }
}
