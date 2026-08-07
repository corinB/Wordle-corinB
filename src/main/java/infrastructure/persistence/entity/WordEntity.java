package infrastructure.persistence.entity;

import domain.model.vo.Word;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // PRIVATE -> PROTECTED로 변경
public class WordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "value", unique = true, nullable = false)
  @Getter
  private String value;

  // Domain -> Entity
  public static WordEntity create(Word word) {
    WordEntity entity = new WordEntity();
    entity.value = word.value();
    return entity;
  }

  public static WordEntity create(String value){
    return create(new Word(value));
  }

  // Entity -> Domain
  public Word toDomain() {
    return new Word(this.value);
  }
}
