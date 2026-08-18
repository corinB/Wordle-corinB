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

import java.time.LocalDateTime;
import java.util.List;

import static application.exception.ApplicationErrorType.GAME_BOARD_NOT_FOUND;
import static application.exception.ApplicationErrorType.TODAY_WORDLE_GAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PlayingGameService {

  private final GameBoardRepository gameBoardRepository;
  private final WordleGameRepository wordleGameRepository;

  public GameBoard joinGame(Player player) {
    WordleGame todayGame = findTodayGame();

    return gameBoardRepository
      .findByPlayerAndGame(player, todayGame)
      .orElseGet(() ->
        gameBoardRepository.save(new GameBoard(player, todayGame))
      );
  }

  public GameBoard submitAnswer(Player player, Word word) {
    LocalDateTime currentTime = LocalDateTime.now();
    WordleGame todayGame = findTodayGame(currentTime);
    GameBoard gameBoard = gameBoardRepository
      .findByPlayerAndGame(player, todayGame)
      .orElseThrow(() ->
        GAME_BOARD_NOT_FOUND.createException()
      );

    gameBoard.finishIfGameEnded(currentTime);
    if (gameBoard.canSubmit()) {
      gameBoard.submit(word);
    }

    return gameBoardRepository.save(gameBoard);
  }

  public void expireAllEndedGames() {
    LocalDateTime currentTime = LocalDateTime.now();

    List<GameBoard> expiredGameBoards =
      gameBoardRepository.findAllPlayingBoardsEndedBefore(currentTime)
        .stream()
        .peek(gameBoard -> gameBoard.finishIfGameEnded(currentTime))
        .toList();

    gameBoardRepository.saveAll(expiredGameBoards);
  }

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
        TODAY_WORDLE_GAME_NOT_FOUND.createException()
      );
  }
}
