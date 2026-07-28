package domain.model;

import domain.repository.WordRepository;
import java.util.Random;

public class WordDictionary {

  private final WordRepository wordRepository;

  public WordDictionary(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public Word chooseCorrectWord(long seed){
    Random random = new Random(seed);
    return new Word(
      wordRepository.getAllWords().get(random.nextInt(wordRepository.getTotalWordsCNT()))
    );
  }
}
