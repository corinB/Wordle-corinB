import application.WordleService;
import infrastructure.WordRepositoryImpl;
import presentation.CLIController;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    WordleService wordleService =
      new WordleService(new WordRepositoryImpl());
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    PrintStream output =
      new PrintStream(System.out, true, StandardCharsets.UTF_8);
    CLIController controller =
      new CLIController(wordleService, scanner, output);

    controller.run();
  }
}
