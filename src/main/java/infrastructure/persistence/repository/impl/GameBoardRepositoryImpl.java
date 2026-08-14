package infrastructure.persistence.repository.impl;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.model.Player;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import domain.vo.Nickname;
import infrastructure.persistence.entity.GameBoardEntity;
import infrastructure.persistence.entity.PlayerEntity;
import infrastructure.persistence.entity.WordleGameEntity;
import infrastructure.persistence.repository.GameBoardJPARepository;
import infrastructure.persistence.repository.PlayerJPARepository;
import infrastructure.persistence.repository.WordleGameJPARepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameBoardRepositoryImpl implements GameBoardRepository {

  private final GameBoardJPARepository gameBoardJPARepository;
  private final PlayerJPARepository playerJPARepository;
  private final WordleGameJPARepository wordleGameJPARepository;
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final EntityManager entityManager;

  @Override
  @Transactional
  public GameBoard save(GameBoard gameBoard) {
    PlayerEntity playerEntity = findPlayerEntity(gameBoard.getNickname());
    WordleGameEntity gameEntity = findGameEntity(gameBoard.getDeadLine());

    GameBoardEntity entity = gameBoardJPARepository
      .findByPlayerIdAndWordleGameId(
        playerEntity.getId(),
        gameEntity.getId()
      )
      .map(foundEntity -> {
        foundEntity.update(gameBoard);
        return foundEntity;
      })
      .orElseGet(() ->
        GameBoardEntity.create(playerEntity, gameEntity, gameBoard)
      );

    return gameBoardJPARepository.save(entity)
      .toDomain(playerEntity, gameEntity);
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
  @Transactional(readOnly = true)
  public Optional<GameBoard> findByPlayerAndGame(
    Player player,
    WordleGame game
  ) {
    PlayerEntity playerEntity = findPlayerEntity(player);
    WordleGameEntity gameEntity = findGameEntity(game);

    return gameBoardJPARepository
      .findByPlayerIdAndWordleGameId(
        playerEntity.getId(),
        gameEntity.getId()
      )
      .map(entity -> entity.toDomain(playerEntity, gameEntity));
  }

  @Override
  @Transactional(readOnly = true)
  public List<GameBoard> findAllPlayingBoardsEndedBefore(
    LocalDateTime currentTime
  ) {
    List<Long> endedGameIds = wordleGameJPARepository
      .findAllByEndAtLessThanEqual(currentTime)
      .stream()
      .map(WordleGameEntity::getId)
      .toList();

    if (endedGameIds.isEmpty()) {
      return List.of();
    }

    return gameBoardJPARepository
      .findAllByStatusAndWordleGameIdIn(
        GameBoardStatus.PLAYING,
        endedGameIds
      )
      .stream()
      .map(this::toDomain)
      .toList();
  }

  private PlayerEntity findPlayerEntity(Player player) {
    return playerJPARepository
      .findByEmail(player.getEmail().value())
      .orElseThrow(() ->
        new IllegalArgumentException("Player does not exist.")
      );
  }

  private PlayerEntity findPlayerEntity(Nickname nickname) {
    return playerJPARepository
      .findByNickname(nickname.value())
      .orElseThrow(() ->
        new IllegalArgumentException("Player does not exist.")
      );
  }

  private WordleGameEntity findGameEntity(WordleGame game) {
    return wordleGameJPARepository
      .findByStartAt(game.getStart())
      .orElseThrow(() ->
        new IllegalArgumentException("Wordle game does not exist.")
      );
  }

  private WordleGameEntity findGameEntity(LocalDateTime endAt) {
    return wordleGameJPARepository
      .findByEndAt(endAt)
      .orElseThrow(() ->
        new IllegalArgumentException("Wordle game does not exist.")
      );
  }

  private PlayerEntity findPlayerEntity(Long playerId) {
    return playerJPARepository
      .findById(playerId)
      .orElseThrow(() ->
        new IllegalArgumentException("Player does not exist.")
      );
  }

  private WordleGameEntity findGameEntity(Long wordleGameId) {
    return wordleGameJPARepository
      .findById(wordleGameId)
      .orElseThrow(() ->
        new IllegalArgumentException("Wordle game does not exist.")
      );
  }

  private GameBoard toDomain(GameBoardEntity entity) {
    return entity.toDomain(
      findPlayerEntity(entity.getPlayerId()),
      findGameEntity(entity.getWordleGameId())
    );
  }

  private SqlParameterSource toStatusUpdateParameter(GameBoard gameBoard) {
    PlayerEntity playerEntity = findPlayerEntity(gameBoard.getNickname());
    WordleGameEntity gameEntity = findGameEntity(gameBoard.getDeadLine());

    return new MapSqlParameterSource()
      .addValue("status", gameBoard.getStatus().name())
      .addValue("playerId", playerEntity.getId())
      .addValue("wordleGameId", gameEntity.getId());
  }
}
