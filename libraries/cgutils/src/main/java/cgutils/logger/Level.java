package cgutils.logger;

public enum Level {
	NONE(0),
	ERROR(1),
	INFO(2),
	DEBUG(3);

  private int priority;

  Level(int priority) {
    this.priority = priority;
  }

  public boolean isInferiorOrEqualTo(Level level) {
    return this.priority <= level.priority;
  }
  
  
}
