package domain.policy;

@FunctionalInterface
public interface PasswordMatcher { //비밀번호의 일치 여부를 판단하는 기능을 추상화

  boolean matches(
    String rawPassword,
    String encodedPassword
  );
}
