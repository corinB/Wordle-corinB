package application;

import domain.model.Word;
import domain.model.WordDictionary;
import domain.repository.WordRepository;

import java.time.Instant;

public class WordleService<T extends WordRepository> {

  private final WordDictionary<T> dictionary;
  private Word correct;

  public WordleService(WordDictionary<T> dictionary) {
    this.dictionary = dictionary;
  }

  void gameStart(){
    long seed = Instant.now().getEpochSecond();
    correct = dictionary.chooseCorrectWord(seed);
  }

  boolean submit(Word answer){
    return correct.equals(answer);
  }

  public Word getCorrect() {
    return correct;
  }
}
