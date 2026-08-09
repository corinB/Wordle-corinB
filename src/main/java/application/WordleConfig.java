package application;

import domain.model.CorrectSelector;
import domain.model.RandomCorrectSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WordleConfig {
  @Bean
  public CorrectSelector correctSelector(){
    return new RandomCorrectSelector();
  }
}
