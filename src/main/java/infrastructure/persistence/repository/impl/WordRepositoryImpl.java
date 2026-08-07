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
  public Optional<Word> findByWord(String value) {
    return wordJPARepository.findByValue(value).map(WordEntity::toDomain);
  }

  @Override
  public Optional<Word> findByWord(Word word) {
    return findByWord(word.value());
  }

  @Override
  public Word save(String value) {
    return wordJPARepository.save(WordEntity.create(value)).toDomain();
  }

  @Override
  public Word save(Word word) {
    return save(word.value());
  }

  @Override
  public void delete(String value) {
    wordJPARepository.deleteByValue(value);
  }

  @Override
  public void delete(Word word) {
    delete(word.value());
  }

  @Override
  public List<Word> findAll() {
    return wordJPARepository.findAll().stream().map(WordEntity::toDomain).toList();
  }
}
