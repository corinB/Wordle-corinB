package presentation.controller;

import application.service.PlayerService;
import application.service.PlayingGameService;
import domain.model.GameBoard;
import domain.model.Player;
import domain.vo.GameHistory;
import domain.vo.Word;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import presentation.dto.SubmitAnswerRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/games/today")
public class GameController {

  private final PlayerService playerService;
  private final PlayingGameService playingGameService;

  @GetMapping
  public String today(HttpSession session, Model model) {
    Player player = findLoggedInPlayer(session);
    if (player == null) {
      return "redirect:/players/login";
    }

    addTodayModel(model, player, currentGameBoard(session), null);
    return "games/today";
  }

  @PostMapping("/join")
  public String join(HttpSession session, Model model) {
    Player player = findLoggedInPlayer(session);
    if (player == null) {
      return "redirect:/players/login";
    }

    try {
      GameBoard gameBoard = playingGameService.joinGame(player);
      session.setAttribute(SessionAttribute.GAME_BOARD, gameBoard);
      return "redirect:/games/today";
    } catch (IllegalArgumentException exception) {
      addTodayModel(model, player, currentGameBoard(session), exception.getMessage());
      return "games/today";
    }
  }

  @PostMapping("/answers")
  public String submitAnswer(
    @ModelAttribute("answerRequest") SubmitAnswerRequest request,
    HttpSession session,
    Model model
  ) {
    Player player = findLoggedInPlayer(session);
    if (player == null) {
      return "redirect:/players/login";
    }

    try {
      GameBoard gameBoard = playingGameService.submitAnswer(
        player,
        new Word(request.getAnswer())
      );
      session.setAttribute(SessionAttribute.GAME_BOARD, gameBoard);
      if (gameBoard.isFinished()) {
        return "redirect:/games/today/history";
      }
      return "redirect:/games/today";
    } catch (RuntimeException exception) {
      addTodayModel(model, player, currentGameBoard(session), exception.getMessage());
      return "games/today";
    }
  }

  @GetMapping("/history")
  public String history(HttpSession session, Model model) {
    Player player = findLoggedInPlayer(session);
    if (player == null) {
      return "redirect:/players/login";
    }

    GameBoard gameBoard = currentGameBoard(session);
    if (gameBoard == null) {
      return "redirect:/games/today";
    }

    try {
      GameHistory history = playingGameService.getHistory(gameBoard);
      model.addAttribute("player", player);
      model.addAttribute("history", history);
      return "games/history";
    } catch (RuntimeException exception) {
      addTodayModel(model, player, gameBoard, exception.getMessage());
      return "games/today";
    }
  }

  private Player findLoggedInPlayer(HttpSession session) {
    String email = (String) session.getAttribute(SessionAttribute.PLAYER_EMAIL);
    if (email == null) {
      return null;
    }

    try {
      return playerService.findByEmail(email);
    } catch (IllegalArgumentException exception) {
      session.invalidate();
      return null;
    }
  }

  private GameBoard currentGameBoard(HttpSession session) {
    return (GameBoard) session.getAttribute(SessionAttribute.GAME_BOARD);
  }

  private void addTodayModel(
    Model model,
    Player player,
    GameBoard gameBoard,
    String errorMessage
  ) {
    model.addAttribute("player", player);
    model.addAttribute("gameBoard", gameBoard);
    model.addAttribute("answerRequest", new SubmitAnswerRequest());
    if (errorMessage != null) {
      model.addAttribute("errorMessage", errorMessage);
    }
  }
}
