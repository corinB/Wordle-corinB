package presentation;

import domain.model.WordleGame;
import domain.exception.InvalidWordException;
import domain.model.GameBoard;
import domain.model.vo.Word;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.Scanner;

import static presentation.GameScript.Script.END_NEGATIVE;
import static presentation.GameScript.Script.END_POSITIVE;
import static presentation.GameScript.Script.IN_PROGRESS;
import static presentation.GameScript.Script.START;
import static presentation.GameScript.Script.WARNING;

@Component
public class CLIController implements CommandLineRunner {
  private final WordleGame wordleGame;
  private final Scanner scanner;
  private final PrintStream output;

  public CLIController(
    WordleGame wordleGame,
    Scanner scanner,
    PrintStream output
  ) {
    this.wordleGame = wordleGame;
    this.scanner = scanner;
    this.output = output;
  }

  @Override
  public void run(String... args) {
    wordleGame.gameStart();
    printStartMessage();

    while (!wordleGame.isFinished()) {
      requestAnswer();
    }

    printResult();
    printRecords();
  }

  private void printStartMessage() {
    output.println(START.getMessage());
    output.println();
  }

  private void requestAnswer() {
    output.println(IN_PROGRESS.getMessage());
    submit(scanner.nextLine());
  }

  private void submit(String answer) {
    try {
      wordleGame.submit(new Word(answer));
      printInProgressRecords();
    } catch (InvalidWordException e) {
      printWarning();
    }
  }

  private void printInProgressRecords() {
    if (wordleGame.isFinished()) {
      return;
    }

    output.println();
    printRecords();
    output.println();
  }

  private void printWarning() {
    output.println();
    output.println(WARNING.getMessage());
    output.println();
  }

  private void printResult() {
    output.println();

    if (wordleGame.isCorrect()) {
      printScore();
      return;
    }

    output.println(END_NEGATIVE.getMessage());
    output.println();
  }

  private void printScore() {
    output.println(String.format(
      END_POSITIVE.getMessage(),
      wordleGame.getSpentChance(),
      GameBoard.MAX_CHANCE
    ));

    output.println();
  }

  private void printRecords() {
    wordleGame.getRecords()
      .forEach(output::println);
  }
}
