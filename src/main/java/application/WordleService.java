package application;

import domain.model.GameBoard;
import domain.model.Word;
import domain.model.Dictionary;
import domain.repository.WordRepository;

import java.time.Instant;
import java.util.List;

public class WordleService {

  private final Dictionary dictionary;
  private GameBoard gameBoard;

  public WordleService(WordRepository wordRepository) {
    this.dictionary = new Dictionary(
      wordRepository.getAllWords()
        .stream()
        .map(Word::new)
        .toList()
    );
  }

  //게임 시장
  public void gameStart() {
    //게임 셋팅(게임보드에 정답 기록)
    gameBoard = new GameBoard(dictionary.correct());
  }

  //응답 제출
  public void submit(Word answer) {
    gameBoard.submit(answer);
  }

  //종료 판단
  public boolean isFinished() {
    return !gameBoard.canSubmit();
  }

  //정답 판단
  public boolean isCorrect() {
    return gameBoard.isCorrect();
  }

  //정답 얻기: 테스트용
  public Word getCorrect() {
    return gameBoard.getCorrect();
  }

  //채점 기록
  public List<String> getRecords() {
    return gameBoard.getRecords();
  }

  //소비한 기회
  public int getSpentChance() {
    return gameBoard.getSpentChance();
  }
}
