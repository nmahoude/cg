package cgfx;

import java.io.IOException;
import java.util.prefs.Preferences;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ViewRestorer {
  private static final String NODE_NAME = "ViewRestorer";

  private static final String WINDOW_POSITION_X = "Window_Position_X";
  private static final String WINDOW_POSITION_Y = "Window_Position_Y";
  private static final String WINDOW_WIDTH = "Window_Width";
  private static final String WINDOW_HEIGHT = "Window_Height";

  public static void applyOn(Stage stage) throws IOException {
    applyOn(stage, ViewRestorer.class);
  }  
  
  public static void applyOn(Stage stage, Class clazz) throws IOException {
    // Pull the saved preferences and set the stage size and start location
    Preferences pref = Preferences.userRoot().node(NODE_NAME+clazz.getName());
    double x = pref.getDouble(WINDOW_POSITION_X, -1);
    double y = pref.getDouble(WINDOW_POSITION_Y, -1);
    double width = pref.getDouble(WINDOW_WIDTH, -1);
    double height = pref.getDouble(WINDOW_HEIGHT, -1);
    if (x != -1)      stage.setX(x);
    if (y != -1)      stage.setY(y);
    if (width != -1)  stage.setWidth(width);
    if (height != -1) stage.setHeight(height);

    // When the stage closes store the current size and window location.
    stage.setOnCloseRequest((final WindowEvent event) -> savePreferences(stage, clazz));
  }

  private static void savePreferences(Stage stage, Class clazz) {
    Preferences preferences = Preferences.userRoot().node(NODE_NAME+clazz.getName());
    preferences.putDouble(WINDOW_POSITION_X, stage.getX());
    preferences.putDouble(WINDOW_POSITION_Y, stage.getY());
    preferences.putDouble(WINDOW_WIDTH, stage.getWidth());
    preferences.putDouble(WINDOW_HEIGHT, stage.getHeight());
  }
}
