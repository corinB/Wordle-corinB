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
      () -> assertThat(gameBoard.getPlayerId()).isEqualTo(player.getId()),
      () -> assertThat(gameBoard.getWordleGameId()).isEqualTo(todayGame.getId()),
      () -> assertThat(gameBoard.getStatus()).isEqualTo(GameBoardStatus.PLAYING)
    );
  }

  @Test
  @DisplayName("오늘자 게임에 답안을 제출한다")
  void submitAnswerToTodayGame() {
    Word correct = new Word("apple");
    saveTodayGame(correct);
    Player player = savePlayer("corinB", "corin@example.com");
    playingGameService.joinGame(player);

    GameBoard gameBoard = playingGameService.submitAnswer(player, correct);

    assertAll(
      () -> assertThat(gameBoard.getStatus()).isEqualTo(GameBoardStatus.WIN),
      () -> assertThat(gameBoard.getSpentChance()).isEqualTo(1),
      () -> assertThat(gameBoard.getRounds())
        .extracting(round -> round.answer())
        .containsExactly(correct)
    );
  }

  @Test
  @DisplayName("종료된 모든 진행 보드를 만료 처리한다")
  void expireAllEndedGames() {
    Word firstCorrect = new Word("apple");
    Word secondCorrect = new Word("cocoa");
    Player firstPlayer = savePlayer("firstB", "first@example.com");
    Player secondPlayer = savePlayer("secondB", "second@example.com");
    LocalDateTime todayStart = LocalDateTime.now()
      .toLocalDate()
      .atStartOfDay();
    WordleGame firstEndedGame = saveGame(
      firstCorrect,
      todayStart.minusDays(3)
    );
    WordleGame secondEndedGame = saveGame(
      secondCorrect,
      todayStart.minusDays(2)
    );
    gameBoardRepository.save(new GameBoard(
      firstPlayer.getId(),
      firstEndedGame.getId()
    ));
    gameBoardRepository.save(new GameBoard(
      secondPlayer.getId(),
      secondEndedGame.getId()
    ));

    playingGameService.expireAllEndedGames();

    assertAll(
      () -> assertThat(gameBoardRepository
        .findByPlayerIdAndWordleGameId(
          firstPlayer.getId(),
          firstEndedGame.getId()
        )
        .orElseThrow().getStatus()).isEqualTo(GameBoardStatus.EXPIRED),
      () -> assertThat(gameBoardRepository
        .findByPlayerIdAndWordleGameId(
          secondPlayer.getId(),
          secondEndedGame.getId()
        )
        .orElseThrow().getStatus()).isEqualTo(GameBoardStatus.EXPIRED)
    );
  }

  @Test
  @DisplayName("종료된 보드의 히스토리를 관계 ID로 조회한다")
  void getHistory() {
    Word correct = new Word("apple");
    saveTodayGame(correct);
    Player player = savePlayer("corinB", "corin@example.com");
    playingGameService.joinGame(player);
    GameBoard gameBoard = playingGameService.submitAnswer(player, correct);

    GameHistory history = playingGameService.getHistory(gameBoard);

    assertAll(
      () -> assertThat(history.player()).isEqualTo(player.getNickname()),
      () -> assertThat(history.correct()).isEqualTo(correct),
      () -> assertThat(history.tryCount().value()).isEqualTo(1),
      () -> assertThat(history.status()).isEqualTo(GameBoardStatus.WIN)
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
    Word savedCorrect = wordRepository.findByWord(correct)
      .orElseGet(() -> wordRepository.save(correct));

    return wordleGameRepository.save(WordleGame.restore(
      null,
      savedCorrect.getId(),
      startAt,
      startAt.plusDays(1)
    ));
  }
}
