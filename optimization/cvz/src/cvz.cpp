<<<<<<< Updated upstream
#include <iostream>
#include <bits/stdc++.h>

#define null NULL
#define Zombie Person
#define Human Person
#define Ash Person

using namespace std;

int WIDTH = 16000;
int HEIGHT = 9000;
int ZOMBIE_MOVE_RADIUS = 400;
int ASH_MOVE_RADIUS = 1000;
int ASH_KILLZONE = 2000;
int ASH_KILLZONE_2 = ASH_KILLZONE * ASH_KILLZONE;

class Person;
Person* getClosestHuman(Person *base);

Person *ash = NULL;
int humanCount;
Person **humans = NULL;
int zombieCount;
Person **zombies = NULL;

class Person {
private:
  int _x, _y;
  bool _alive;
  Person* _target;
public:
  int x, y;
  int nextX, nextY; // TODO temp, need to calculate it in simulation !
  bool alive = true;
  Person* target = null;

  void backup() {
    _x = x;
    _y = y;
    _alive = alive;
    _target = target;
  }

  void restore() {
    x = _x;
    y = _y;
    alive = _alive;
    target = _target;
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

  int dist2(Person *other) {
    return (other->x - x) * (other->x - x) + (other->y - y) * (other->y - y);
  }

  void acquireTarget() {
    if (target == NULL || target->alive) {
      target = getClosestHuman(this);
    }

    /* ash may have move, not humans */
    if (dist2(target) < dist2(ash)) {
      target = ash;
    }
  }

  void zombieMove() {
    this->move(target->x, target->y, ZOMBIE_MOVE_RADIUS);
  }

  void move(int tx, int ty, int radius) {
    int dist = (int) sqrt((x - tx) * (x - tx) + (y - ty) * (y - ty));
    if (dist < radius * radius) {
      x = tx;
      y = ty;
    } else {
      int dx = radius * (tx - x) / dist;
      int dy = radius * (ty - y) / dist;
      x += dx;
      y += dy;
    }
  }
};

Person* getClosestHuman(Person *base) {
  int bestDist = INT_MAX;
  Person* best = NULL;

  for (int h = 0; h < humanCount; h++) {
    Person* p = humans[h];
    if (!p->alive)
      continue;

    double distBaseToHuman = sqrt(base->dist2(p));
    if (distBaseToHuman < bestDist) {
      bestDist = distBaseToHuman;
      best = p;
    }
  }
  return best;
}

class Simulation {
private:
  void zombiesMove() {
    for (int i = 0; i < zombieCount; i++) {
      Zombie* zombie = zombies[i];
      if (!zombie->alive)
        continue;

      zombie->acquireTarget();
      zombie->zombieMove();
    }
  }

  void ashMove(int x, int y) {
    ash->move(x, y, ASH_MOVE_RADIUS);
  }

  void ashKillsZombies() {
    int bonus = 1;
    int score = 0;
    for (int i = 0; i < zombieCount; i++) {
      Zombie* zombie = zombies[i];
      if (!zombie->alive)
        continue;
      if (ash->dist2(zombie) < ASH_KILLZONE_2) {
        score += nbHumansALive * 10 * fib(bonus);
        bonus++;
      }
    }
  }

