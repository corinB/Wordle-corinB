package domain.policy;

import domain.vo.EncodedPassword;

@FunctionalInterface
public interface PasswordEncryptor {

  EncodedPassword encrypt(String rawPassword);
}
