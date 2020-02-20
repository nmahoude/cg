package cotc.tests.analyser.reader;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


public class BattlesReader {
  public final static String URL = "https://www.codingame.com/services/gamesPlayersRankingRemoteService/findLastBattlesAndProgressByAgentId";
  private static final String SINGLE_BATTLE_URL = "https://www.codingame.com/services/gameResultRemoteService/findByGameId";

  public Game game = new Game();
  public static void main(String[] args) {
    // send simple json to get battles list

    BattlesReader br = new BattlesReader();
    //readAllBattles();
    br.readOneBattle("212955176");    
  }

  public void readAllBattles() {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(URL);
    
    Response response = target.request().
        post(Entity.text("[1206697, null]"));

    JsonObject battles = response.readEntity(JsonObject.class);
    
    System.out.println(battles);
  }
  
  public void readOneBattle(String battleId) {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(SINGLE_BATTLE_URL);
    
    Response response = target.request()
//        .cookie("JSESSIONID","AEC356A40048DCB722D4597248D94E3E")
//        .cookie("AWSELB", "43AD292116245448CF7264593C17D198824F648E2931D95E0E9D495978F5CE53423E78B1A038B3DDC7D061E8D5673A1EF5F11C9F7454388DAF5221430C435960E1BE995800")
        .post(Entity.text("["+battleId+", null]"));

    JsonObject battle = response.readEntity(JsonObject.class);
    
    readFrame(battle.toString());
  }
  
  private void readFrame(String viewerLine) {
    Pattern pattern = Pattern.compile("\"view\":\" (.*?)\",");
    Matcher matcher = pattern.matcher(viewerLine);
    while (matcher.find()) {
      String[] inputs = matcher.group(1).split("\\\\n");
      if (isKeyFrame(inputs)) {
        Frame frame = new Frame(game);
        if (isInitFrame(inputs)) {
          readInitFrame(inputs);
          
          frame.readInputsFrom(inputs, 1+3);
        } else {
          frame.readInputsFrom(inputs, 1);
        }
        game.frames.add(frame);
      }
    }
  }

  private void readInitFrame(String[] inputs) {
    Scanner in = new Scanner(inputs[3]);
    in.nextInt();  // barrels count
    in.nextInt();  // mines count
    game.shipCount = in.nextInt(); // ships per player count
    in.nextInt(); // ?
    
    in.close();
  }

  private boolean isInitFrame(String[] inputs) {
    return "0".equals(inputs[0]);
  }

  private boolean isKeyFrame(String[] inputs) {
    return inputs.length > 1;
  }

}
