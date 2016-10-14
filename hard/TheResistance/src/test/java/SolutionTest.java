
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionTest {

  @Test
  public void simple() {

    Solution.indexToWordCountCache.clear();
    Solution.Node root = new Solution.Node();
    Solution.morseCode = "-.-";
    Solution.finalIndex = Solution.morseCode.length();

    root.acceptWord("A");
    root.acceptWord("B");
    root.acceptWord("C");
    root.acceptWord("HELLO");
    root.acceptWord("K");
    root.acceptWord("WORLD");
    
    long count = root.acceptMorse(0);
    
    assertThat(count, is(1L));
  }
  
  @Test
  public void second() throws Exception {
    Solution.Node root = new Solution.Node();
    
    Solution.indexToWordCountCache.clear();
    Solution.root = root;
    Solution.morseCode = "--.-------..";
    Solution.finalIndex = Solution.morseCode.length();

    root.acceptWord("GOD");
    root.acceptWord("GOOD");
    root.acceptWord("MORNING");
    root.acceptWord("G");
    root.acceptWord("HELLO");
    
    long count = root.acceptMorse(0);
    
    assertThat(count, is(1L));
  }
  
  @Test
  public void longMorse() throws Exception {
    Solution.Node root = new Solution.Node();
    
    Solution.indexToWordCountCache.clear();
    Solution.root = root;
    Solution.morseCode = ".-...-.-...--......--....------.-.....---..--.-.----...-----.-.........-..--.-.-.----....--...-.--.--.--.--...-.-..--....--.--.-....--.-.-..--..-...-..-...-.......-.......-....--.--...-.-.......----....-.-.---..-.-.-.....-.-..----.--...-....--.-..-..-...-.........---...-.-.--..-.----....-..-.....--..-.-.-...........--.-.......--...-....---...-..---..-..--.--.....-.-.-..-.-.----.......-..-..-.----.--.-.-.---.-...-.....-....---..-..-..-----.-......-.-..---.-....--.-..-..--..-....-........-.-.....--...-....----.-......-...---.-...-.....-.-.-...----..-...........-.--.-.-.-....-..-..-.....-.-...-....-....-.-.-.-..--.---..-.-..-...--.-..--..-..-.--........-.-..-.-.----...---..-...-.-..-.-..--.-.-.-......-........--.-..-.-......---.-..-..-...--.-..-.-.........-...-..-...-.....--.-..-....--.----.-.-.-..-.--..----...--..-.-.------.....-..-.....----...--...--.-..-.-........--.-..-..--..-..-.-..-.--..--.-.-....-...-.-.----...-----.-.........--.-.----......-...-.-.-.-------..-....--..-.-.-...-.-.......-.-.-......-.-.-------..--...-.-..--..----....-...---.-...--.-..-.....-..-.------..-.-.-........-...-...-----.-.........-.--..-...-.-.-....--..-.-...--.-.-..--...-........--..-.-..-......--...-....-.-...-.....-.-.-------.------.--.-.-...-.-..--....--..........----....---..-...-.-.--.-.........-...-..-...-..-......--.-..-.-...--.....-...-.-......-...-...--.---..-.-.-.-...-.-.-..-.....--..........----....-.-..-.-..--.-.-.-...-..-------.--..--.-..-..-..--....----....-.-............-..-...-...--..----...-.-....-.-..--....-.-...-.--..--...-..-.........-.--..----....--.-.-...-..--....-......--.--...-.-......-.-..-.-..--..----..-..--..-....---.-..-..-----......-------..--.-..-....---..--......-..-.-.-.---.-...--.---..-.-..-...-.....--.-..-.-........--..----.--.-.-...-..--..--..-.-.-.-...-.-.-..-....-.-..--.....--.----.-.....-.-.....-..--...-.-..-..-....--..--....-....-...--.-..-----.-......--..-.-..-..-.--.--..-....-...-.-...--.-...--.-..--....--.......--..-.-...--.-..-....-....-...---..-....-...-..-.---.-.--......--.-.....-......-..--..-....-..-......-...-----.-.........---..-.-...--.-.-.--..-..--..--.-..-.--..-.-.-.......---.--...-.-....-.-.-------.-...-.------....-....--..-.-..--....---.---.--...-.-.......-.-.---..--...-----.-........----.---.-...-.-....---..-....--..-.--..-..----.--....-.-....---..-.-.-...--...-......-....-----.-......-....---..-....--..-.-...-.-.----....--....---.-..-.-..--.....--.---.-.-...-...-..--......--.-..-.-...-..--....---.--...-.--.--..-.---.-..-..-..----..-.-...-....---..--...-.-----.-...-.-.----.-..-...-.---..--.-.----.-..-...----..-....--.-...-..-..--.--..-...-.....--.-..-..-.-...--.-.-.--....-.--..-.-.-..--..--....--.--...-.-.......----..-...-.-.-.-...--.-..-.-.-..-...-..-......--.-.-.-....-...-.-..--..-..-.--.......--.-..-.-.-.....-..-.-..-...-.---....------..-.-......--......--.-..-.-..---.-..--...--.-..-.---..-...-.......-..-....-..-...-........-....---..-....--..-.-....-...--.-..-...--..-......-....-...-...--...---.-..--..----.-....--.-.----...-----.-.........--...-.-.----....-..-......-.-..-.-..--.-.-.-.......-..-.--.-..-.-.-....-...-.-.-..--....--........--.-..-..--..-.--...-.-.----....-..-..-.....-.--.-.-...........-.-.-.........--.-..-.-.-.-......--..-....-....-..----.--...-.....-......-.-..-.-..--.-.-.-.....--.-..-...-.....-...-.-..-.-.--..-....-....-..-.-..--.-.-.--..-.-..-..-.--.--..-....-.....-...--.-..-.-......-..-.--.-..-.-.-.....--.-.----...--..-.-...--......--.--..-....--.---..-.-.-....--..-....-...-....-....-.-...----..-....-...........--.-......-.......-..--.-......-...--.-........----.-----..--....-.-.----...........-.-.--.-..-.--.-.......--.-..-..--..-.-..---.--..-..--..---.-......-.-.........-...-..-..........-.-.-.-...-.--.-.....--.-..-..--..-.-.........--.-..-.-.-.-......--..-....-....-..----.--...-....-.-.---.-..-......--.----.-...-.--.........-.-----...---.-.......-.......--..-....-....-..-.-..........--.-.....-....--.----.-.-.-..-.--..----....-.-----...---.-.......--.-..-.-..--.-.-.-.........--.-.-....-..--..-.--..-.-..-.-...-...-...--.-.-.-..--.-.----...-----.-.....-.......--....-....-...--..----....-.-.-------..-.-.-....-.-.......--..-....-....-...--...---.-.....----.---.-..-.....-.-.----...--.--.......--......-..-.......-.-.-----.--..-..-.....-...--.......-.-..--------..-.---..-.--..-....-...--.-......-......---.--..-..--...-.-....-...-.-..--..-..-.--........-.-.----....-..-.....-...-.-.-.-..---.-.....-...--...-........--.--......------...-..-.....--.-..-.-...-.------..----.-.-..-.-----..--.-.-.--..-.--.-----.-..-..-.--.-..---..-----..-...-.-....---..--.--.--....-.---..----.-----..--.----.--.....--..-.---.----.-..-....-..----..-.--.---.--.-....--.-----.-..-....-.-..-..-.---.-.----...-...-...--...-------.-..-.-..-....-.-..-..-.--.-....-.-.-.-..----.-...-.-.....-.-.----......-.-..--..---.--.-..--.......-..-...-.-.-----......-.-..--....--..........----.........-.-..-......-.-.-....-.......-.......-..--....---.---.--...-.-......--...-........-.-----..-.------..-.-....-.-...-.-.-..-....-.-.----.......-.--..--...-.....-.-.----...--.-.--.-.----...--.....-........----.-..--...-...........--..-.-..-......---.--..-..--...-.-.....-..-...-.-......-.-......--.--...-.-........-...-.---.-.--.--..----.....-.-.-...-.-..-..-.-.....--.---.-.-.--.-.--..-.-..-..-.--.--..-........-.-..-...---.-..-.-.-....--....-...-.-.....-.....----..-......-.-......--.--..--.-.-...-.....-..-.--.-..-.-.-.-......-.-......-.-..--..-..-----.....-.......--..-..--...-......-.....-.....----.....-..--....----.-..--..-..-.--.......--.-..-........--......-....-....--...-........--.-.-...-.-.----...-----.-.....-...--.-.--....--..-.....---....--..--..-......-.-..-....-..-...--.-.----...-..-..-.-.-..-...-.....-..-....-..---.-...-.....-..-...-.-..----.--...-...-.-.-..----.-...-...---.-.....-.-....--...-..--.-..-..-...-.-..-..-..--....----.-..--..-..-.--........-----.-......-.--..-..----..-...-...-.-....-.-.........-........--.-..-.-.....---...-.-.-------..-.--..--...-..-...-..-.-..........---..-...--.-..-.-..--..-....-........-.-....-.-.----...-..-..-.-.-.....------.-.....-.-......-.......-..--....----.-.-------..--...-.-..--..----....-....-...-..........-.-.-......-....--.-........--...-.........-..--.---..-.-..-......-.-.-------..--...-.-..--..----.....-.-.-..--..-..-.--........-----.-.....-....--...-......-.....-...-....-.-.....-.-...--.....-...-.-..--...-..-....-...---.....--.-...........-....-..-.-.....--.-.-...-..--....-.-.-....-.....-------......-......-.....-..-..--.-.-...--...-.------....-....--..-.-..--.-......--..--.--...----.-.--....--.-..----.-..-...-...-....-...-....-..--....----.-..--..-..-.--.......--.-..-.----....-....--..-----...-...--.-.........-......--..........----.....-.-----.-.....-.----.-----.-.-.....-.-..-...-..-..-.--.-..-.-.-.-..-...-.-....--..--.-.-...-..--..-.-.-.----..-..--..--.-..-..-.....-.-..-...-..-..-.--.-..-.-.-.-..-...-.-....--...-..-...-..-.-.-.....-..--.-..-..-...-.-.-...-..-.-.........---..--...-.--..--...-...-..-..--....----.-..--..-..-.--........-....--...-......-.......--.-.......--....-.--...---.-.......--..-...-....-....-...-.-..-.-.-------..--...-.-..--..----..--.--.--.-.--..-.......-....-...-...-......--..-.-.-...-.-...-.-..-------.-.-.-...-.-..-..-...-.-.--.-.-...-..--..-..--....----.-..--..-..-.--.......--.-..-..-.-..--....----.-...--.....----...--.-..-...--...-........--.-.-......----.-............-.---.....-.-.....-.-.-------.-.-.-..-..-.-..-....-...-..-...-.-.--.-.--.-...--..-.-...--.-...-...-..-.....-..-.-.-.---.-..-.-...------..-.-.-........-..-.--.-..-.-.-.-.......-.-..-.-..--..----..-.-.......-..-...-..-.-.-.....--..-.-..-....-..-.-...--......-...-...-.....----.-....-.........-.-..-....-...---------.-.-...-..---..-.-..--..--.-.-------..--...-.-..--..----....--.-.-...-..--.....-..-..--....----.-..--..-..-.--.........--..-.-.--..-...-..--.-.....-.-....--..-.-....--..---..--.-....--..-.-....--...--.---..-...-.--.-.-..-........-.-....--...-........--..-.-..-...-...-...--..----.-.-......-...-....-...--....--.-...-......---.-.....-.----.-----.-.-.-.....-...--...-.........--..-.-...--.....-..-.--.-.-..-..-....--..--.--...-...--.....-...------.-....--..-.-.--...-......--.-..-..-......-...-.-..--..........----..--....-..-...-.-.--..-.-..-...-...---....-.-....--...---.--..-...-.-..---..-.---..--..-...--.-.......-...-...-.-..--...--....-....--.-..-.-....--.-.----.--.----...---..-...---..--...-...-.-.---.....-...-......-.--.....--.......-....-.-..----........-....-.-.-......-.....-..-...--.-...-----.--..-...-.-.---..---.--.....-...-..-.-...-..--..----.-....-...-..........-..........------.-.---.-...-.-..-..-......-.-..........-.---.-.......-...-......--.-..-..-..--.-..-.....-..--.-...--.-..----.....--..-.....-...-.--..-....-.--..-.-.--.........-.--..----....--.-.-...-..--.....-.-.-.---.-...-..--.-.-.....-..--.--.--.-.-.--..-....-....-.-----.-.....--.-..-..-.....-.-...-.-....-.-..--....-.-...-.--..--...-...-...-.-.-..-..-...-...--.-.-.-.....-.-...-..-.--.-..-.-.-.-..-..--..........----..-..-.------..-.-.-........-..-.-...-.-.-..-.......-..-.--.-..-.-.-.-.....-......-..-......-..--.-..-..-...-.-..-.-.-..---....-...---.-.-...-..--.-.-.-.-......--.-...----.-.-.-.-.---.-....--...-..-...-..-.-.-.--..----.----........-...-......-.....----.-.--..-....-.....--...-...........--.-..-..--..-.-.--.-..-..-..--.-..-.....--..--.--....--.-.....-..-.-..--.-.--....--...-......-...-..-.....---....--..--..-......-.-..-....-.-....-....-.-.-..----.-....-...-.-...---.--...-.-..-........-......-....--..-..--..-.--....-...-.-.---..-.-...--.-.--.-.-------..--...--.-..-..-..-.-----.-......-....---..-....--..-.-.-..---.-....--.-..-..-...--.-..-.--..--.-...---.--.-..-...-..-..........--.-..-..--..-.-.....-..-..-..--...--.---..-.-.-......-.-....-.---..-...-...-.-.....-.-....-...---.-.-...-..--.-.-.-.-......--.-...-...--....-...-.------....-....--..-.--..--.--...-..----.-.-...-..-..-..--.-.-..";
    Solution.finalIndex = Solution.morseCode.length();

    root.acceptWord("VGKEY");
    root.acceptWord("FJCXO");
    root.acceptWord("OGOCZ");
    root.acceptWord("IYXPP");
    root.acceptWord("QVTCA");
    root.acceptWord("CMFPY");
    root.acceptWord("VHPYX");
    root.acceptWord("GBDROR");
    root.acceptWord("JOJXX");
    root.acceptWord("QSEGW");
    root.acceptWord("QWEUN");
    long count = root.acceptMorse(0);
    
    assertThat(count, is(1L));
  }
}