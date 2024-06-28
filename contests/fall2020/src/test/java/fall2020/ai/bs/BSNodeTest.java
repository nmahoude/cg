package fall2020.ai.bs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BSNodeTest {

  
  @Test
  void spellCost() throws Exception {
    assertThat(spellCost(0b0111_0100,0)).isEqualTo(0b0111_0100_1111);
    assertThat(spellCost(0b0111_0100,1)).isEqualTo(0b0111_1111_0100);
    
    assertThat(0b0111_0100_0100 >> 4*1 & 0b1111).isEqualTo(4);
    assertThat(0b0111_0100_0100 >> 4*2 & 0b1111).isEqualTo(7);
  }
  
  
  @Test
  void canBuy5SpellsFor0In5Turns() throws Exception {
    assertThat(spellCost(0b0101_0100_0011_0010_0001_0000,0)).isEqualTo(0b0100_0011_0010_0001_0000_1111);
    assertThat(spellCost(0b0100_0011_0010_0001_0000_1111,0)).isEqualTo(0b0011_0010_0001_0000_1111_1111);
    assertThat(spellCost(0b0011_0010_0001_0000_1111_1111,0)).isEqualTo(0b0010_0001_0000_1111_1111_1111);
    assertThat(spellCost(0b0010_0001_0000_1111_1111_1111,0)).isEqualTo(0b0001_0000_1111_1111_1111_1111);
    assertThat(spellCost(0b0001_0000_1111_1111_1111_1111,0)).isEqualTo(0b0000_1111_1111_1111_1111_1111);
    assertThat(spellCost(0b0000_1111_1111_1111_1111_1111,0)).isEqualTo(0b1111_1111_1111_1111_1111_1111);
  }
  
  
  
  int spellCost(int spellsCost, int t) {
    int result = (spellsCost & BSNode.allOverMask[t]) << 4 & BSNode.allTomesMask// on décale tous les prix au dessus 
        | (spellsCost & BSNode.allUnderMask[t])  // on remet les prix du dessous inchangé
        | BSNode.tomesMask[t]; // on met le prix à 15 pour ne plus l'acheter

//    System.out.println(Integer.toBinaryString(spellsCost));
//    System.out.println(Integer.toBinaryString(BSNode.allOverMask[t]));
//    System.out.println(Integer.toBinaryString(spellsCost & BSNode.allOverMask[t]));
//    System.out.println(Integer.toBinaryString((spellsCost & BSNode.allOverMask[t]) << 4));
//    System.out.println(Integer.toBinaryString(spellsCost & BSNode.allUnderMask[t]));
//    System.out.println(Integer.toBinaryString(BSNode.tomesMask[t]));
//    System.out.println(Integer.toBinaryString(BSNode.tomesMask[t]));
//    System.out.println(Integer.toBinaryString(result));
    

    return result;
  }
}
