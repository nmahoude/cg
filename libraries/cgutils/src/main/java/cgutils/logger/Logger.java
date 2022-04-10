package cgutils.logger;


public class Logger {
	private static Level globalLevel = Level.DEBUG;

	private final Level level;
  private final String logName;

  private Logger(String name, Level levelSpecific) {
    this.logName = name;
    this.level = levelSpecific;
  }

  private Logger(String name) {
  	this(name, Level.DEBUG);
  }

  private Logger() {
  	this("");
  }

  public static Logger getLogger(Class<?> clazz, Level levelSpecific) {
    Logger ret = new Logger(clazz.getSimpleName(), levelSpecific);
    return ret;
  }

  public static Logger getLogger(String name, Level levelSpecific) {
    Logger ret = new Logger(name, levelSpecific);
    return ret;
  }

  public void error(Object... s) {
    print("[ERROR]", s);
  }

  public void info(Object... s) {
    if (getLevel() == Level.DEBUG || getLevel() == Level.INFO) {
      print("[INFO ]",s);
    }
  }

  public void debug(Object... s) {
    if (getLevel() == Level.DEBUG) {
      print("[DEBUG]", s);
    }
  }

  private static StringBuilder sb = new StringBuilder(5000);

  public void print(Object... s) {
    if (getLevel() == Level.NONE) {
      return;
    }
    sb.delete(0, sb.length());
//    sb.append(Timer.getInstance().getTime());
//    sb.append(' ');
    sb.append(logName);
    sb.append(" : ");
    int i = 0;
    for (Object o : s) {
      sb.append(o == null ? "null" : o.toString());
      if (++i < s.length) {
        sb.append(' ');
      }
    }
    System.err.println(sb);
  }

  public void disableAllLogs() {
  	globalLevel = Level.NONE;
  }
  
  private Level getLevel() {
    if (level == null || globalLevel == Level.NONE) {
      return globalLevel;
    }
    return level;
  }
}
