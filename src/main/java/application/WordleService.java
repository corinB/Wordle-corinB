package application;

import domain.model.GameBoard;
import domain.model.Word;
import domain.model.WordDictionary;
import domain.repository.WordRepository;

import java.time.Instant;
import java.util.List;

public class WordleService {

  private final WordDictionary dictionary;
  private GameBoard gameBoard;

  public WordleService(WordRepository wordRepository) {
    this.dictionary = new WordDictionary(wordRepository);
  }

  public void gameStart() {
    long seed = Instant.now().getEpochSecond();
    Word correct = dictionary.chooseCorrectWord(seed);

    gameBoard = new GameBoard(correct);
  }

  public void submit(Word answer) {
    gameBoard.submit(answer);
  }

  public boolean isFinished() {
    return !gameBoard.canSubmit();
  }

  public boolean isCorrect() {
    return gameBoard.isCorrect();
  }

  public Word getCorrect() {
    return gameBoard.getCorrect();
  }

  public List<String> getRecords() {
    return gameBoard.getRecords();
  }

  public int getSpentChance() {
    return gameBoard.getSpentChance();
  }
}
