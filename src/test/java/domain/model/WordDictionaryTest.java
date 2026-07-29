package domain.model;

import domain.repository.WordRepository;
import infrastructure.WordRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WordDictionaryTest {

  @Test
  @DisplayName("지금 시간을 기준으로 랜덤 단어 선택")
  void chooseCorrectWordTest(){

    long seed1 = Instant.now().getEpochSecond();
    long seed2 = Instant.now().plusSeconds(1).getEpochSecond();

    WordDictionary dictionary = new WordDictionary(new WordRepositoryImpl());

    Word word1 = dictionary.chooseCorrectWord(seed1);
    Word word2 = dictionary.chooseCorrectWord(seed1);
    Word word3 = dictionary.chooseCorrectWord(seed2);

    assertThat(word1).isEqualTo(word2);
    assertThat(word1).isNotEqualTo(word3);
  }

}
