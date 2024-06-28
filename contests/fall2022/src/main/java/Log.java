
public class Log {
	public static boolean hasWarning;
	
	public static void warning(String str) {
		System.err.println(str);
		hasWarning = true;
	}
}
