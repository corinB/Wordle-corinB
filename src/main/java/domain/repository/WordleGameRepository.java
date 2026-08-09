package domain.repository;

import domain.model.WordleGame;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WordleGameRepository {

  WordleGame save(WordleGame wordleGame);

  Optional<WordleGame> findByStartAt(LocalDateTime startAt);
}
