package lcm.cards;

public class Abilities {
  public static final int BREAKTHROUGH = 0b1;
  public static final int CHARGE       = 0b10;
  public static final int GUARD        = 0b100;
  public static final int DRAIN        = 0b1000;
  public static final int LETHAL       = 0b10000;
  public static final int WARD         = 0b100000;

  public static int read(String abs) {
    int abilities = 0;
    
    for (int i=0;i<abs.length();i++) {
      char c = abs.charAt(i);
      switch (c) {
      case 'B':
       abilities |= BREAKTHROUGH;
       break;
      case 'C':
        abilities |= CHARGE;
        break;
      case 'G':
        abilities |= GUARD;
        break;
      case 'D':
        abilities |= DRAIN;
        break;
      case 'L':
        abilities |= LETHAL;
        break;
      case 'W':
        abilities |= WARD;
        break;
      }
    }
    return abilities;
  }

  public static String toString(int abilities) {
    String out = "";
    if ((abilities & BREAKTHROUGH) != 0) out+="B";
    if ((abilities & CHARGE) != 0) out+="C";
    if ((abilities & GUARD) != 0) out+="G";
    if ((abilities & DRAIN) != 0) out+="D";
    if ((abilities & LETHAL) != 0) out+="L";
    if ((abilities & WARD) != 0) out+="W";
    
    if ("".equals(out)) {
      out="-";
    }
    return out;
  }

  public static int upgrade(int abilities, Card item) {
    return abilities | item.abilities;
  }
  
  public static int downgrade(int abilities, Card item) {
    return abilities & ~item.abilities;
  }

}
