package domain.model;

import domain.vo.GameHistory;
import domain.vo.Round;
import domain.vo.TryCount;
import domain.vo.Word;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static domain.exception.DomainErrorType.GAME_ALREADY_FINISHED;
import static domain.exception.DomainErrorType.GAME_NOT_FINISHED;

public class GameBoard {
  public static final int MAX_CHANCE = 6;

  private final Player player;
  private final WordleGame game;
  private final List<Round> rounds;
  private GameBoardStatus status;

  public GameBoard(Player player, WordleGame game) {
    this.player = Objects.requireNonNull(player);
    this.game = Objects.requireNonNull(game);
    this.rounds = new ArrayList<>();
    this.status = GameBoardStatus.PLAYING;
  }

  private GameBoard(
    Player player,
    WordleGame game,
    List<Round> rounds,
    GameBoardStatus status
  ) {
    this.player = Objects.requireNonNull(player);
    this.game = Objects.requireNonNull(game);
    this.rounds = new ArrayList<>(Objects.requireNonNull(rounds));
    this.status = Objects.requireNonNull(status);
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

    if (!Objects.requireNonNull(currentTime).isBefore(game.getEnd())) {
      status = GameBoardStatus.EXPIRED;
    }
  }

  public Player getPlayer() {
    return player;
  }

  public WordleGame getGame() {
    return game;
  }

  public Word getCorrect() {
    return game.getCorrect();
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
      game,
      player,
      new TryCount(rounds.size()),
      status
    );
  }
}
