package application.scheduler;

import application.service.GameLifecycleService;
import application.service.PlayingGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordleGameCreationScheduler {

  private final GameLifecycleService gameLifecycleService;
  private final PlayingGameService playingGameService;

  @Scheduled(cron = "${wordle.game.creation-cron}")
  public void startNewGame() {

    playingGameService.expireAllEndedGames();

    gameLifecycleService.startNewGame();
  }
}
