package domain.model;

import java.util.ArrayList;
import java.util.List;

import static domain.exception.DomainErrorType.GAME_ALREADY_FINISHED;

//게임 현황(진행 상황)을 나타내는 도메인
public class GameBoard {

  //최대 도전 기회
  public static final int MAX_CHANCE = 6;

  //정답
  private final Word correct;
  //채점 기록
  private final List<String> records;
  //정답 맞췄는지 여부
  private boolean correctFlag;

  public GameBoard(Word correct) {
    this.correct = correct;
    this.records = new ArrayList<>();
    this.correctFlag = false;
  }

  //응답 제출
  public void submit(Word answer) {
    if (!canSubmit()) {
      throw GAME_ALREADY_FINISHED.createException();
    }

    records.add(correct.compare(answer));

    if (correct.equals(answer)) {
      correctFlag = true;
    }
  }

  //종료 판단: 제출 가능한 상태인가?
  public boolean canSubmit() {
    return !correctFlag && records.size() < MAX_CHANCE;
  }

  //종료 판단: 정답을 맞추었는가?
  public boolean isCorrect() {
    return correctFlag;
  }

  //정답 얻기(테스트용)
  public Word getCorrect() {
    return correct;
  }

  //소비한 기회 몇번인지
  public int getSpentChance() {
    return records.size();
  }

  //지금까지의 채점 기록 얻기
  public List<String> getRecords() {
    return List.copyOf(records);
  }
}
