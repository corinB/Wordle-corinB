package application.service;

import domain.model.Player;
import domain.policy.PasswordEncryptor;
import domain.policy.PasswordMatcher;
import domain.repository.PlayerRepository;
import domain.vo.Email;
import domain.vo.EncodedPassword;
import domain.vo.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static application.exception.ApplicationErrorType.DUPLICATED_EMAIL;
import static application.exception.ApplicationErrorType.DUPLICATED_NICKNAME;
import static application.exception.ApplicationErrorType.PASSWORD_DOES_NOT_MATCH;
import static application.exception.ApplicationErrorType.PLAYER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final PasswordEncryptor passwordEncryptor;
  private final PasswordMatcher passwordMatcher;

  public Player register(
    String nickname,
    String email,
    String rawPassword
  ) {
    Nickname playerNickname = new Nickname(nickname);
    Email playerEmail = new Email(email);

    validateDuplicateNickname(playerNickname);
    validateDuplicateEmail(playerEmail);

    EncodedPassword encodedPassword =
      passwordEncryptor.encrypt(rawPassword);

    return playerRepository.save(
      Player.create(
        playerNickname,
        playerEmail,
        encodedPassword
      )
    );
  }

  public Player login(String email, String rawPassword) {
    Player player = findByEmail(email);

    if (!player.matchesPassword(rawPassword, passwordMatcher)) {
      throw PASSWORD_DOES_NOT_MATCH.createException();
    }

    return player;
  }

  public Player findByEmail(String email) {
    return playerRepository.findByEmail(new Email(email))
      .orElseThrow(PLAYER_NOT_FOUND::createException);
  }

  private void validateDuplicateNickname(Nickname nickname) {
    playerRepository.findByNickname(nickname)
      .ifPresent(player -> {
        throw DUPLICATED_NICKNAME.createException();
      });
  }

  private void validateDuplicateEmail(Email email) {
    playerRepository.findByEmail(email)
      .ifPresent(player -> {
        throw DUPLICATED_EMAIL.createException();
      });
  }
}
