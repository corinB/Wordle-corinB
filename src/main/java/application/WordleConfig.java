package application;

import domain.model.WordleGame;
import domain.repository.WordRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Configuration
public class WordleConfig {

  @Bean
  public Scanner scanner() {
    return new Scanner(
      System.in,
      StandardCharsets.UTF_8
    );
  }

  @Bean
  public PrintStream output() {
    return new PrintStream(
      System.out,
      true,
      StandardCharsets.UTF_8
    );
  }

  @Bean
  public WordleGame wordleGame(
    WordRepository wordRepository
  ) {
    return new WordleGame(
      wordRepository.findAll()
    );
  }
}
