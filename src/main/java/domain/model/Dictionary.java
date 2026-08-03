package domain.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


//WordDictionary 상테랑 행위만 가짐
//서비스에 넣으려다가 핵심비즈니스 인것 같아서 기능이 하나인거 같지만 도메인으로 뺌
public class Dictionary {

  private final List<Word> words;

  public Dictionary(List<Word> words) {
    this.words = words;
  }

  public Dictionary(final String... words) {
    this(Arrays.stream(words).map(Word::new).toList());
  }

  //씨드 기준으로 랜덤 단어 고르기
  public Word  correct(long seed) {
    final Random random = new Random(seed);
    return words.get(random.nextInt(words.size()));
  }

  public Word correct(){
    long seed = Instant.now().getEpochSecond();
    return  this.correct(seed);
  }
}

