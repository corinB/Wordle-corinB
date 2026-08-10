package domain.repository;

import domain.vo.Word;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository {

  Optional<Word> findByWord(Word word);

  Word save(Word word);

  void delete(Word word);

  List<Word> findAll();
}
