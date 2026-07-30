package infrastructure.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InfrastructureErrorTypeTest {

  @Test
  @DisplayName("단어 파일 없음 에러는 경로가 포함된 WordFileException 생성")
  void wordFileNotFoundCreatesException() {
    WordFileException exception =
      InfrastructureErrorType.WORD_FILE_NOT_FOUND.createException("/words.txt");

    assertThat(exception)
      .hasMessage("단어 파일을 찾을 수 없습니다: /words.txt");
  }

  @Test
  @DisplayName("단어 파일 읽기 실패 에러는 원인 예외를 보존")
  void wordFileReadFailedCreatesException() {
    IOException cause = new IOException("failed");

    WordFileException exception =
      InfrastructureErrorType.WORD_FILE_READ_FAILED.createException(cause);

    assertThat(exception)
      .hasMessage("단어 파일을 읽지 못했습니다.")
      .hasCause(cause);
  }
}
