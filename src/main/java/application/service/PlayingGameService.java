package application.service;

import domain.model.GameBoard;
import domain.model.Player;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import domain.repository.WordleGameRepository;
import domain.vo.GameHistory;
import domain.vo.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayingGameService {

  private final GameBoardRepository gameBoardRepository;
  private final WordleGameRepository wordleGameRepository;

  @Transactional
  public GameBoard joinGame(Player player) {
    WordleGame todayGame = findTodayGame();

    return gameBoardRepository
      .findByPlayerAndGame(player, todayGame)
      .orElseGet(() ->
        gameBoardRepository.save(new GameBoard(player, todayGame))
      );
  }

  @Transactional
  public GameBoard submitAnswer(Player player, Word word) {
    LocalDateTime currentTime = LocalDateTime.now();
    WordleGame todayGame = findTodayGame(currentTime);
    GameBoard gameBoard = gameBoardRepository
      .findByPlayerAndGame(player, todayGame)
      .orElseThrow(() ->
        new IllegalArgumentException("Game board does not exist.")
      );

    gameBoard.finishIfGameEnded(currentTime);
    if (gameBoard.canSubmit()) {
      gameBoard.submit(word);
    }

    return gameBoardRepository.save(gameBoard);
  }

  @Transactional
  public void expireAllEndedGames() {
    LocalDateTime currentTime = LocalDateTime.now();

    List<GameBoard> expiredGameBoards =
      gameBoardRepository.findAllPlayingBoardsEndedBefore(currentTime)
        .stream()
        .peek(gameBoard -> gameBoard.finishIfGameEnded(currentTime))
        .toList();

    gameBoardRepository.saveAll(expiredGameBoards);
  }

  @Transactional(readOnly = true)
  public GameHistory getHistory(GameBoard gameBoard) {
    return gameBoard.getHistory();
  }

  private WordleGame findTodayGame() {
    return findTodayGame(LocalDateTime.now());
  }

  private WordleGame findTodayGame(LocalDateTime currentTime) {
    LocalDateTime todayStart = currentTime.toLocalDate().atStartOfDay();

    return wordleGameRepository.findByStartAt(todayStart)
      .orElseThrow(() ->
        new IllegalArgumentException("Today's wordle game does not exist.")
      );
  }
}
