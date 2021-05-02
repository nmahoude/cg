package singleClassTester.result;

public class Result {

}

class Move {

    public static void move() {
        System.out.println("static Move");
    }
}

class Player {

    public static void main(String[] args) {
        System.out.println("Test");
        move();
    }
}
