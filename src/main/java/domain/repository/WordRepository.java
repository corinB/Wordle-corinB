package domain.repository;

import domain.model.vo.Word;

import java.util.List;
import java.util.Optional;

public interface WordRepository {

  Optional<Word> findByWord(Word word);

  Word save(Word word);

  void delete(Word word);

  List<Word> findAll();
}
