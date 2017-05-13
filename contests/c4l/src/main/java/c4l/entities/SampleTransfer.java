package c4l.entities;

import c4l.Referee;

public class SampleTransfer extends Transfer {
  Sample sample, clone;
  Bound bound;

  public SampleTransfer(PlayerData player, Sample sample, Bound bound) {
    super(player);
    this.sample = sample;
    this.bound = bound;
  }

  @Override
  public void apply(Referee referee) {
    if (bound.equals(Bound.TO_DIAGNOSIS)) {
      player.tray.remove(sample);
      if (referee.storedSamples.stream().noneMatch(stored -> stored.equals(sample))) {
        referee.storedSamples.add(sample);
      }

    } else if (bound.equals(Bound.FROM_SAMPLES)) {
      player.tray.add(sample);
    } else if (bound.equals(Bound.FROM_DIAGNOSIS)) {
      if (clone == null) {
        player.tray.add(sample);
        referee.storedSamples.remove(sample);
      } else {
        player.tray.add(clone);
      }
    }
  }

  @Override
  public Translatable getSummary() {
    if (bound.equals(Bound.TO_DIAGNOSIS)) {
      return new Translatable("upload", player.index, sample.id);
    } else if (bound.equals(Bound.FROM_SAMPLES)) {
      return new Translatable("newSample", player.index, sample.id);
    } else {
      return new Translatable("download", player.index, sample.id);
    }
  }

  public void setClone(Sample clonedSample) {
    this.clone = clonedSample;
  }

}