#include <iostream>
#include <bits/stdc++.h>

using namespace std;

int WIDTH = 16000;
int HEIGHT = 9000;

class Person;

Person *ash = NULL;
int humanCount;
Person **humans = NULL;
int zombieCount;
Person **zombies = NULL;

class Person {
private:
  int _x, _y;
  bool _alive;
public:
  int x, y;
  int nextX, nextY; // TODO temp, need to calculate it in simulation !
  bool alive = true;

  void backup() {
    _x = x;
    _y = y;
    _alive = alive;
  }

  void restore() {
    x = _x;
    y = _y;
    alive = _alive;
  }

  void update(int x, int y) {
    this->x = x;
    this->y = y;
    this->alive = true;
  }

  void update(int x, int y, int nextX, int nextY) {
    update(x, y);
    this->nextX = nextX;
    this->nextY = nextY;
  }
};

class Simulation {
public:
  void simulate() {
    int deadHuman = 0;
    for (int i = 0; i < zombieCount; i++) {
      Person* zombie = zombies[i];
      if (!zombie->alive)
        continue;

      for (int h = 0; h < humanCount; h++) {
        Person* human = humans[h];
        if (!human->alive)
          continue;
        if ((zombie->x - human->x) * (zombie->x - human->x)
            + (zombie->y - human->y) * (zombie->y - human->y) < 400 * 400) {
          human->alive = false;
          deadHuman++;
        }
      }
    }
    cerr << "Calculated death = " << deadHuman << endl;
  }
};

class AI {
  int x, y;

public:
  void think() {
    int bestDist = INT_MAX;
    Person* best = NULL;

    for (int h = 0; h < humanCount; h++) {
      Person* p = humans[h];
      cerr << "Human " << h << " is alive ? " << p->alive << endl;
      if (!p->alive)
        continue;

      double distAshToHuman = sqrt(
          (p->x - ash->x) * (p->x - ash->x)
              + (p->y - ash->y) * (p->y - ash->y));

      cerr << "Dist A2H " << h << " = " << distAshToHuman << endl;
      bool canSave = true;
      for (int z = 0; z < zombieCount; z++) {
        Person* zombie = zombies[z];
        if (!zombie->alive)
          continue;
        double distZombieToHuman = sqrt(
            (p->x - zombie->x) * (p->x - zombie->x)
                + (p->y - zombie->y) * (p->y - zombie->y));
        cerr << "Dist Z2H " << z << " = " << distZombieToHuman << endl;
        if (distZombieToHuman / 400 < distAshToHuman / 1000) {
          cerr << "Cant save it ! " << endl;
          canSave = false;
          break;
        }
      }
      if (!canSave)
        continue;

      if (distAshToHuman < bestDist) {
        bestDist = distAshToHuman;
        best = p;
      }
    }
    if (best != NULL) {
      x = best->x;
      y = best->y;
    }
  }

  string output() {
    std::string s;
    s.append(to_string(x));
    s.append(" ");
    s.append(to_string(y));

    return s;
  }

};

int main() {
  ash = new Person();

  AI ai;
  Simulation sim;

  // game loop
  while (1) {
    int x;
    int y;

    // read Ash
    cin >> x >> y;
    cin.ignore();
    ash->update(x, y);
    ash->backup();

    // read humans
    int hc;
    cin >> hc;
    cin.ignore();
    if (humans == NULL) {
      humanCount = hc;
      humans = new Person*[humanCount];
      for (int i = 0; i < humanCount; i++) {
        humans[i] = new Person();
      }
    } else {
      for (int i = 0; i < humanCount; i++) {
        humans[i]->alive = false;
      }
    }

    for (int i = 0; i < hc; i++) {
      int humanId;
      int humanX;
      int humanY;
      cin >> humanId >> humanX >> humanY;
      cin.ignore();
      humans[humanId]->update(humanX, humanY);
      humans[humanId]->backup();
    }

    // readZombies
    int zc;
    cin >> zc;
    cin.ignore();
    if (zombies == NULL) {
      zombieCount = zc;
      zombies = new Person*[zombieCount];
      for (int i = 0; i < zombieCount; i++) {
        zombies[i] = new Person();
      }
    } else {
      for (int i = 0; i < zombieCount; i++) {
        zombies[i]->alive = false;
      }
    }
    for (int i = 0; i < zc; i++) {
      int zombieId;
      int zombieX;
      int zombieY;
      int zombieXNext;
      int zombieYNext;
      cin >> zombieId >> zombieX >> zombieY >> zombieXNext >> zombieYNext;
      cin.ignore();
      zombies[zombieId]->update(zombieX, zombieY, zombieXNext, zombieYNext);
      zombies[zombieId]->backup();
    }

    // Write an action using cout. DON'T FORGET THE "<< endl"
    // To debug: cerr << "Debug messages..." << endl;
    sim.simulate();

    ai.think();
    cout << ai.output() << endl; // Your destination coordinates
  }
}
