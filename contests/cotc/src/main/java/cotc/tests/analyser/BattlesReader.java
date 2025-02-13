package cotc.tests.analyser;

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
        .post(Entity.text("["+battleId+", xxxx]"));

    JsonObject battle = response.readEntity(JsonObject.class);
    
    readFrame(battle.toString());
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
