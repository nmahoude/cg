package referee;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import referee.exceptions.GameOverException;
import referee.exceptions.InvalidFormatException;
import referee.exceptions.InvalidInputException;
import referee.exceptions.LostException;
import referee.exceptions.WinException;

public class MultiReferee {

  public MultiReferee(InputStream is, PrintStream out, PrintStream err) {
  }

  protected void initReferee(int playerCount, Properties prop) throws InvalidFormatException {
  }

  protected Properties getConfiguration() {
    return null;
  }

  protected String[] getInitInputForPlayer(int playerIdx) {
    return null;
  }

  protected void prepare(int round) {
    
  }

  protected String[] getInputForPlayer(int round, int playerIdx) {
    return null;
  }

  protected int getExpectedOutputLineCountForPlayer(int playerIdx) {
    return 0;
  }

  protected void handlePlayerOutput(int frame, int round, int playerIdx, String[] outputs) throws WinException, LostException, InvalidInputException {
  }

  protected void printError(String output) {
    System.out.println(output);
  }

  protected void updateGame(int round) throws GameOverException {
  }

  protected void populateMessages(Properties p) {
  }

  protected boolean isGameOver() {
    return false;
  }

  protected String[] getInitDataForView() {
    return null;
  }

  protected String[] getFrameDataForView(int round, int frame, boolean keyFrame) {
    return null;
  }

  protected String getGameName() {
    return null;
  }

  protected String getHeadlineAtGameStartForConsole() {
    return null;
  }

  protected int getMinimumPlayerCount() {
    return 0;
  }

  protected boolean showTooltips() {
    return false;
  }

  protected String[] getPlayerActions(int playerIdx, int round) {
    return null;
  }

  protected boolean isPlayerDead(int playerIdx) {
    return false;
  }

  protected String getDeathReason(int playerIdx) {
    return null;
  }

  protected int getScore(int playerIdx) {
    return 0;
  }

  protected String[] getGameSummary(int round) {
    return null;
  }

  protected void setPlayerTimeout(int frame, int round, int playerIdx) {
    
  }

  protected int getMaxRoundCount(int playerCount) {
    return 0;
  }

}
