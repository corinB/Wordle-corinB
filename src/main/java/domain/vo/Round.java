package domain.vo;

public record Round(int index, Word answer, String compare) {
  public Round(
    int index,
    Word answer,
    Word correct
  ) {
    this(index, answer, correct.compare(answer));
  }
}
