package infrastructure.security;

import domain.policy.PasswordEncryptor;
import domain.policy.PasswordMatcher;
import domain.vo.EncodedPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static domain.exception.DomainErrorType.PASSWORD_REQUIRED;

@Component
public class BCryptPasswordEncryptor
  implements PasswordEncryptor, PasswordMatcher {

  private final BCryptPasswordEncoder passwordEncoder =
    new BCryptPasswordEncoder();

  @Override
  public EncodedPassword encrypt(String rawPassword) {
    if (rawPassword == null || rawPassword.isBlank()) {
      throw PASSWORD_REQUIRED.createException();
    }

    return new EncodedPassword(passwordEncoder.encode(rawPassword));
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
