package ooc;

import java.util.BitSet;
import java.util.List;

import ooc.orders.Order;
import ooc.orders.OrderTag;
import ooc.orders.Orders;

public class SilentRangeCalculator {
	int cells[][] = new int[100][100];
	int currentX = 50;
	int currentY = 50;
	
	public int[] run(List<Orders> ordersList) {
		int[] ranges = new int[] { 4, 4, 4, 4};

		int currentIndex = 1;
		cells[currentX][currentY] = currentIndex;
		System.err.println("Silent range calculator ....");
		for (int o=0;o<ordersList.size()-1;o++) {
		  Orders orders = ordersList.get(o);
			for (Order order : orders.getOrders()) {
				if (order.tag == OrderTag.MOVE) {
					switch(order.value) {
						case 0 /* N */: currentY--;break;
						case 1 /* E */: currentX++;break;
						case 2 /* S */: currentY++;break;
						case 3 /* W */: currentX--;break;
					}
					cells[currentX][currentY] = currentIndex;
				}
				
				if (order.tag == OrderTag.SURFACE || order.tag == OrderTag.SILENCE) {
					currentX = 50;
					currentY = 50;
					currentIndex++;
				}
			}
		}
		
		int max;

		// NORTH
		max = 0;
		for (int r=1;r<=4;r++) {
			if (cells[currentX][currentY-r] == currentIndex) break;
			max++;
		}
		ranges[Direction.NORTH.direction] = max;
		// EAST
		max = 0;
		for (int r=1;r<=4;r++) {
			if (cells[currentX+r][currentY] == currentIndex) break;
			max++;
		}
		ranges[Direction.EAST.direction] = max;
		// SOUTH
		max = 0;
		for (int r=1;r<=4;r++) {
			if (cells[currentX][currentY+r] == currentIndex) break;
			max++;
		}
		ranges[Direction.SOUTH.direction] = max;
		// WEST
		max = 0;
		for (int r=1;r<=4;r++) {
			if (cells[currentX-r][currentY] == currentIndex) break;
			max++;
		}
		ranges[Direction.WEST.direction] = max;
		
		return ranges;
	}

	public int[] run(P from, BitSet visitedCells) {
		int lengths[] = new int[4];
		for (int d=0;d<4;d++) {
      P c = from;
      for (int l=1;l<=4;l++) {
        c = c.neighbors[d];
        if (Player.map.isIsland(c)) break;
        if (visitedCells.get(c.o)) break;
        lengths[d] = l;
      }
    }
		return lengths;
	}
}
