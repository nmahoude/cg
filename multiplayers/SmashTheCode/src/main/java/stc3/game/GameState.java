package stc3.game;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class GameState {
  public static final ThreadLocalRandom random =ThreadLocalRandom.current();
  
  private Scanner in;
  
  public long roundStartTime = 0;
  
  public Pair[] pairs = new Pair[8];
  public PlayerInfo[] playerInfos = new PlayerInfo[2];
  
  public GameState(Scanner in) {
    this.in = in;
    playerInfos[0] = new PlayerInfo();
    playerInfos[1] = new PlayerInfo();
  }

  private void readPairs() {
    for (int i = 0; i < 8; i++) {
      if (i == 0) {
        roundStartTime = System.nanoTime();
      }
      pairs[i] = new Pair(in.nextInt(), in.nextInt());
    }
  }

  public void readState() {
    readPairs();
    extractPlayerInfo(playerInfos[0]);
    extractPlayerInfo(playerInfos[1]);
  }

  public void prepare() {
    playerInfos[0].opponentBoard = playerInfos[1].board;
    playerInfos[0].pairs = this.pairs;

    playerInfos[1].opponentBoard = playerInfos[0].board;
    playerInfos[1].pairs = this.pairs;
  }
  private void extractPlayerInfo(PlayerInfo playerInfo) {
    playerInfo.clearForRound();
    playerInfo.points = in.nextInt();
    for (int i = 0; i < 12; i++) {
      playerInfo.board.updateRow(i, in.next());
    }
    playerInfo.board.buildCompleteLayerMask();
  }

  public void generateANewPair() {
    int lastPairIndex = pairs.length-1;
    for (int i=0;i<lastPairIndex;i++) {
      pairs[i] = pairs[i+1];
    }
    pairs[lastPairIndex] = new Pair(random.nextInt(5)+1, random.nextInt(5)+1);
  }

  public void generateRandomPairs() {
    for (int i = 0;i<8;i++) {
      generateANewPair();    
    }
  }

}
