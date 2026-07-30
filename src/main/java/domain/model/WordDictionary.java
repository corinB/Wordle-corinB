package domain.model;

import domain.repository.WordRepository;

import java.util.List;
import java.util.Objects;
import java.util.Random;
//서비스에 넣으려다가 핵심비즈니스 인것 같아서 기능이 하나인거 같지만 도메인으로 뺌
public class WordDictionary {

  private final WordRepository wordRepository;

  public WordDictionary(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  //씨드 기준으로 랜덤 단어 고르기
  public Word chooseCorrectWord(long seed) {
    List<String> words = wordRepository.getAllWords();
    Random random = new Random(seed);

    return new Word(
      words.get(random.nextInt(words.size()))
    );
  }
}

