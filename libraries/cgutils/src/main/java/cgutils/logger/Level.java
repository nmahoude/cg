package cgutils.logger;

public enum Level {
	NONE(100),
	ERROR(50),
	INFO(10),
	DEBUG(0),
  DISABLED(-1)
  ;

  private int priority;

  Level(int priority) {
    this.priority = priority;
  }

  public boolean isInferiorOrEqualTo(Level level) {
    return this.priority <= level.priority;
  }
  
  
}
