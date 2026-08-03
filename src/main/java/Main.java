import infrastructure.WordsImpl;
import presentation.CLIController;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    PrintStream output =
      new PrintStream(System.out, true, StandardCharsets.UTF_8);
    CLIController controller =
      new CLIController(new WordsImpl(), scanner, output);

    controller.run();
  }
}
