package application.service;

import domain.model.GameBoard;
import domain.model.Player;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import domain.repository.PlayerRepository;
import domain.repository.WordRepository;
import domain.repository.WordleGameRepository;
import domain.vo.GameHistory;
import domain.vo.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static application.exception.ApplicationErrorType.GAME_BOARD_NOT_FOUND;
import static application.exception.ApplicationErrorType.PLAYER_NOT_FOUND;
import static application.exception.ApplicationErrorType.TODAY_WORDLE_GAME_NOT_FOUND;
import static application.exception.ApplicationErrorType.WORDLE_GAME_NOT_FOUND;
import static application.exception.ApplicationErrorType.WORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PlayingGameService {

  private final GameBoardRepository gameBoardRepository;
  private final PlayerRepository playerRepository;
  private final WordRepository wordRepository;
  private final WordleGameRepository wordleGameRepository;

  @Transactional
  public GameBoard joinGame(Player player) {
    WordleGame todayGame = findTodayGame();

    return gameBoardRepository
      .findByPlayerIdAndWordleGameId(player.getId(), todayGame.getId())
      .orElseGet(() ->
        gameBoardRepository.save(
          new GameBoard(player.getId(), todayGame.getId())
        )
      );
  }

  @Transactional
  public GameBoard submitAnswer(Player player, Word word) {
    LocalDateTime currentTime = LocalDateTime.now();
    WordleGame todayGame = findTodayGame(currentTime);
    GameBoard gameBoard = gameBoardRepository
      .findByPlayerIdAndWordleGameId(player.getId(), todayGame.getId())
      .orElseThrow(GAME_BOARD_NOT_FOUND::createException);

    Word correct = findWord(todayGame.getCorrectWordId());
    gameBoard.finishIfGameEnded(currentTime, todayGame.getEnd());
    if (gameBoard.canSubmit()) {
      gameBoard.submit(word, correct);
    }

    return gameBoardRepository.save(gameBoard);
  }

  @Transactional
  public void expireAllEndedGames() {
    LocalDateTime currentTime = LocalDateTime.now();
    List<WordleGame> endedGames =
      wordleGameRepository.findAllEndedBefore(currentTime);

    if (endedGames.isEmpty()) {
      return;
    }

    Map<Long, WordleGame> endedGamesById = endedGames.stream()
      .collect(Collectors.toMap(WordleGame::getId, Function.identity()));

    List<GameBoard> expiredGameBoards =
      gameBoardRepository.findAllPlayingByWordleGameIds(
          endedGamesById.keySet().stream().toList()
        )
        .stream()
        .peek(gameBoard -> gameBoard.finishIfGameEnded(
          currentTime,
          endedGamesById.get(gameBoard.getWordleGameId()).getEnd()
        ))
        .toList();

    gameBoardRepository.saveAll(expiredGameBoards);
  }

  @Transactional(readOnly = true)
  public GameHistory getHistory(GameBoard gameBoard) {
    Player player = playerRepository.findById(gameBoard.getPlayerId())
      .orElseThrow(PLAYER_NOT_FOUND::createException);
    WordleGame game = wordleGameRepository
      .findById(gameBoard.getWordleGameId())
      .orElseThrow(WORDLE_GAME_NOT_FOUND::createException);

    return gameBoard.getHistory(
      player.getNickname(),
      findWord(game.getCorrectWordId())
    );
  }

  private WordleGame findTodayGame() {
    return findTodayGame(LocalDateTime.now());
  }

  private WordleGame findTodayGame(LocalDateTime currentTime) {
    LocalDateTime todayStart = currentTime.toLocalDate().atStartOfDay();

    return wordleGameRepository.findByStartAt(todayStart)
      .orElseThrow(TODAY_WORDLE_GAME_NOT_FOUND::createException);
  }

  private Word findWord(Long wordId) {
    return wordRepository.findById(wordId)
      .orElseThrow(WORD_NOT_FOUND::createException);
  }
}
