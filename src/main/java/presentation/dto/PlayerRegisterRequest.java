package presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayerRegisterRequest {

  private String nickname;
  private String email;
  private String password;

}
