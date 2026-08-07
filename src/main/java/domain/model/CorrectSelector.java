package domain.model;

import domain.model.vo.Word;

import java.util.List;

public interface CorrectSelector {

  public Word select(List<Word> words);
}
