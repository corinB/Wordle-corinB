package application.service;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.model.Player;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import domain.repository.PlayerRepository;
import domain.repository.WordRepository;
import domain.repository.WordleGameRepository;
import domain.vo.GameHistory;
import domain.vo.Word;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PlayingGameServiceTest {

  @Autowired
  private PlayingGameService playingGameService;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private WordRepository wordRepository;

  @Autowired
  private WordleGameRepository wordleGameRepository;

  @Autowired
  private GameBoardRepository gameBoardRepository;

  @Test
  @DisplayName("오늘자 게임에 참가한다")
  void joinTodayGame() {
    Word correct = new Word("apple");
    WordleGame todayGame = saveTodayGame(correct);
    Player player = savePlayer("corinB", "corin@example.com");

    GameBoard gameBoard = playingGameService.joinGame(player);

    assertAll(
      () -> assertThat(gameBoard.getPlayer().getEmail())
        .isEqualTo(player.getEmail()),
      () -> assertThat(gameBoard.getGame().getStart())
        .isEqualTo(todayGame.getStart()),
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.PLAYING)
    );
  }

  @Test
  @DisplayName("오늘자 게임에 답안을 제출한다")
  void submitAnswerToTodayGame() {
    Word correct = new Word("apple");
    saveTodayGame(correct);
    Player player = savePlayer("corinB", "corin@example.com");
    playingGameService.joinGame(player);

    GameBoard gameBoard =
      playingGameService.submitAnswer(player, correct);

    assertAll(
      () -> assertThat(gameBoard.getStatus())
        .isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(gameBoard.getSpentChance())
        .isEqualTo(1),
      () -> assertThat(gameBoard.getRounds())
        .extracting(round -> round.answer())
        .containsExactly(correct)
    );
  }

  @Test
  @DisplayName("종료 시간이 지난 진행 중인 보드를 모두 만료 처리한다")
  void expireAllEndedGames() {
    Word correct = new Word("apple");
    Player player = savePlayer("corinB", "corin@example.com");
    LocalDateTime startAt = LocalDateTime.now()
      .toLocalDate()
      .atStartOfDay()
      .minusDays(2);
    WordleGame endedGame = saveGame(correct, startAt);
    gameBoardRepository.save(new GameBoard(player, endedGame));

    playingGameService.expireAllEndedGames();

    GameBoard expiredGameBoard = gameBoardRepository
      .findByPlayerAndGame(player, endedGame)
      .orElseThrow();

    assertAll(
      () -> assertThat(expiredGameBoard.getStatus())
        .isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThat(expiredGameBoard.isExpired())
        .isTrue()
    );
  }

  @Test
  @DisplayName("종료된 보드의 히스토리를 조회한다")
  void getHistory() {
    Word correct = new Word("apple");
    saveTodayGame(correct);
    Player player = savePlayer("corinB", "corin@example.com");
    playingGameService.joinGame(player);
    GameBoard gameBoard =
      playingGameService.submitAnswer(player, correct);

    GameHistory history = playingGameService.getHistory(gameBoard);

    assertAll(
      () -> assertThat(history.player().getEmail())
        .isEqualTo(player.getEmail()),
      () -> assertThat(history.wordleGame().getCorrect())
        .isEqualTo(correct),
      () -> assertThat(history.tryCount().value())
        .isEqualTo(1),
      () -> assertThat(history.status())
        .isEqualTo(GameBoardStatus.WIN)
    );
  }

  private Player savePlayer(String nickname, String email) {
    return playerRepository.save(Player.create(
      nickname,
      email,
      "encodedPassword"
    ));
  }

  private WordleGame saveTodayGame(Word correct) {
    LocalDateTime todayStart = LocalDateTime.now()
      .toLocalDate()
      .atStartOfDay();
    return saveGame(correct, todayStart);
  }

  private WordleGame saveGame(Word correct, LocalDateTime startAt) {
    wordRepository.findByWord(correct)
      .orElseGet(() -> wordRepository.save(correct));

    return wordleGameRepository.save(
      WordleGame.restore(correct, startAt, startAt.plusDays(1))
    );
  }
}
