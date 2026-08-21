package infrastructure.persistence.repository.impl;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.repository.GameBoardRepository;
import infrastructure.persistence.entity.GameBoardEntity;
import infrastructure.persistence.repository.GameBoardJPARepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameBoardRepositoryImpl implements GameBoardRepository {

  private final GameBoardJPARepository gameBoardJPARepository;
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final EntityManager entityManager;

  @Override
  @Transactional
  public GameBoard save(GameBoard gameBoard) {
    GameBoardEntity entity = gameBoardJPARepository
      .findByPlayerIdAndWordleGameId(
        gameBoard.getPlayerId(),
        gameBoard.getWordleGameId()
      )
      .map(foundEntity -> {
        foundEntity.update(gameBoard);
        return foundEntity;
      })
      .orElseGet(() -> GameBoardEntity.create(gameBoard));

    return gameBoardJPARepository.save(entity).toDomain();
  }

  @Override
  @Transactional
  public void saveAll(List<GameBoard> gameBoards) {
    if (gameBoards.isEmpty()) {
      return;
    }

    entityManager.flush();
    jdbcTemplate.batchUpdate(
      """
      UPDATE game_boards
      SET status = :status
      WHERE player_id = :playerId
        AND wordle_game_id = :wordleGameId
      """,
      gameBoards.stream()
        .map(this::toStatusUpdateParameter)
        .toArray(SqlParameterSource[]::new)
    );
    entityManager.clear();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public Optional<GameBoard> findByPlayerIdAndWordleGameId(
    Long playerId,
    Long wordleGameId
  ) {
    return gameBoardJPARepository
      .findByPlayerIdAndWordleGameId(playerId, wordleGameId)
      .map(GameBoardEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<GameBoard> findAllPlayingByWordleGameIds(
    List<Long> wordleGameIds
  ) {
    if (wordleGameIds.isEmpty()) {
      return List.of();
    }

    return gameBoardJPARepository
      .findAllByStatusAndWordleGameIdIn(
        GameBoardStatus.PLAYING,
        wordleGameIds
      )
      .stream()
      .map(GameBoardEntity::toDomain)
      .toList();
  }

  private SqlParameterSource toStatusUpdateParameter(GameBoard gameBoard) {
    return new MapSqlParameterSource()
      .addValue("status", gameBoard.getStatus().name())
      .addValue("playerId", gameBoard.getPlayerId())
      .addValue("wordleGameId", gameBoard.getWordleGameId());
  }
}
