package spring2021;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CellTest {
	@Test
	void randomBoardIsWellLinked() throws Exception {
		initRandomCells();
			
		assertThat(Cell.cells[0].neighbors[0]).isEqualTo(Cell.cells[1]);	
		assertThat(Cell.cells[0].neighbors[1]).isEqualTo(Cell.cells[2]);	
		assertThat(Cell.cells[0].neighbors[2]).isEqualTo(Cell.cells[3]);	
		assertThat(Cell.cells[0].neighbors[3]).isEqualTo(Cell.cells[4]);	
		assertThat(Cell.cells[0].neighbors[4]).isEqualTo(Cell.cells[5]);	
		assertThat(Cell.cells[0].neighbors[5]).isEqualTo(Cell.cells[6]);	
		
		assertThat(Cell.cells[25].neighbors[0]).isEqualTo(Cell.cells[24]);
		assertThat(Cell.cells[1].neighbors[0]).isEqualTo(Cell.cells[7]);
	}

	@Nested
	class Shadows {
		@Test
		void randomBoardShadows() throws Exception {
			initRandomCells();

			assertThat(shadowIndex(0,0,0)).isEqualTo(1);	
			assertThat(shadowIndex(0,0,1)).isEqualTo(7);	
			assertThat(shadowIndex(0,0,2)).isEqualTo(19);	
	
			assertThat(shadowIndex(28,0,0)).isEqualTo(13);	
			assertThat(shadowIndex(28,0,1)).isEqualTo(4);	
			assertThat(shadowIndex(28,0,2)).isEqualTo(0);	
	
			assertThat(shadowIndex(28,1,0)).isEqualTo(27);	
			assertThat(shadowIndex(28,1,1)).isEqualTo(26);	
			assertThat(shadowIndex(28,1,2)).isEqualTo(25);	
			
		}

		private int shadowIndex(int index, int day, int dist) {
			return Cell.shadowIndexes[index][day][dist];
		}
	}
	
	@Nested
	class Distances {
		@Test
		void distToWallIsInfinity() throws Exception {
			assertThat(Cell.cells[0].distances[Cell.WALL.index]).isEqualTo(Integer.MAX_VALUE);
		}
		@Test
		void differentDistances() throws Exception {
			assertThat(dist(0, 19)).isEqualTo(3);
			assertThat(dist(19, 0)).isEqualTo(3);
			assertThat(dist(31, 23)).isEqualTo(6);
			assertThat(dist(32, 24)).isEqualTo(6);
		}
		
		@Test
		void allDistancesUnder7() throws Exception {
			for (int i=0;i<37;i++) {
				for (int j=0;j<37;j++) {
					assertThat(dist(i,j) <= 6).as("distance between "+Cell.cells[i]+" "+Cell.cells[j]).isTrue();
				}
			}
		}
		
		private int dist(int index0, int index1) {
			return Cell.cells[index0].distances[Cell.cells[index1].index];
		}
	}
	
	
	
	public static void initRandomCells() {
		Cell.init();
		
		String input = "37\r\n"
				+ "0 3 1 2 3 4 5 6 \r\n"
				+ "1 0 7 8 2 0 6 18 \r\n"
				+ "2 3 8 9 10 3 0 1 \r\n"
				+ "3 0 2 10 11 12 4 0 \r\n"
				+ "4 0 0 3 12 13 14 5 \r\n"
				+ "5 3 6 0 4 14 15 16 \r\n"
				+ "6 0 18 1 0 5 16 17 \r\n"
				+ "7 2 19 20 8 1 18 36 \r\n"
				+ "8 2 20 21 9 2 1 7 \r\n"
				+ "9 2 21 22 23 10 2 8 \r\n"
				+ "10 2 9 23 24 11 3 2 \r\n"
				+ "11 2 10 24 25 26 12 3 \r\n"
				+ "12 2 3 11 26 27 13 4 \r\n"
				+ "13 2 4 12 27 28 29 14 \r\n"
				+ "14 2 5 4 13 29 30 15 \r\n"
				+ "15 2 16 5 14 30 31 32 \r\n"
				+ "16 2 17 6 5 15 32 33 \r\n"
				+ "17 2 35 18 6 16 33 34 \r\n"
				+ "18 2 36 7 1 6 17 35 \r\n"
				+ "19 1 -1 -1 20 7 36 -1 \r\n"
				+ "20 1 -1 -1 21 8 7 19 \r\n"
				+ "21 1 -1 -1 22 9 8 20 \r\n"
				+ "22 1 -1 -1 -1 23 9 21 \r\n"
				+ "23 1 22 -1 -1 24 10 9 \r\n"
				+ "24 1 23 -1 -1 25 11 10 \r\n"
				+ "25 1 24 -1 -1 -1 26 11 \r\n"
				+ "26 1 11 25 -1 -1 27 12 \r\n"
				+ "27 1 12 26 -1 -1 28 13 \r\n"
				+ "28 1 13 27 -1 -1 -1 29 \r\n"
				+ "29 1 14 13 28 -1 -1 30 \r\n"
				+ "30 1 15 14 29 -1 -1 31 \r\n"
				+ "31 1 32 15 30 -1 -1 -1 \r\n"
				+ "32 1 33 16 15 31 -1 -1 \r\n"
				+ "33 1 34 17 16 32 -1 -1 \r\n"
				+ "34 1 -1 35 17 33 -1 -1 \r\n"
				+ "35 1 -1 36 18 17 34 -1 \r\n"
				+ "36 1 -1 19 7 18 35 -1 ";
		
			new Player().readGlobal(new Scanner(input));
	}
}
