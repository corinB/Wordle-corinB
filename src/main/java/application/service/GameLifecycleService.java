package application.service;

import domain.policy.CorrectSelector;
import domain.model.WordleGame;
import domain.repository.WordRepository;
import domain.repository.WordleGameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameLifecycleService {

  private final CorrectSelector correctSelector;
  private final WordRepository wordRepository;
  private final WordleGameRepository wordleGameRepository;

  public WordleGame startNewGame() {
    WordleGame wordleGame = new WordleGame(
      wordRepository.findAll(),
      correctSelector
    );
    return wordleGameRepository.save(wordleGame);
  }
}
