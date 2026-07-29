package domain.model;

import domain.repository.WordRepository;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WordDictionary {



  private final WordRepository wordRepository;

  public WordDictionary(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public Word chooseCorrectWord(long seed) {
    List<String> words = wordRepository.getAllWords();
    Random random = new Random(seed);

    return new Word(
      words.get(random.nextInt(words.size()))
    );
  }
}

