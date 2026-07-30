package presentation;

import java.util.Arrays;
import java.util.List;

public class GameScript {

  public static List<String> getScript(Script... scripts) {
    return Arrays.stream(scripts)
      .map(Script::getMessage)
      .toList();
  }

  public enum Script {
    START("WORDLE을 6번 만에 맞춰 보세요.\n시도의 결과는 타일의 색 변화로 나타납니다."),
    IN_PROGRESS("정답을 입력해 주세요."),
    WARNING("올바른 값을 입력하세요."),
    END_POSITIVE("%d/%d"),
    END_NEGATIVE("게임이 종료되었습니다.");

    private final String message;

    Script(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
