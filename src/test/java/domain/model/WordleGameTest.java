package domain.model;
import domain.model.vo.Word;
import java.util.List;
import java.util.stream.Stream;

public class WordleGameTest {

  private final List<Word> words = Stream.of(
    "apple",
    "cocoa",
    "mania",
    "radar",
    "green"
  ).map(Word::new).toList();

  private final Word wrongAnswer = new Word("hello");


}

class FakeSelector implements CorrectSelector{
  @Override
  public Word select(List<Word> words) {
    return words.getFirst();
  }
}
