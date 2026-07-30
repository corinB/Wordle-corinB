package infrastructure;

import domain.repository.WordRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static infrastructure.exception.InfrastructureErrorType.WORD_FILE_NOT_FOUND;
import static infrastructure.exception.InfrastructureErrorType.WORD_FILE_READ_FAILED;

public class WordRepositoryImpl implements WordRepository {

  private static final String WORD_FILE_PATH = "/words.txt";

  @Override
  public List<String> getAllWords() {
    InputStream inputStream =
      WordRepositoryImpl.class.getResourceAsStream(WORD_FILE_PATH);

    if (inputStream == null) {
      throw WORD_FILE_NOT_FOUND.createException(WORD_FILE_PATH);
    }

    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(inputStream, StandardCharsets.UTF_8)
    )) {
      return reader.lines()
        .map(String::trim)
        .filter(word -> !word.isBlank())
        .map(String::toLowerCase)
        .toList();

    } catch (IOException e) {
      throw WORD_FILE_READ_FAILED.createException(e);
    }
  }
}
