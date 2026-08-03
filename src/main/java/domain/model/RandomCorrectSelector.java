package domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomCorrectSelector implements CorrectSelector{

  private final Random random;
  private Word correct;

  public RandomCorrectSelector() {
    long seed = Instant.now().getEpochSecond();
    this(seed);
  }

  public RandomCorrectSelector(long seed){
    this.random = new Random(seed);
  }

  @Override
  public Word select(List<Word> words) {
    if (Objects.isNull(correct)){
      correct = words.get(random.nextInt(words.size()));
    }
    return correct;
  }
}
