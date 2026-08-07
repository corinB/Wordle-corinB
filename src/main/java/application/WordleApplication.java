package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
  "application",
  "domain",
  "infrastructure",
  "presentation"
})
@EnableJpaRepositories(
  basePackages = "infrastructure.persistence.repository"
)
@EntityScan(
  basePackages = "infrastructure.persistence.entity"
)
public class WordleApplication {

  public static void main(String[] args) {
    SpringApplication.run(WordleApplication.class, args);
  }
}
