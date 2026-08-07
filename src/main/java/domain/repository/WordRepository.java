package domain.repository;

import domain.model.vo.Word;

import java.util.List;
import java.util.Optional;

public interface WordRepository {

  Optional<Word> findByWord(String value);
  Optional<Word> findByWord(Word word);

  Word save(String value);
  Word save(Word word);


  void delete(String value);
  void delete(Word word);

  // words.txt 단어 전체 조회
  List<Word> findAll();
}
