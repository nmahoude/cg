package fall2022;

public class Logger {
	public static boolean hasWarning;
	public static boolean hasError;

	public static void reset() {
		hasWarning = false;
		hasError = false;
	}

	
	public static void info(Object str) {
		info(true, str);
	}
	
	public static void warning(Object str) {
		warning(true, str);
	}

	public static void error(Object str) {
		error(true, str);
	}

	public static void info(boolean enabled, Object str) {
		if (!enabled) return;

		System.err.println(str);
	}

	public static void warning(boolean enabled, Object str) {
		if (!enabled) return;
		
		System.err.println(str);
		hasWarning = true;
	}

	public static void error(boolean enabled, Object str) {
		if (!enabled) return;
		
		System.err.println(str);
		hasError = true;
	}
}