  void zombiesKillHumans() {

  }

public:
  void simulate() {
    zombiesMove();
    ashMove();
    ashKillsZombies();
    zombiesKillHumans();

    int deadHuman = 0;
    for (int i = 0; i < zombieCount; i++) {
      Zombie* zombie = zombies[i];
      if (!zombie->alive)
        continue;

      if (zombie->target != NULL && zombie->target->alive) {
        /* ash may have move, not humans*/
        if (zombie->dist2(zombie->target) < zombie->dist2(ash)) {
          zombie->target = ash;
        }
        // continue chasing this target

      }

      for (int h = 0; h < humanCount; h++) {
        Human* human = humans[h];
        if (!human->alive)
          continue;
        if (zombie->dist2(human) < ZOMBIE_MOVE_RADIUS * ZOMBIE_MOVE_RADIUS) {
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

      double distAshToHuman = sqrt(ash->dist2(p));

      cerr << "Dist A2H " << h << " = " << distAshToHuman << endl;
      bool canSave = true;
      for (int z = 0; z < zombieCount; z++) {
        Person* zombie = zombies[z];
        if (!zombie->alive)
          continue;
        double distZombieToHuman = sqrt(zombie->dist2(p));
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

    if (best == NULL) {
      best = getClosestHuman(ash);
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
=======
#include <iostream>
#include <bits/stdc++.h>

#define null NULL
#define Zombie Person
#define Human Person
#define Ash Person

using namespace std;

int WIDTH = 16000;
int HEIGHT = 9000;
int ZOMBIE_MOVE_RADIUS = 400;
int ASH_MOVE_RADIUS = 1000;
int ASH_KILLZONE = 2000;
int ASH_KILLZONE_2 = ASH_KILLZONE * ASH_KILLZONE;

class Person;
Person* getClosestHuman(Person *base);

Person *ash = NULL;
int humanCount;
Person **humans = NULL;
int zombieCount;
Person **zombies = NULL;

class Person {
private:
  int _x, _y;
  bool _alive;
  Person* _target;
public:
  int x, y;
  int nextX, nextY; // TODO temp, need to calculate it in simulation !
  bool alive = true;
  Person* target = null;

  void backup() {
    _x = x;
    _y = y;
    _alive = alive;
    _target = target;
  }

  void restore() {
    x = _x;
    y = _y;
    alive = _alive;
    target = _target;
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

  int dist2(Person *other) {
    return (other->x-x)*(other->x-x) + (other->y-y)*(other->y-y);
  }

  void acquireTarget() {
    if (target == NULL || target->alive) {
      target = getClosestHuman(this);
    }

    /* ash may have move, not humans */
    if (dist2(target) < dist2(ash)) {
      target = ash;
    }
  }

  void zombieMove() {
    this->move(target->x, target->y, ZOMBIE_MOVE_RADIUS);
  }

  void move(int tx, int ty, int radius) {
    int dist = (int)sqrt((x-tx)*(x-tx) + (y-ty)*(y-ty));
    if (dist < radius*radius) {
      x = tx;
      y = ty;
    } else {
      int dx = radius * (tx - x) / dist;
      int dy = radius * (ty - y) / dist;
      x += dx;
      y += dy;
    }
  }
};

Person* getClosestHuman(Person *base) {
  int bestDist = INT_MAX;
  Person* best = NULL;

  for (int h = 0; h < humanCount; h++) {
    Person* p = humans[h];
    if (!p->alive) continue;

    double distBaseToHuman = sqrt(base->dist2(p));
    if (distBaseToHuman < bestDist) {
      bestDist= distBaseToHuman;
      best = p;
    }
  }
  return best;
}

class Simulation {
private:
  void zombiesMove() {
    for (int i = 0; i < zombieCount; i++) {
      Zombie* zombie = zombies[i];
      if (!zombie->alive) continue;

      zombie->acquireTarget();
      zombie->zombieMove();
    }
  }

  void ashMove(int x, int y) {
    ash->move(x, y, ASH_MOVE_RADIUS);
  }

  void ashKillsZombies() {
    int bonus = 1;
    int score = 0;
    for (int i = 0; i < zombieCount; i++) {
      Zombie* zombie = zombies[i];
      if (!zombie->alive) continue;
      if (ash->dist2(zombie) < ASH_KILLZONE_2) {
        score += nbHumansALive * 10 * fib(bonus);
        bonus++;
      }
    }
  }

  void zombiesKillHumans() {

  }

public:
  void simulate() {
    zombiesMove();
    ashMove();
    ashKillsZombies();
    zombiesKillHumans();

    int deadHuman = 0;
    for (int i = 0; i < zombieCount; i++) {
      Zombie* zombie = zombies[i];
      if (!zombie->alive) continue;

      if (zombie->target != NULL
          && zombie->target->alive) {
        /* ash may have move, not humans*/
        if (zombie->dist2(zombie->target) < zombie->dist2(ash)) {
          zombie->target = ash;
        }
        // continue chasing this target

      }

      for (int h = 0; h < humanCount; h++) {
        Human* human = humans[h];
        if (!human->alive) continue;
        if (zombie->dist2(human) < ZOMBIE_MOVE_RADIUS * ZOMBIE_MOVE_RADIUS) {
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

      double distAshToHuman = sqrt(ash->dist2(p));

      cerr << "Dist A2H " << h << " = " << distAshToHuman << endl;
      bool canSave = true;
      for (int z = 0; z < zombieCount; z++) {
        Person* zombie = zombies[z];
        if (!zombie->alive)
          continue;
        double distZombieToHuman = sqrt(zombie->dist2(p));
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

    if (best == NULL) {
      best = getClosestHuman(ash);
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
>>>>>>> Stashed changes
