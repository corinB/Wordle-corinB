package domain.model;

import domain.vo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static domain.exception.DomainErrorType.GAME_ALREADY_FINISHED;
import static domain.exception.DomainErrorType.GAME_NOT_FINISHED;

public class GameBoard {
  public static final int MAX_CHANCE = 6;

  private final Nickname nickname;
  private final Word correct;
  private final LocalDateTime deadLine;
  private final List<Round> rounds;
  private GameBoardStatus status;

  public GameBoard(LocalDateTime deadLine,GameBoardStatus status, List<Round> rounds, Word correct,  Nickname nickname) {
    this.deadLine = deadLine;
    this.status = status;
    this.rounds = rounds;
    this.correct = correct;
    this.nickname = nickname;
  }

  public GameBoard(Player player, WordleGame game) {
    this(
      game.getEnd(),
      GameBoardStatus.PLAYING,
      new ArrayList<>(),
      Objects.requireNonNull(game, "게임은 필수입니다.").getCorrect(),
      Objects.requireNonNull(player, "플레이어는 필수입니다.").getNickname()
    );
  }

  private GameBoard(
    Player player,
    WordleGame game,
    List<Round> rounds,
    GameBoardStatus status
  ) {
    this(
      game.getEnd(),
      status,
      rounds,
      Objects.requireNonNull(game, "게임은 필수입니다.").getCorrect(),
      Objects.requireNonNull(player, "플레이어는 필수입니다.").getNickname()
    );
  }

  public static GameBoard restore(
    Player player,
    WordleGame game,
    List<Round> rounds,
    GameBoardStatus status
  ) {
    return new GameBoard(player, game, rounds, status);
  }

  public void submit(Word answer) {
    if (!canSubmit()) {
      throw GAME_ALREADY_FINISHED.createException();
    }

    Word submittedAnswer = Objects.requireNonNull(answer);
    rounds.add(new Round(rounds.size(), submittedAnswer, getCorrect()));
    updateStatus(submittedAnswer);
  }

  public void finishIfGameEnded(LocalDateTime currentTime) {
    if (isFinished()) {
      return;
    }

    if (!Objects.requireNonNull(currentTime).isBefore(deadLine)) {
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

  private void updateStatus(Word answer) {
    if (getCorrect().equals(answer)) {
      status = GameBoardStatus.WIN;
      return;
    }

    if (rounds.size() >= MAX_CHANCE) {
      status = GameBoardStatus.LOSE;
    }
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
}
