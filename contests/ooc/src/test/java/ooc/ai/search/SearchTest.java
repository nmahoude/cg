package ooc.ai.search;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ooc.Player;
import ooc.PlayerTest;
import ooc.P;
import ooc.State;
import ooc.ai.search.Search;
import ooc.ai.search.scorers.Scorer;

public class SearchTest {

	
	private static Search search;

	@BeforeAll
	public static void setup() {
	  PlayerTest.emptyMap();
	  
		search = new Search();
	}
	
}
