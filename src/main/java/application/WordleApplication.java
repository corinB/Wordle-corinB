package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
  "application",
  "domain",
  "infrastructure",
  "presentation"
})
public class WordleApplication {

  public static void main(String[] args) {
    SpringApplication.run(WordleApplication.class, args);
  }
}
