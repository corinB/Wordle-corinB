package domain.repository;

import java.util.List;

public interface WordRepository {
  // words.txt 단어 전체 조회
  List<String> getAllWords();
}
