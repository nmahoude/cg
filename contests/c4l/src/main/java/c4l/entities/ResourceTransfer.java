package c4l.entities;

import c4l.Referee;

public class ResourceTransfer extends Transfer {
  MoleculeType resourceType;

  public ResourceTransfer(PlayerData player, MoleculeType type) {
    super(player);
    this.resourceType = type;
  }

  @Override
  public void apply(Referee referee) {
    player.storage[resourceType.index]++;
    referee.molecules.put(resourceType, referee.molecules.get(resourceType) - 1);
  }

  @Override
  public Translatable getSummary() {
    return new Translatable("takeMolecule", player.index, resourceType.name());
  }
}