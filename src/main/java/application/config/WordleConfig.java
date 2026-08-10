package application.config;

import domain.policy.CorrectSelector;
import domain.policy.impl.RandomCorrectSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WordleConfig {
  @Bean
  public CorrectSelector correctSelector(){
    return new RandomCorrectSelector();
  }
}
