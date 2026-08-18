package domain.model;

import domain.vo.GameHistory;
import domain.vo.Nickname;
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
import static domain.exception.DomainErrorType.GAME_REQUIRED;
import static domain.exception.DomainErrorType.PLAYER_REQUIRED;

public class GameBoard {
  public static final int MAX_CHANCE = 6;

  private final Nickname nickname;
  private final Word correct;
  private final LocalDateTime deadLine;
  private final List<Round> rounds;
  private GameBoardStatus status;

  private GameBoard(
    LocalDateTime deadLine,
    GameBoardStatus status,
    List<Round> rounds,
    Word correct,
    Nickname nickname
  ) {
    this.deadLine = deadLine;
    this.status = status;
    this.rounds = new ArrayList<>(rounds);
    this.correct = correct;
    this.nickname = nickname;
  }

  public GameBoard(Player player, WordleGame game) {
    this(
      requireGame(game).getEnd(),
      GameBoardStatus.PLAYING,
      new ArrayList<>(),
      requireGame(game).getCorrect(),
      requirePlayer(player).getNickname()
    );
  }

  private GameBoard(
    Player player,
    WordleGame game,
    List<Round> rounds,
    GameBoardStatus status
  ) {
    this(
      requireGame(game).getEnd(),
      status,
      rounds,
      requireGame(game).getCorrect(),
      requirePlayer(player).getNickname()
    );
  }

  public static GameBoard restore(
    LocalDateTime deadLine,
    GameBoardStatus status,
    List<Round> rounds,
    Word correct,
    Nickname nickname
  ) {
    return new GameBoard(
      deadLine,
      status,
      rounds,
      correct,
      nickname
    );
  }

  public void submit(Word answer) {
    if (!canSubmit()) {
      throw GAME_ALREADY_FINISHED.createException();
    }

    if (answer == null) {
      throw ANSWER_REQUIRED.createException();
    }

    rounds.add(new Round(rounds.size(), answer, getCorrect()));
    updateStatus(answer);
  }

  public void finishIfGameEnded(LocalDateTime currentTime) {
    if (isFinished()) {
      return;
    }

    if (currentTime == null) {
      throw CURRENT_TIME_REQUIRED.createException();
    }

    if (!currentTime.isBefore(deadLine)) {
      status = GameBoardStatus.EXPIRED;
    }
  }

  public Nickname getNickname() {
    return nickname;
  }

  public Word getCorrect() {
    return correct;
  }

  public LocalDateTime getDeadLine() {
    return deadLine;
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

  public GameHistory getHistory() {
    if (!isFinished()) {
      throw GAME_NOT_FINISHED.createException();
    }

    return new GameHistory(
      correct,
      nickname,
      new TryCount(rounds.size()),
      status
    );
  }

  private void updateStatus(Word answer) {
    if (getCorrect().equals(answer)) {
      status = GameBoardStatus.WIN;
      return;
    }

    if (rounds.size() >= MAX_CHANCE) {
      status = GameBoardStatus.LOSE;
    }
  }

  private static Player requirePlayer(Player player) {
    if (player == null) {
      throw PLAYER_REQUIRED.createException();
    }
    return player;
  }

  private static WordleGame requireGame(WordleGame game) {
    if (game == null) {
      throw GAME_REQUIRED.createException();
    }
    return game;
  }
}
