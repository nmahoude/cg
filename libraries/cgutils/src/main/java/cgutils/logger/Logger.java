package cgutils.logger;

import java.util.Date;

public class Logger {
	private static Level globalLevel = Level.DEBUG;

	private final Level level;
  private final String logName;

  private boolean appendLogName = false;
  private boolean appendTime = false;

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

  public static class LoggerBuilder {
    private Level level;
    private String name;
    private boolean appendTime;
    private boolean appendLogName;

    public LoggerBuilder(String name) {
      this.name = name;
    }

    public LoggerBuilder withName(String name) {
      this.name = name;
      return this;
    }
    public LoggerBuilder withLevel(Level level) {
      this.level = level;
      return this;
    }
    
    public LoggerBuilder withTimeAppended() {
      this.appendTime = true;
      return this;
    }
    
    public LoggerBuilder withNameAppended() {
      this.appendLogName = true;
      return this;
    }
    
    public Logger build() {
      Logger logger = new Logger(name, level); 
      logger.appendTime = this.appendTime;
      logger.appendLogName = this.appendLogName;
      return logger;
    }
  }
  
  
  public void error(Object... s) {
    if (getLoggerLevel().isInferiorOrEqualTo(Level.INFO)) {
      print("[ERROR]", s);
    }
  }

  public void info(Object... s) {
    if (getLoggerLevel().isInferiorOrEqualTo(Level.INFO)) {
      print("[INFO ]",s);
    }
  }

  public void debug(Object... s) {
    if (getLoggerLevel().isInferiorOrEqualTo(Level.DEBUG)) {
      print("[DEBUG]", s);
    }
  }

  private static StringBuilder sb = new StringBuilder(5000);

  protected void print(Object... s) {
    sb.delete(0, sb.length());
    
    if (appendTime ) {
      sb.append(new Date());
      sb.append(' ');
    }
    
    if (appendLogName ) {
      sb.append(logName);
      sb.append(" : ");
    }
    
    int i = 0;
    for (Object o : s) {
      sb.append(o == null ? "null" : o.toString());
      if (++i < s.length) {
        sb.append(' ');
      }
    }
    outputSb();
  }

  protected void outputSb() {
    System.err.println(sb);
  }

  public static void disableAllLogs() {
  	globalLevel = Level.NONE;
  }
  
  public static void setGlobalLevel(Level level) {
    globalLevel = level;
  }

  private Level getLoggerLevel() {
    if (level == null || level.isInferiorOrEqualTo(globalLevel)) {
      return globalLevel;
    }
    return level;
  }
}
