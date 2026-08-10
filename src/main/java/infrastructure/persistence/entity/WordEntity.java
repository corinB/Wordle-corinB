package infrastructure.persistence.entity;

import domain.vo.Word;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "\"value\"", unique = true, nullable = false)
  @Getter
  private String value;

  // Domain -> Entity
  public static WordEntity create(Word word) {
    WordEntity entity = new WordEntity();
    entity.value = word.value();
    return entity;
  }

  // Entity -> Domain
  public Word toDomain() {
    return new Word(this.value);
  }
}
