package domain.model;

import domain.vo.GameHistory;
import domain.vo.Round;
import domain.vo.TryCount;
import domain.vo.Word;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static domain.exception.DomainErrorType.ANSWER_REQUIRED;
import static domain.exception.DomainErrorType.CURRENT_TIME_REQUIRED;
import static domain.exception.DomainErrorType.GAME_ALREADY_FINISHED;
import static domain.exception.DomainErrorType.GAME_NOT_FINISHED;
import static domain.exception.DomainErrorType.CORRECT_WORD_REQUIRED;
import static domain.exception.DomainErrorType.GAME_END_TIME_REQUIRED;
import static domain.exception.DomainErrorType.NICKNAME_REQUIRED;
import static domain.exception.DomainErrorType.PLAYER_ID_REQUIRED;
import static domain.exception.DomainErrorType.WORDLE_GAME_ID_REQUIRED;

public class GameBoard {
  public static final int MAX_CHANCE = 6;

  private final Long playerId;
  private final Long wordleGameId;
  private final List<Round> rounds;
  private GameBoardStatus status;

  private GameBoard(
    Long playerId,
    Long wordleGameId,
    GameBoardStatus status,
    List<Round> rounds
  ) {
    this.playerId = requirePlayerId(playerId);
    this.wordleGameId = requireWordleGameId(wordleGameId);
    this.status = status;
    this.rounds = new ArrayList<>(rounds);
  }

  public GameBoard(Long playerId, Long wordleGameId) {
    this(
      playerId,
      wordleGameId,
      GameBoardStatus.PLAYING,
      new ArrayList<>()
    );
  }

  public static GameBoard restore(
    Long playerId,
    Long wordleGameId,
    GameBoardStatus status,
    List<Round> rounds
  ) {
    return new GameBoard(
      playerId,
      wordleGameId,
      status,
      rounds
    );
  }

  public void submit(Word answer, Word correct) {
    if (!canSubmit()) {
      throw GAME_ALREADY_FINISHED.createException();
    }

    if (answer == null) {
      throw ANSWER_REQUIRED.createException();
    }

    Word requiredCorrect = requireCorrect(correct);
    rounds.add(new Round(rounds.size(), answer, requiredCorrect));
    updateStatus(answer, requiredCorrect);
  }

  public void finishIfGameEnded(
    LocalDateTime currentTime,
    LocalDateTime deadLine
  ) {
    if (isFinished()) {
      return;
    }

    if (currentTime == null) {
      throw CURRENT_TIME_REQUIRED.createException();
    }

    if (deadLine == null) {
      throw GAME_END_TIME_REQUIRED.createException();
    }

    if (!currentTime.isBefore(deadLine)) {
      status = GameBoardStatus.EXPIRED;
    }
  }

  public Long getPlayerId() {
    return playerId;
  }

  public Long getWordleGameId() {
    return wordleGameId;
  }

  public List<Round> getRounds() {
    return List.copyOf(rounds);
  }

  public List<String> getRecords() {
    return rounds.stream()
      .map(Round::compare)
      .toList();
  }

  public int getSpentChance() {
    return rounds.size();
  }

  public boolean canSubmit() {
    return status == GameBoardStatus.PLAYING;
  }

  public boolean isCorrect() {
    return status == GameBoardStatus.WIN;
  }

  public boolean isFailed() {
    return status == GameBoardStatus.LOSE;
  }

  public boolean isExpired() {
    return status == GameBoardStatus.EXPIRED;
  }

  public boolean isFinished() {
    return status != GameBoardStatus.PLAYING;
  }

  public GameBoardStatus getStatus() {
    return status;
  }

  public GameHistory getHistory(
    domain.vo.Nickname nickname,
    Word correct
  ) {
    if (!isFinished()) {
      throw GAME_NOT_FINISHED.createException();
    }

    return new GameHistory(
      requireCorrect(correct),
      requireNickname(nickname),
      new TryCount(rounds.size()),
      status
    );
  }

  private void updateStatus(Word answer, Word correct) {
    if (correct.equals(answer)) {
      status = GameBoardStatus.WIN;
      return;
    }

    if (rounds.size() >= MAX_CHANCE) {
      status = GameBoardStatus.LOSE;
    }
  }

  private static Long requirePlayerId(Long playerId) {
    if (playerId == null) {
      throw PLAYER_ID_REQUIRED.createException();
    }
    return playerId;
  }

  private static Long requireWordleGameId(Long wordleGameId) {
    if (wordleGameId == null) {
      throw WORDLE_GAME_ID_REQUIRED.createException();
    }
    return wordleGameId;
  }

  private static Word requireCorrect(Word correct) {
    if (correct == null) {
      throw CORRECT_WORD_REQUIRED.createException();
    }
    return correct;
  }

  private static domain.vo.Nickname requireNickname(
    domain.vo.Nickname nickname
  ) {
    if (nickname == null) {
      throw NICKNAME_REQUIRED.createException();
    }
    return nickname;
  }
}
