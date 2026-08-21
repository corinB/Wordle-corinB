package infrastructure.persistence.repository.impl;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.repository.GameBoardRepository;
import domain.vo.Round;
import infrastructure.persistence.entity.GameBoardEntity;
import infrastructure.persistence.entity.GameBoardRoundEntity;
import infrastructure.persistence.repository.GameBoardJPARepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    GameBoardEntity savedEntity = gameBoardJPARepository.saveAndFlush(entity);
    appendNewRounds(savedEntity.getId(), gameBoard.getRounds());

    return savedEntity.toDomain(gameBoard.getRounds());
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
      .map(entity -> entity.toDomain(findRoundsByGameBoardId(entity.getId())));
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<GameBoard> findAllPlayingByWordleGameIds(
    List<Long> wordleGameIds
  ) {
    if (wordleGameIds.isEmpty()) {
      return List.of();
    }

    List<GameBoardEntity> entities = gameBoardJPARepository
      .findAllByStatusAndWordleGameIdIn(
        GameBoardStatus.PLAYING,
        wordleGameIds
      );

    Map<Long, List<Round>> roundsByGameBoardId =
      findRoundsByGameBoardIds(
        entities.stream().map(GameBoardEntity::getId).toList()
      );

    return entities.stream()
      .map(entity -> entity.toDomain(
        roundsByGameBoardId.getOrDefault(entity.getId(), List.of())
      ))
      .toList();
  }

  private void appendNewRounds(
    Long gameBoardId,
    List<Round> domainRounds
  ) {
    int savedRoundCount = countRoundsByGameBoardId(gameBoardId);

    domainRounds.stream()
      .skip(savedRoundCount)
      .map(round -> GameBoardRoundEntity.create(gameBoardId, round))
      .forEach(entityManager::persist);
  }

  private int countRoundsByGameBoardId(Long gameBoardId) {
    Long count = entityManager.createQuery(
      """
      SELECT COUNT(round)
      FROM GameBoardRoundEntity round
      WHERE round.gameBoardId = :gameBoardId
      """,
      Long.class
    ).setParameter("gameBoardId", gameBoardId)
      .getSingleResult();

    return count.intValue();
  }

  private List<Round> findRoundsByGameBoardId(Long gameBoardId) {
    return findRoundEntitiesByGameBoardIds(List.of(gameBoardId))
      .stream()
      .map(GameBoardRoundEntity::toDomain)
      .toList();
  }

  private Map<Long, List<Round>> findRoundsByGameBoardIds(
    List<Long> gameBoardIds
  ) {
    if (gameBoardIds.isEmpty()) {
      return Map.of();
    }

    return findRoundEntitiesByGameBoardIds(gameBoardIds)
      .stream()
      .collect(Collectors.groupingBy(
        GameBoardRoundEntity::getGameBoardId,
        HashMap::new,
        Collectors.mapping(GameBoardRoundEntity::toDomain, Collectors.toList())
      ));
  }

  private List<GameBoardRoundEntity> findRoundEntitiesByGameBoardIds(
    List<Long> gameBoardIds
  ) {
    return entityManager.createQuery(
      """
      SELECT round
      FROM GameBoardRoundEntity round
      WHERE round.gameBoardId IN :gameBoardIds
      ORDER BY round.gameBoardId ASC, round.roundIndex ASC
      """,
      GameBoardRoundEntity.class
    ).setParameter("gameBoardIds", gameBoardIds)
      .getResultList();
  }

  private SqlParameterSource toStatusUpdateParameter(GameBoard gameBoard) {
    return new MapSqlParameterSource()
      .addValue("status", gameBoard.getStatus().name())
      .addValue("playerId", gameBoard.getPlayerId())
      .addValue("wordleGameId", gameBoard.getWordleGameId());
  }
}
