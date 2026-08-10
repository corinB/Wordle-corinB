package application.service;

import domain.model.WordleGame;
import domain.vo.Word;
import domain.repository.WordRepository;
import domain.repository.WordleGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GameLifecycleServiceTest {

  @Autowired
  GameLifecycleService gameLifecycleService;

  @Autowired
  WordleGameRepository wordleGameRepository;

  @Autowired
  WordRepository wordRepository;

  @BeforeEach
  void setUp() {
    Word word = new Word("apple");
    if (wordRepository.findByWord(word).isEmpty()) {
      wordRepository.save(word);
    }
  }

  @Test
  @DisplayName("New game is saved with correct word and daily time range")
  void startNewGameTest() {
    WordleGame wordleGame = gameLifecycleService.startNewGame();

    WordleGame savedGame = wordleGameRepository
      .findByStartAt(wordleGame.getStart())
      .orElseThrow();

    assertAll(
      () -> assertThat(savedGame).isNotNull(),
      () -> assertThat(savedGame.getCorrect())
        .isEqualTo(wordleGame.getCorrect()),
      () -> assertThat(savedGame.getStart())
        .isEqualTo(wordleGame.getStart()),
      () -> assertThat(savedGame.getStart()).isEqualTo(todayStart()),
      () -> assertThat(savedGame.getEnd()).isEqualTo(todayStart().plusDays(1))
    );
  }

  private static LocalDateTime todayStart() {
    return LocalDateTime.now().toLocalDate().atStartOfDay();
  }
}
