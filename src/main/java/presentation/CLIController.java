package presentation;

import application.WordleService;
import domain.model.GameBoard;
import domain.model.Word;

import java.io.PrintStream;
import java.util.Scanner;

import static presentation.GameScript.Script.END_NEGATIVE;
import static presentation.GameScript.Script.END_POSITIVE;
import static presentation.GameScript.Script.IN_PROGRESS;
import static presentation.GameScript.Script.START;
import static presentation.GameScript.Script.WARNING;

public class CLIController {

  private final WordleService wordleService;
  private final Scanner scanner;
  private final PrintStream output;

  public CLIController(WordleService wordleService, Scanner scanner, PrintStream output) {
    this.wordleService = wordleService;
    this.scanner = scanner;
    this.output = output;
  }

  public void run() {
    wordleService.gameStart();
    printStartMessage();

    while (!wordleService.isFinished()) {
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
      wordleService.submit(new Word(answer));
      printInProgressRecords();
    } catch (IllegalArgumentException e) {
      printWarning();
    }
  }

  private void printInProgressRecords() {
    if (wordleService.isFinished()) {
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
    if (wordleService.isCorrect()) {
      printScore();
      return;
    }
    output.println(END_NEGATIVE.getMessage());
    output.println();
  }

  private void printScore() {
    output.println(String.format(
      END_POSITIVE.getMessage(),
      wordleService.getSpentChance(),
      GameBoard.MAX_CHANCE
    ));
    output.println();
  }

  private void printRecords() {
    wordleService.getRecords()
      .forEach(output::println);
  }
}
