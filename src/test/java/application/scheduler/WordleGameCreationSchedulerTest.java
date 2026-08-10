package application.scheduler;

import application.service.GameLifecycleService;
import application.service.PlayingGameService;
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
    PlayingGameService playingGameService =
      mock(PlayingGameService.class);
    WordleGameCreationScheduler scheduler =
      new WordleGameCreationScheduler(
        gameLifecycleService,
        playingGameService
      );

    scheduler.startNewGame();

    verify(playingGameService).expireAllEndedGames();
    verify(gameLifecycleService).startNewGame();
  }
}
