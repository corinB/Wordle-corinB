package infrastructure.persistence.repository.impl;

import domain.model.vo.Word;
import domain.repository.WordRepository;
import infrastructure.persistence.entity.WordEntity;
import infrastructure.persistence.repository.WordJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements WordRepository {

  private final WordJPARepository wordJPARepository;

  @Override
  public Optional<Word> findByWord(Word word) {
    return wordJPARepository.findByValue(word.value())
      .map(WordEntity::toDomain);
  }

  @Override
  public Word save(Word word) {
    return wordJPARepository.save(WordEntity.create(word)).toDomain();
  }

  @Override
  public void delete(Word word) {
    wordJPARepository.deleteByValue(word.value());
  }

  @Override
  public List<Word> findAll() {
    return wordJPARepository.findAll().stream()
      .map(WordEntity::toDomain)
      .toList();
  }
}
