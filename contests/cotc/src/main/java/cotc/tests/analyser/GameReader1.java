package cotc.tests.analyser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameReader1 {
  public Game game = new Game();

  public void read(String name) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(
            Reader1.class.getClassLoader().getResourceAsStream(name)))) {
      String viewerLine = br.readLine();
      System.out.println(viewerLine);
      readFrame(viewerLine);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  private void readFrame(String viewerLine) {
    Pattern pattern = Pattern.compile("\"view\":\" (.*?)\",");
    Matcher matcher = pattern.matcher(viewerLine);
    while (matcher.find()) {
      String data = matcher.group(1);
      System.out.println(data);
      String[] inputs = data.split("\\\\n");
      if (inputs.length > 1) {
        Frame frame = new Frame(game);
        if ("0".equals(inputs[0])) {
          Scanner in = new Scanner(inputs[3]);
          in.nextInt();
          in.nextInt();
          game.shipCount = in.nextInt();
          in.nextInt();
          
          in.close();
          
          frame.readInputsFrom(inputs, 4);
        } else {
          frame.readInputsFrom(inputs, 1);
        }
        game.frames.add(frame);
      }
    }
  }

}
