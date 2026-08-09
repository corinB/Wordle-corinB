package infrastructure.persistence.repository.impl;

import domain.model.WordleGame;
import domain.repository.WordleGameRepository;
import infrastructure.persistence.entity.WordEntity;
import infrastructure.persistence.entity.WordleGameEntity;
import infrastructure.persistence.repository.WordJPARepository;
import infrastructure.persistence.repository.WordleGameJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WordleGameRepositoryImpl implements WordleGameRepository {

  private final WordleGameJPARepository wordleGameJPARepository;
  private final WordJPARepository wordJPARepository;

  @Override
  @Transactional
  public WordleGame save(WordleGame wordleGame) {
    WordEntity correctEntity = findCorrectEntity(wordleGame);
    return wordleGameJPARepository
      .save(WordleGameEntity.create(wordleGame, correctEntity))
      .toDomain();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<WordleGame> findByStartAt(LocalDateTime startAt) {
    return wordleGameJPARepository.findByStartAt(startAt)
      .map(WordleGameEntity::toDomain);
  }

  private WordEntity findCorrectEntity(WordleGame wordleGame) {
    return wordJPARepository
      .findByValue(wordleGame.getCorrect().value())
      .orElseThrow(() ->
        new IllegalArgumentException("Correct word does not exist.")
      );
  }
}
