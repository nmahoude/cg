package exolegend._analyser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class BattlesReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(BattlesReader.class); 
  
  private Path base;
  private String rememberMe;
  private String userId;

  public record Player(String nickname, String playerAgentId) {

    @Override
    public String toString() {
      return nickname;
    }

    @Override
    public int hashCode() {
      return Objects.hash(nickname);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Player other = (Player) obj;
      return Objects.equals(nickname, other.nickname);
    }
    
    
  }
  
  public class FullGame {
    public String gameId;
    public String content;
    
    public List<Integer> scores() {
      Gson gson = new Gson();
      JsonObject o = gson.fromJson(content, JsonObject.class);
      JsonArray scores = o.get("scores").getAsJsonArray();
      
      return List.of(scores.get(0).getAsInt(), scores.get(1).getAsInt());
    }
  }

  public class MatchInfo {

    public String gameId;
    private FullGame fullGame = null;
    
    List<Player> players = new ArrayList<>();
    List<Integer> positions = new ArrayList<>();

    @Override
    public String toString() {
      return ""+gameId+" - "+players.get(0).nickname()+" vs "+players.get(1).nickname();
    }
    
    public FullGame fullGame() {
      return fullGame;
    }

    public Player player(int index) {
      return players.get(index);
    }
    public Player p0() {
      return players.get(0);
    }
    
    public Player p1() {
      return players.get(1);
    }

    public boolean isWon() {
      return positions.get(1) == 1;
    }
    
    public boolean isDraw() {
      return positions.get(1) == 0;
    }
  }
  
  public BattlesReader(Path base, String rememberMe, String userId) {
    this.base = base;
    this.rememberMe = rememberMe;
    this.userId = userId;
  }
  

  public List<MatchInfo> read() throws IOException {
    return loadGames();
  }

  private FullGame getOneGame(String gameId) {
    
    LOGGER.info("Reading game {}", gameId);
    Client client = ClientBuilder.newClient();
    WebTarget target = client
        .target("https://www.codingame.com/services/gameResult/findByGameId");

    Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .cookie("rememberMe", rememberMe)
        .post(Entity.json("["+gameId+","+userId+"]"));

    String result = response.readEntity(String.class);
    
    FullGame game = new FullGame();
    game.gameId = gameId;
    game.content = result;
    
    try {
      Files.createDirectories(base);
      Files.write(base.resolve(gameId+".json"), result.getBytes());
    } catch (IOException e) {
      LOGGER.error("Error while reading game {}", gameId, e);
    }
    
    LOGGER.debug("content of game {} is {}", gameId, result);
    return game;
  }

  
  private List<MatchInfo> loadGames() throws IOException {
    
    String result;
    Path allGamesPath = base.resolve("allgames.json");
    if (!Files.exists(allGamesPath)) {
      LOGGER.info("Getting all games from codingame ...");
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("https://www.codingame.com/services/gamesPlayersRanking/findLastBattlesByTestSessionHandle");
      
      
      Response response = target.request(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .post(Entity.json("""
            ["63799678b9aa99ecb5b2fbfe64c3ce722643e7c3",null]
          """));
      
      result = response.readEntity(String.class);
      Files.createDirectories(base);
      Files.write(allGamesPath, result.getBytes());
    } else {
      LOGGER.info("Getting all games info from cache ...");
      result = Files.readString(allGamesPath);
    }
    
    Gson gson = new Gson();
    JsonArray array = gson.fromJson(result, JsonArray.class);

    List<MatchInfo> infos = new ArrayList<>();
    for (int i=0;i<array.size();i++) {
      JsonObject o = (JsonObject)array.get(i);
      LOGGER.debug("reading all games info : {}", o);

      String gameId = o.get("gameId").toString();
      if (!o.get("done").getAsBoolean()) {
        LOGGER.info("Game "+gameId+" is not done yet ... passing");
        continue;
      }
      
      MatchInfo info = new MatchInfo();
      if (!Files.exists(base.resolve(gameId+".json"))) {
        FullGame game = getOneGame(gameId);
        info.fullGame  = game;
      } else {
        var fullGame = new FullGame();
        fullGame.gameId = gameId;
        fullGame.content = Files.readString(base.resolve(gameId+".json"));
        info.fullGame = fullGame;
      }
      
      // System.out.println("Gameid : "+gameId);
      info.gameId = gameId;
      
      
      JsonArray players = (JsonArray)o.get("players");
      
      for (int p=0;p<2;p++) {
        JsonObject playerAsJson = (JsonObject)players.get(p);
        String nickname = playerAsJson.get("nickname").getAsString();
        String playerAgentId = playerAsJson.get("playerAgentId").getAsString();
        Player p0 = new Player(nickname, playerAgentId);
        
        int position = playerAsJson.get("position").getAsInt();
        
        info.players.add(p0);
        info.positions.add(position);
        
      }
      infos.add(info);
    }
    
    LOGGER.info("Read full done");
    return infos;
  }


}
