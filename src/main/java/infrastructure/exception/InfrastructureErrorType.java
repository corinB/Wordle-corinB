package infrastructure.exception;

public enum InfrastructureErrorType {

  WORD_FILE_NOT_FOUND("단어 파일을 찾을 수 없습니다: %s"),
  WORD_FILE_READ_FAILED("단어 파일을 읽지 못했습니다.");

  private final String message;

  InfrastructureErrorType(String message) {
    this.message = message;
  }

  // 인프라 오류별 메시지 포맷과 원인 예외 보존 방식을 한 곳에서 관리한다.
  public WordFileException createException(String value) {
    return new WordFileException(String.format(message, value));
  }

  public WordFileException createException(Throwable cause) {
    return new WordFileException(message, cause);
  }

  public String getMessage() {
    return message;
  }
}
