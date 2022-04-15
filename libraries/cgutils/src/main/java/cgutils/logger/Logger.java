package cgutils.logger;

import java.util.Date;

public class Logger {
	private static Level globalLevel = Level.DEBUG;

	private final Level level;
  private final String logName;

  private boolean appendLogName = false;
  private boolean appendTime = false;
  private boolean disabled;
  private String prefix;

  private boolean showLevel = true;

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
    private boolean disabled;
    private String prefix = null;
    private boolean showLevel = true;

    public LoggerBuilder() {
    }
    
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
    
    public LoggerBuilder withShowLevel(boolean showLevel) {
      this.showLevel = showLevel;
      return this;
    }

    public Logger build() {
      Logger logger = new Logger(name, level); 
      logger.appendTime = this.appendTime;
      logger.appendLogName = this.appendLogName;
      logger.disabled = this.disabled;
      logger.prefix = this.prefix;
      logger.showLevel = this.showLevel ;
      return logger;
    }

    public LoggerBuilder disabled() {
      this.disabled = true;
      return this;
    }

    public LoggerBuilder withPrefix(String prefix) {
      this.prefix = prefix;
      return this;
    }
  }
  
  
  public void error(String... s) {
    if (getLoggerLevel().isInferiorOrEqualTo(Level.INFO)) {
      print("[ERROR]", s);
    }
  }

  public void info(String... s) {
    if (getLoggerLevel().isInferiorOrEqualTo(Level.INFO)) {
      print("[INFO ]",s);
    }
  }

  public void debug(String... s) {
    if (getLoggerLevel().isInferiorOrEqualTo(Level.DEBUG)) {
      print("[DEBUG]", s);
    }
  }

  private static StringBuilder sb = new StringBuilder(5000);

  protected void print(String level, String... s) {
    if (disabled) return;
    
    sb.delete(0, sb.length());
    
    if (prefix != null) {
      sb.append(prefix);
    }
    
    if (appendTime ) {
      sb.append('[');
      sb.append(new Date());
      sb.append(']');
    }
    
    if (appendLogName ) {
      sb.append("[");
      sb.append(logName);
      sb.append("]");
    }
    
    if (showLevel ) {
      sb.append(level).append(' ');
    }
    
    int i = 0;
    for (Object o : s) {
      sb.append(o);
      if (++i < s.length) {
        sb.append(' ');
      }
    }
    outputSb();
  }

  public void disable() {
    this.disabled = true;
  }
  
  protected void outputSb() {
    System.err.println(sb);
  }

  public static void disableAllLogs() {
  	globalLevel = Level.NONE;
  }
  
  public static void setGlobalLevel(Level level) {
    if (level == Level.DISABLED) {
      throw new IllegalArgumentException("Can't set global level to disabled");
    }
    globalLevel = level;
  }

  private Level getLoggerLevel() {
    if (level == null || level.isInferiorOrEqualTo(globalLevel)) {
      return globalLevel;
    }
    return level;
  }

  public static Logger inputLogger() {
    return new Logger.LoggerBuilder().withName("input")
                      .withLevel(Level.DEBUG)
                      .withPrefix("^")
                      .withShowLevel(false)
                      .build();
  }
}
