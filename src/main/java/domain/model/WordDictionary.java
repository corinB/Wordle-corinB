package domain.model;

import domain.repository.WordRepository;

import java.util.Objects;
import java.util.Random;

public class WordDictionary<T extends WordRepository> {


  private final T wordRepository;

  public WordDictionary(T wordRepository) {
    this.wordRepository = Objects.requireNonNull(wordRepository);
  }

  public Word chooseCorrectWord(long seed){
    Random random = new Random(seed);
    return new Word(
      wordRepository.getAllWords().get(random.nextInt(wordRepository.getTotalWordsCNT()))
    );
  }
}

