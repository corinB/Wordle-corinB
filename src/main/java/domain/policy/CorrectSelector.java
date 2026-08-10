package domain.policy;

import domain.vo.Word;

import java.util.List;

public interface CorrectSelector {

  public Word select(List<Word> words);
}
