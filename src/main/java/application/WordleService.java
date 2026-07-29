package application;

import domain.model.Word;
import domain.model.WordDictionary;
import domain.repository.WordRepository;

import java.time.Instant;

public class WordleService {

  private final WordDictionary dictionary;
  private Word correct;

  public WordleService(WordRepository wordRepository) {
    this.dictionary = new WordDictionary(wordRepository);
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
