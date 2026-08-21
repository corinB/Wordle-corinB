package domain.repository;

import domain.model.WordleGame;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WordleGameRepository {

  WordleGame save(WordleGame wordleGame);

  Optional<WordleGame> findById(Long id);

  Optional<WordleGame> findByStartAt(LocalDateTime startAt);

  List<WordleGame> findAllEndedBefore(LocalDateTime currentTime);
}
