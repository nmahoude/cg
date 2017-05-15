package c4l;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.entities.ScienceProject;

public class GameState {
  public final static int MOLECULE_TYPE = 5;
  
  public Robot[] robots = new Robot[2];
  public int availables[] = new int[5];
  public int sampleCount;
  public List<Sample> availableSamples = new ArrayList<>();
  private int projectCount;
  public List<ScienceProject> scienceProjects = new ArrayList<>();
  
  public GameState() {
    robots[0] = new Robot();
    robots[1] = new Robot();
  }

  public void readAvailables(Scanner in) {
    availables[0] = in.nextInt();
    availables[1] = in.nextInt();
    availables[2] = in.nextInt();
    availables[3] = in.nextInt();
    availables[4] = in.nextInt();
  }

  public void initRound() {
    availableSamples.clear();
    robots[0].clearForRound();
    robots[1].clearForRound();
  }

  public void readSamples(Scanner in) {
    sampleCount = in.nextInt();
    for (int i = 0; i < sampleCount; i++) {
      int sampleId = in.nextInt();
      int carriedBy = in.nextInt();
      int rank = in.nextInt();
      String expertiseGain = in.next();
      int health = in.nextInt();
      int costs[] = new int[] { in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt() };

      MoleculeType giveExpertise = null;
      if (!"-1".equals(expertiseGain) && !"0".equals(expertiseGain)) {
        giveExpertise = MoleculeType.valueOf(expertiseGain);
      }
      Sample sample = new Sample(costs, health, giveExpertise);
      sample.id = sampleId;
      
      if (carriedBy != -1) {
        robots[carriedBy].carriedSamples.add(sample);
      } else {
        availableSamples.add(sample);
      }
    }
  }

  public void updateScienceProjects() {
    for (ScienceProject project : scienceProjects) {
      if (project.doneBy !=-1) continue;
      for (int p=0;p<2;p++) {
        boolean good = true;
        for (int i=0;i<MOLECULE_TYPE;i++) {
          if (robots[p].expertise[i] < project.expertiseNeeded[i]) good = false;
        }
        if (good) {
          project.doneBy = p;
        }
      }
    }
  }
  public void readScienceProjects(Scanner in) {
    projectCount = in.nextInt();
    for (int i = 0; i < projectCount; i++) {
      ScienceProject project = new ScienceProject();
      project.read(in);
      scienceProjects.add(project);
    }    
  }

  public void debugScienceProjects() {
    for (ScienceProject project : scienceProjects) {
      System.err.println(project.toString());
    }
  }
}
