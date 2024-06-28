package ooc.minimax;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ooc.P;

public class MinimaxTest {

	
	@Test
	void minimax_setup() throws Exception {
		
		Minimax minimax = new Minimax();
		
		MMNode node = new MMNode();
		node.pos[0] = P.get(7,7);
		node.pos[1] = P.get(13,7);
		node.trails[1].set(P.get(14, 7).o); // on le bloque vers l'arriere
		node.life[0] = 6;
		node.life[1] = 6;
		node.torpedoDelai[0] = 2;
		node.torpedoDelai[1] = 6;

		
		double score = minimax.minimax(node, 3, 0);
		System.err.println("score "+score);
	}
}
