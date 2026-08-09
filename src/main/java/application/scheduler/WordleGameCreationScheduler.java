package application.scheduler;

import application.service.GameLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordleGameCreationScheduler {

  private final GameLifecycleService gameLifecycleService;

  @Scheduled(cron = "${wordle.game.creation-cron}")
  public void startNewGame() {
    gameLifecycleService.startNewGame();
  }
}
