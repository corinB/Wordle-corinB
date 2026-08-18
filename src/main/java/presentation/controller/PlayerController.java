package presentation.controller;

import application.service.PlayerService;
import domain.model.Player;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import presentation.dto.PlayerLoginRequest;
import presentation.dto.PlayerRegisterRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {

  private final PlayerService playerService;

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("registerRequest", new PlayerRegisterRequest());
    return "players/register";
  }

  @PostMapping("/register")
  public String register(
    @ModelAttribute("registerRequest") PlayerRegisterRequest request,
    HttpSession session,
    Model model
  ) {
    try {
      Player player = playerService.register(
        request.getNickname(),
        request.getEmail(),
        request.getPassword()
      );
      session.setAttribute(
        SessionAttribute.PLAYER_EMAIL,
        player.getEmail().value()
      );
      return "redirect:/games/today";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("errorMessage", exception.getMessage());
      return "players/register";
    }
  }

  @GetMapping("/login")
  public String loginPage(Model model) {
    model.addAttribute("loginRequest", new PlayerLoginRequest());
    return "players/login";
  }

  @PostMapping("/login")
  public String login(
    @ModelAttribute("loginRequest") PlayerLoginRequest request,
    HttpSession session,
    Model model
  ) {
    try {
      Player player = playerService.login(
        request.getEmail(),
        request.getPassword()
      );
      session.setAttribute(
        SessionAttribute.PLAYER_EMAIL,
        player.getEmail().value()
      );
      return "redirect:/games/today";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("errorMessage", exception.getMessage());
      return "players/login";
    }
  }

  @PostMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }
}
