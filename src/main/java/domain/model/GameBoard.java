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

// 게임 진행 상태
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

  //entity to Domain 용
  public static GameBoard restore(
    Player player,
    WordleGame game,
    List<Round> rounds,
    GameBoardStatus status
  ) {
    return new GameBoard(player, game, rounds, status);
  }

  //답안 제출
  public void submit(Word answer) {
    // 끝난 게임에 대한 보드인지 검증
    if (!canSubmit()) {
      throw GAME_ALREADY_FINISHED.createException();
    }

    Word submittedAnswer = Objects.requireNonNull(answer);
    //새 라운드 추가
    rounds.add(new Round(rounds.size(), submittedAnswer, getCorrect()));
    //게임 상태 업데이트
    updateStatus(submittedAnswer);
  }

  //게임 종료(만료) 처리
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

  //게임 기록 얻기
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
