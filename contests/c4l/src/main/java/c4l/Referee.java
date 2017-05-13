package c4l;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import c4l.entities.Diagnosis;
import c4l.entities.ModulePair;
import c4l.entities.MoleculeType;
import c4l.entities.PlayerData;
import c4l.entities.ProjectCompletion;
import c4l.entities.Sample;
import c4l.entities.SampleTransfer;
import c4l.entities.ScienceProject;
import c4l.entities.Transfer;

public class Referee {

  public List<PlayerData> players;
  public List<Transfer> transfers;
  public  Map<Sample, SampleTransfer> cloudRequests;
  public  List<Diagnosis> diagnoses;
  public  List<ProjectCompletion> projectCompletions;
  public  Random random;
  public  List<LinkedList<Sample>> samplePool;
  public List<Sample> storedSamples;
  public List<ScienceProject> scienceProjects;
  private long seed;
  public Map<MoleculeType, Integer> molecules;
  public Map<ModulePair, Integer> distances;

}
