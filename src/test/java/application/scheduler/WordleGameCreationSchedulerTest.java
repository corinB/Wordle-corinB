package application.scheduler;

import application.service.GameLifecycleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WordleGameCreationSchedulerTest {

  @Test
  @DisplayName("Scheduler starts a new game once triggered")
  void startNewGameDelegatesToService() {
    GameLifecycleService gameLifecycleService =
      mock(GameLifecycleService.class);
    WordleGameCreationScheduler scheduler =
      new WordleGameCreationScheduler(gameLifecycleService);

    scheduler.startNewGame();

    verify(gameLifecycleService).startNewGame();
  }
}
