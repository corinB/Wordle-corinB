package application;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class wordleServiceTest {

  @Test
  @DisplayName("정답 검증 테스트 정답: apple")
  void submitCorrectTest() {
    WordleService wordleService = new WordleService();

    assertThat(wordleService.submit("apple")).isTrue();
  }


}
