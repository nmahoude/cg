import java.util.*;
import java.io.*;
import java.math.*;

class Solution {
  static int L;
  static int H;

  static class Character {
    char[][] chars;

    public Character() {
      chars = new char[L][H];
    }

    public void readChar() {
      for (int y=0;y<H;y++) {
        String num1Line = in.next();
        for (int x=0;x<L;x++) {
          chars[x][y] = num1Line.charAt(x);
        }
      }
    }
    
    @Override
    public boolean equals(Object obj) {
      Character c = (Character)obj;
      for (int x=0;x<L;x++) {
        for (int y=0;y<H;y++) {
          if (c.chars[x][y] != this.chars[x][y]) {
            return false;
          }
        }
      }
      return true;
    }

    public void print() {
      for (int y=0;y<H;y++) {
        String line = "";
        for (int x=0;x<L;x++) {
          line+=chars[x][y];
        }
        System.out.println(line);
      }
    }
  }

  static Character[] characters = new Character[20];
  private static Scanner in;
  static int charToInt(Character c) {
    for (int i = 0; i < 20; i++) {
      if (c.equals(characters[i])) {
        return i;
      }
    }    
    return -1;
  }

  public static void main(String args[]) {
    in = new Scanner(System.in);
    L = in.nextInt();
    H = in.nextInt();
    System.err.println("LxH="+L+"x"+H);
    initCharacters();

    
    for (int i = 0; i < H; i++) {
      String numeral = in.next();
      System.err.println(numeral);
      for (int x = 0; x < 20; x++) {
        Character c = characters[x];
        for (int xx = 0; xx < L; xx++) {
          c.chars[xx][i] = numeral.charAt(x * L + xx);
        }
      }
    }

    int S1 = in.nextInt();
    int number1 = 0;
    for (int i = 0; i < S1 / H; i++) {
      Character c = new Character();
      c.readChar();
      number1 = number1*20+charToInt(c);
      System.err.println("number 1 is "+charToInt(c)+" which give "+number1);
    }

    int S2 = in.nextInt();
    int number2 = 0;
    for (int i = 0; i < S2 / H; i++) {
      Character c = new Character();
      c.readChar();
      number2 = number2*20+charToInt(c);
      System.err.println("number 2 is "+charToInt(c)+" which give "+number2);
    }
    
    String operation = in.next();
    BigInteger result = BigInteger.valueOf(number1);
    switch(operation) {
    case "+":
      result = result.add(BigInteger.valueOf(number2));
      break;
    case "-":
      result = result.add(BigInteger.valueOf(-number2));
      break;
    case "*":
      result = result.multiply(BigInteger.valueOf(number2));
      break;
    case "/":
      result = result.divide(BigInteger.valueOf(number2));
      break;
    }

    System.err.println(""+number1+operation+number2+"="+result);
    List<Character> suit= new ArrayList<>();
    if (result.intValue() == 0) {
      suit.add(characters[0]);
    } else {
      while (!result.equals(BigInteger.valueOf(0))) {
        int base = result.mod(BigInteger.valueOf(20)).intValue();
        result = result.divide(BigInteger.valueOf(20));
        System.err.println("base is "+base+" remainder is "+result);
        Character c = characters[base];
        suit.add(0, c);
      }
    }
    for (Character c : suit) {
      c.print();
    }
    
  }

  private static void initCharacters() {
    for (int i = 0; i < 20; i++) {
      characters[i] = new Character();
    }
  }
}