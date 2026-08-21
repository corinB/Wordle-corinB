package infrastructure.persistence.repository.impl;

import domain.model.WordleGame;
import domain.repository.WordleGameRepository;
import infrastructure.persistence.entity.WordleGameEntity;
import infrastructure.persistence.repository.WordleGameJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WordleGameRepositoryImpl implements WordleGameRepository {

  private final WordleGameJPARepository wordleGameJPARepository;

  @Override
  @Transactional
  public WordleGame save(WordleGame wordleGame) {
    return wordleGameJPARepository
      .save(WordleGameEntity.create(wordleGame))
      .toDomain();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<WordleGame> findById(Long id) {
    return wordleGameJPARepository.findById(id)
      .map(WordleGameEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<WordleGame> findByStartAt(LocalDateTime startAt) {
    return wordleGameJPARepository.findByStartAt(startAt)
      .map(WordleGameEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<WordleGame> findAllEndedBefore(LocalDateTime currentTime) {
    return wordleGameJPARepository
      .findAllByEndAtLessThanEqual(currentTime)
      .stream()
      .map(WordleGameEntity::toDomain)
      .toList();
  }
}
