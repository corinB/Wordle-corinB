package presentation;

import domain.model.WordleGame;
import domain.exception.InvalidWordException;
import domain.model.GameBoard;
import domain.model.Word;
import domain.model.Words;

import java.io.PrintStream;
import java.util.Scanner;

import static presentation.GameScript.Script.END_NEGATIVE;
import static presentation.GameScript.Script.END_POSITIVE;
import static presentation.GameScript.Script.IN_PROGRESS;
import static presentation.GameScript.Script.START;
import static presentation.GameScript.Script.WARNING;

public class CLIController {

  private final WordleGame wordleGame;
  private final Scanner scanner;
  private final PrintStream output;

  public CLIController(Words repository, Scanner scanner, PrintStream output) {
    this.scanner = scanner;
    this.output = output;
    this.wordleGame = new WordleGame(repository
      .findAll().stream()
      .map(Word::new).toList()
    );
  }

  public void run() {
    //게임 시작
    wordleGame.gameStart();
    printStartMessage();

    //안끝났음 반복
    while (!wordleGame.isFinished()) {
      requestAnswer();
    }

    printResult();
    printRecords();
  }

  //사작 메시지 출력
  private void printStartMessage() {
    output.println(START.getMessage());
    output.println();
  }

  //정답 입력 요구 메시지 출력
  private void requestAnswer() {
    output.println(IN_PROGRESS.getMessage());
    submit(scanner.nextLine());
  }

  //이력된 정답 서비스로 전달
  private void submit(String answer) {
    try {
      wordleGame.submit(new Word(answer));
      printInProgressRecords();
    } catch (InvalidWordException e) {
      printWarning();
    }
  }

  // 채점 결고과 (아직 안끝났을때)
  private void printInProgressRecords() {
    if (wordleGame.isFinished()) {
      return;
    }

    output.println();
    printRecords();
    output.println();
  }

  //경고
  private void printWarning() {
    output.println();
    output.println(WARNING.getMessage());
    output.println();
  }

  //토탈 채점 결과 줄력
  private void printResult() {
    output.println();
    // 정답 마추면 결과 출력
    if (wordleGame.isCorrect()) {
      printScore();
      return;
    }
    output.println(END_NEGATIVE.getMessage());
    output.println();
  }

  //점수 출력 ex) 4/6
  private void printScore() {
    output.println(String.format(
      END_POSITIVE.getMessage(),
      wordleGame.getSpentChance(),
      GameBoard.MAX_CHANCE
    ));
    output.println();
  }

  //채점기록 출력
  private void printRecords() {
    wordleGame.getRecords()
      .forEach(output::println);
  }
}
