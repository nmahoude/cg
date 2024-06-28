package ooc;

import java.util.Scanner;

public class MapBuilder {
	private static final String EOL = "\n"; 
	OOCMap map = new OOCMap();
	
	private MapBuilder() {
	}
	
	public static MapBuilder emptyMap() {
		MapBuilder builder = new MapBuilder();
		Scanner in = new Scanner(
				"15 15 0"+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				"               "+EOL+
				""
				);
		builder.map.read(in);
		
		return builder;
	}

	public OOCMap build() {
		return map;
	}

	
	
	
}
