package pac.simpleai;

import pac.map.Pos;

public class ColliderResolver {

	public static boolean hasCollided(Pos[] first, Pos[] second) {
//    if (first[0] == to) {
//    	return true;
//    }
//    if (second[0] == from) {
//    	return true;
//    }
		if (first[0] == second[0]) {
    	return true;
    }
    if (first[1] == second[1]) {
    	return true;
    }

    // crossing
    if (first[0] == second[1] && first[1] == second[0]) {
    	return true;
    }

    return false;
	}
	
	public static boolean hasCollidedFull(Pos[] first, Pos[] second) {
    if (first[1] == second[1]) { return true; }
    if (first[2] == second[2]) { return true; }
  
    // crossing
    if (first[0] == second[1] && first[1] == second[0]) {	return true; }
    if (first[1] == second[2] && first[2] == second[1]) {	return true; }
    
    return false;
  }
}
