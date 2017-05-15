package c4l.molecule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import c4l.GameState;
import c4l.entities.MoleculeType;

public class MoleculeInfo {
  int molecules[] = new int [GameState.MOLECULE_TYPE];
  public int sampleIndex;
  public int health;
  
  @Override
  public String toString() {
    return "sample="+sampleIndex+" "+Arrays.toString(molecules)+"\n\r";
  }
  /**
   * return the list of needed molcules, without how much we need
   * @return
   */
  public List<MoleculeType> getNeededMolecules() {
    List<MoleculeType> types = new ArrayList<>();
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (molecules[i]>0) {
        types.add(MoleculeType.values()[i]);
      }
    }
    return types;
  }
}
