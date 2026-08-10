package infrastructure.persistence.repository.impl;

import domain.model.GameBoard;
import domain.model.GameBoardStatus;
import domain.model.Player;
import domain.model.WordleGame;
import domain.repository.GameBoardRepository;
import infrastructure.persistence.entity.GameBoardEntity;
import infrastructure.persistence.entity.PlayerEntity;
import infrastructure.persistence.entity.WordleGameEntity;
import infrastructure.persistence.repository.GameBoardJPARepository;
import infrastructure.persistence.repository.PlayerJPARepository;
import infrastructure.persistence.repository.WordleGameJPARepository;
import lombok.RequiredArgsConstructor;
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

  @Override
  @Transactional
  public GameBoard save(GameBoard gameBoard) {
    PlayerEntity playerEntity = findPlayerEntity(gameBoard.getPlayer());
    WordleGameEntity gameEntity = findGameEntity(gameBoard.getGame());

    GameBoardEntity entity = gameBoardJPARepository
      .findByPlayerAndGame(playerEntity, gameEntity)
      .map(foundEntity -> {
        foundEntity.update(gameBoard);
        return foundEntity;
      })
      .orElseGet(() ->
        GameBoardEntity.create(playerEntity, gameEntity, gameBoard)
      );

    return gameBoardJPARepository.save(entity).toDomain();
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
      .findByPlayerAndGame(playerEntity, gameEntity)
      .map(GameBoardEntity::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<GameBoard> findAllPlayingBoardsEndedBefore(
    LocalDateTime currentTime
  ) {
    return gameBoardJPARepository
      .findAllByStatusAndGame_EndAtLessThanEqual(
        GameBoardStatus.PLAYING,
        currentTime
      )
      .stream()
      .map(GameBoardEntity::toDomain)
      .toList();
  }

  private PlayerEntity findPlayerEntity(Player player) {
    return playerJPARepository
      .findByEmail(player.getEmail().value())
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
}
