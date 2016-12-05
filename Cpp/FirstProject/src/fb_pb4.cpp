class Fitness {
 public:
  bool gameWon;
  bool gameLost;
  int roundGameFinished;
  double snafPosCrit;

  double wizPosCrit;

  Fitness() {}
  INLINE void init() {
    // Upon initialization, should be the worst fitness possible
    gameWon = false;
    gameLost = false;
    roundGameFinished = 0;

    snafPosCrit = 0.0;
    wizPosCrit = 0.0;
  }
  INLINE bool operator<(const Fitness &other) const {
    LEX_LT(gameWon);
    if (gameWon) {
      LEX_GT(roundGameFinished);
    }

    LEX_GT(gameLost);
    if (gameLost) {
      LEX_LT(roundGameFinished);
    }
    LEX_LT(snafPosCrit);
    LEX_LT(wizPosCrit);
    return false;
  }
};
std::ostream &operator<<(std::ostream &oStream, const Fitness &fitness) {
  oStream << fitness.gameWon SPACE fitness.gameLost SPACE
                 fitness.roundGameFinished SPACE
                     fitness.snafPosCrit SPACE fitness.wizPosCrit;
  return oStream;
}
Fitness _fitness;

/******************************************************************************
*
*     GAME SPECIFIC CLASSES
*
* Holds the game logic
*
*******************************************************************************/

class Wiz {
 public:
  vec2 pos;
  double tLastUpdate = 0.0;
  vec2 speed;
  int eId;  // EntityId for output
  int pickCooldown = 0;
  int pickedSnaffleId;
  int teamId;
  bool carriesSnaffle;

  int accioCd = -1;
  int accioTarget;

  int flipendoCd = -1;
  int flipendoTarget;

  int petrificusTarget = -1;

  int obliviateCd = -1;

  INLINE void roundAll() {
    pos.round();
    speed.round();
  }
  INLINE bool hasSnaffle() const { return carriesSnaffle; }
  INLINE bool canGrab() const { return pickCooldown <= 0; }
  INLINE bool noAggroBudger() const { return obliviateCd > 0; }

  void compare(const Wiz &other) {
    LOGSIMIL(pos SPACE other.pos);
    assert(normSq(pos - other.pos) < EPSILON);
    LOGSIMIL(speed SPACE other.speed);
    assert(normSq(speed - other.speed) < EPSILON);
    LOGSIMIL(tLastUpdate SPACE other.tLastUpdate);
    assert(tLastUpdate == other.tLastUpdate);
    LOGSIMIL(eId SPACE other.eId);
    assert(eId == other.eId);
    LOGSIMIL(pickedSnaffleId SPACE other.pickedSnaffleId);
    assert(pickedSnaffleId == other.pickedSnaffleId);
    LOGSIMIL(teamId SPACE other.teamId);
    assert(teamId == other.teamId);
    LOGSIMIL(carriesSnaffle SPACE other.carriesSnaffle);
    assert(carriesSnaffle == other.carriesSnaffle);
    LOGSIMIL(accioTarget SPACE other.accioTarget);
    assert(accioTarget == other.accioTarget);
    LOGSIMIL(flipendoTarget SPACE other.flipendoTarget);
    assert(flipendoTarget == other.flipendoTarget);
    LOGSIMIL(petrificusTarget SPACE other.petrificusTarget);
    assert(petrificusTarget == other.petrificusTarget);
  }

  constexpr static double INVERSEMASS = 1.0 / 1.0;
  constexpr static double RADIUS = 400.0;
  constexpr static double FRICTION = 0.75;
  constexpr static double MOVETHRUST = 150.0;
  constexpr static double THROWTHRUST = 500.0;
  constexpr static vec2 GOALCENTER[2] = {vec2(MAPW, MIDGOALH),
                                         vec2(0.0, MIDGOALH)};
  constexpr static int COOLDOWN = 3;
};
std::ostream &operator<<(std::ostream &out, const Wiz &wiz) {
  return out << "Wiz :" SPACE std::setw(2)
             << wiz.eId SPACE wiz.pos SPACE wiz.speed SPACE wiz
                    .tLastUpdate SPACE wiz.pickCooldown SPACE wiz
                    .pickedSnaffleId SPACE wiz.teamId SPACE wiz.carriesSnaffle;
}
constexpr vec2 Wiz::GOALCENTER[2];

class Pole {
 public:
  const vec2 pos;
  constexpr static double RADIUS = 300.0;
};

enum class WallType { Top, Bottom, Left, Right };
enum class EntityType { Wiz, Snaffle, Budger, Pole, Wall };
class Wall {
 public:
  const WallType wallType;
};

class Snaffle {
 public:
  vec2 pos;
  double tLastUpdate = 0.0;
  vec2 speed;
  int eId;
  bool isAlive;
  bool isGrabbed;
  bool wasGrabbed;
  int grabberId;
  inline bool canCollide() { return isAlive && !isGrabbed; }
  INLINE bool canBeGrabbed() { return isAlive && !isGrabbed; }
  INLINE void roundAll() {
    pos.round();
    speed.round();
  }

  void compare(const Snaffle &other) {
    LOGSIMIL(isAlive SPACE other.isAlive);
    assert(isAlive == other.isAlive);
    if (!isAlive) {
      return;
    }
    LOGSIMIL(eId SPACE other.eId);
    assert(eId == other.eId);
    LOGSIMIL(tLastUpdate SPACE other.tLastUpdate);
    assert(tLastUpdate == other.tLastUpdate);
    LOGSIMIL(pos SPACE other.pos);
    assert(normSq(pos - other.pos) < EPSILON);
    LOGSIMIL(speed SPACE other.speed);
    assert(normSq(speed - other.speed) < EPSILON);
    LOGSIMIL(tLastUpdate SPACE other.tLastUpdate);
    assert(tLastUpdate == other.tLastUpdate);
    LOGSIMIL(isGrabbed SPACE other.isGrabbed);
    assert(isGrabbed == other.isGrabbed);
    LOGSIMIL(grabberId SPACE other.grabberId);
    assert(grabberId == other.grabberId);
  }
  constexpr static double INVERSEMASS = 1.0 / 0.5;
  constexpr static double RADIUS = 150.0;
  constexpr static double FRICTION = 0.75;
};
std::ostream &operator<<(std::ostream &out, const Snaffle &sna) {
  return out << "Sna :" SPACE std::setw(2)
             << sna.eId SPACE sna.pos SPACE sna.speed SPACE
                    sna.tLastUpdate SPACE sna.isAlive SPACE
                        sna.isGrabbed SPACE sna.grabberId;
}

class Budger {
 public:
  vec2 pos;
  double tLastUpdate = 0.0;
  vec2 speed;
  int eId;
  int lastWizId = -1;

  INLINE void roundAll() {
    pos.round();
    speed.round();
  }

  void compare(const Budger &other) {
    LOGSIMIL(pos SPACE other.pos);
    assert(normSq(pos - other.pos) < EPSILON);
    LOGSIMIL(speed SPACE other.speed);
    assert(normSq(speed - other.speed) < EPSILON);
    LOGSIMIL(tLastUpdate SPACE other.tLastUpdate);
    assert(tLastUpdate == other.tLastUpdate);
    LOGSIMIL(eId SPACE other.eId);
    assert(eId == other.eId);
    LOGSIMIL(lastWizId SPACE other.lastWizId);
    assert(lastWizId == other.lastWizId);
  }
  constexpr static double INVERSEMASS = 1.0 / 8.0;
  constexpr static double RADIUS = 200.0;
  constexpr static double FRICTION = 0.9;
};
std::ostream &operator<<(std::ostream &out, const Budger &bud) {
  return out << "Bud :" SPACE std::setw(2)
             << bud.eId SPACE bud.pos SPACE bud.speed SPACE bud.tLastUpdate;
}
enum class ActionType { Move, Throw, Obliv, Petrif, Accio, Flip };

class MapData {
  // Holds the information about the game in a global variable, to avoid
  // copying
  // everything.
  // Initialized during the fist round or at the beginning of a round
 public:
  constexpr static Pole poles[4] = {{vec2(0.0, HIGHGOALH)},
                                    {vec2(0.0, LOWGOALH)},
                                    {vec2(MAPW, HIGHGOALH)},
                                    {vec2(MAPW, LOWGOALH)}};
  constexpr static Wall walls[4] = {
      {WallType::Top}, {WallType::Bottom}, {WallType::Left}, {WallType::Right}};
  constexpr static Segment goals[2] = {
      Segment(vec2(MAPW, HIGHGOALH - Pole::RADIUS - Snaffle::RADIUS),
              vec2(MAPW, LOWGOALH + Pole::RADIUS + Snaffle::RADIUS)),
      Segment(vec2(0.0, HIGHGOALH - Pole::RADIUS - Snaffle::RADIUS),
              vec2(0.0, LOWGOALH + Pole::RADIUS + Snaffle::RADIUS))};
  MapData(){};
};
constexpr Pole MapData::poles[];
constexpr Wall MapData::walls[];
constexpr Segment MapData::goals[];
INLINE bool isInMap(const vec2 &pos) {
  return pos.x >= 0.0 && pos.x <= MAPW && pos.y >= 0.0 && pos.y <= MAPH;
}
INLINE bool isNotNan(const vec2 &pos) {
  return pos.x >= -100000.0 && pos.x <= 100000.0 && pos.y >= -100000.0 &&
         pos.y <= 100000.0;
}

enum class CollisionType { DiskDiskMobile, DiskDiskFixed, DiskWall };

class Collision {
 public:
  EntityType e1Type;
  EntityType e2Type;
  int e1Id;
  int e2Id;
  double time;
  Collision() {}
  Collision(EntityType e1Type_, EntityType e2Type_, int e1Id_, int e2Id_,
            double time_)
      : e1Type(e1Type_),
        e2Type(e2Type_),
        e1Id(e1Id_),
        e2Id(e2Id_),
        time(time_) {}
  INLINE bool mustErase(const EntityType e1Type_, const EntityType e2Type_,
                        const int e1Id_, const int e2Id_) const {
    return (e1Type == e1Type_ && e1Id == e1Id_) ||
           (e1Type == e2Type_ && e1Id == e2Id_) ||
           (e2Type == e1Type_ && e2Id == e1Id_) ||
           (e2Type == e2Type_ && e2Id == e2Id_);
  }
};

class Game;

class WizAction {
 public:
  ActionType actionType;
  vec2 thrust;
  int id;
  WizAction() {}
  WizAction(ActionType actionType_, const vec2 &thrust_, double id_)
      : actionType(actionType_), thrust(thrust_), id(id_) {}
  void toOutput(std::ostream &oStream, const Game &game, int wId);
};

class GameAction {
 public:
  WizAction wizActions[2];
  GameAction(){};
  GameAction(const WizAction &wizActions1, const WizAction &wizActions2)
      : wizActions{wizActions1, wizActions2} {};
  void toOutput(std::ostream &oStream, const Game &game, int pId) {
    wizActions[0].toOutput(oStream, game, 2 * pId);
    wizActions[1].toOutput(oStream, game, 2 * pId + 1);
  }
};

class Game {
  // Contains full & minimal information about the game state. Must be easily
  // copiable
 public:
  Wiz wiz[4];
  int nSnaffles;
  Snaffle snaffles[7];
  Budger budgers[2];

  bool gameFinished = false;
  int turnGameFinished = 100;
  int winningTeam = 0;

  int gameTurn = 0;

  int teamScore[2] = {0, 0};
  int teamMana[2] = {0, 0};
  double spellCost[2] = {0.0, 0.0};

  void compare(const Game &other) {
    LOGSIMIL(nSnaffles SPACE other.nSnaffles);
    assert(nSnaffles == other.nSnaffles);
    for (int i = 0; i < 4; ++i) {
      wiz[i].compare(other.wiz[i]);
    }
    for (int i = 0; i < 7; ++i) {
      snaffles[i].compare(other.snaffles[i]);
    }
    for (int i = 0; i < 2; ++i) {
      budgers[i].compare(other.budgers[i]);
    }
  }

  void applyGameAction(GameAction &gameAction, int pId) {
    applyWizAction(gameAction.wizActions[0], 2 * pId);
    applyWizAction(gameAction.wizActions[1], 2 * pId + 1);
  }
  void applyWizAction(WizAction &wizAction, int wId) {
    // Has to be done at the beginning of the turn
    Wiz &wizard = wiz[wId];
    Snaffle &snaffle =
        snaffles[wizard.pickedSnaffleId];  // /!\ is well defined only if the
                                           // wizard was carrying a snaffle.
    if (wizAction.actionType != ActionType::Throw && wizard.hasSnaffle()) {
      // Release the snaffle where you are
      releaseSnaffle(wId, wizard.pickedSnaffleId);
    }
    switch (wizAction.actionType) {
      case (ActionType::Move):
        wizard.speed += wizAction.thrust * Wiz::INVERSEMASS;
        break;
      case (ActionType::Throw):
        assert(wizard.hasSnaffle());
        releaseSnaffle(wId, wizard.pickedSnaffleId);
        snaffle.speed += wizAction.thrust * Snaffle::INVERSEMASS;
        break;
      case (ActionType::Obliv):

        break;
      case (ActionType::Petrif):
        if (teamMana[wizard.teamId] >= 10) {
          wizard.petrificusTarget = wizAction.id - 2000;
          teamMana[wizard.teamId] -= 10;
        } else {
          spellCost[wizard.teamId] += BIGDOUBLE;
        }
        break;
      case (ActionType::Accio):
        if (teamMana[wizard.teamId] >= 20) {
          wizard.accioTarget = wizAction.id;
          wizard.accioCd = 7;
          teamMana[wizard.teamId] -= 20;
          spellCost[wizard.teamId] += 4000.0;
        } else {
          spellCost[wizard.teamId] += BIGDOUBLE;
        }
        break;
      case (ActionType::Flip):
        if (teamMana[wizard.teamId] >= 20) {
          wizard.flipendoTarget = wizAction.id;
          wizard.flipendoCd = 4;
          teamMana[wizard.teamId] -= 20;
          spellCost[wizard.teamId] += 1500.0;
        } else {
          spellCost[wizard.teamId] += BIGDOUBLE;
        }
        break;
      default:
        assert(false);
    }
  }

  void playOneTurn() {
    /*
    *   Apply the game logic
    */

    applyBudgerActions();
    applySpellActions();
    playTimeInterval(gameTurn, gameTurn + 1);
    applyEndTurnPhysics();
    updateCoolDowns();
    ++gameTurn;
  }

  void applyEndTurnPhysics() {
    for (int i = 0; i < 4; ++i) {
      updatePosition(wiz[i], gameTurn + 1);
      if (!isInMap(wiz[i].pos)) {
        LOGA(wiz[i]
                 .pos SPACE wiz[i]
                 .speed SPACE wiz[i]
                 .eId SPACE wiz[i]
                 .tLastUpdate SPACE gameTurn);
      }
      assert(isInMap(wiz[i].pos));

      wiz[i].speed *= Wiz::FRICTION;
      assert(isInMap(wiz[i].pos));

      wiz[i].roundAll();

      if (!isInMap(wiz[i].pos)) {
        LOGA(wiz[i].pos SPACE wiz[i].speed SPACE wiz[i].eId);
      }
      assert(isInMap(wiz[i].pos));
    }
    for (int i = 0; i < nSnaffles; ++i) {
      if (!snaffles[i].isAlive) {
        continue;
      }
      if (!snaffles[i].isGrabbed) {
        updatePosition(snaffles[i], gameTurn + 1);
        snaffles[i].speed *= Snaffle::FRICTION;
        snaffles[i].roundAll();

      } else {
        snaffles[i].tLastUpdate = wiz[snaffles[i].grabberId].tLastUpdate;
        snaffles[i].pos = wiz[snaffles[i].grabberId].pos;
        snaffles[i].speed = wiz[snaffles[i].grabberId].speed;
      }
      snaffles[i].wasGrabbed = false;
      assert(isInMap(snaffles[i].pos));
    }
    for (int i = 0; i < 2; ++i) {
      updatePosition(budgers[i], gameTurn + 1);
      budgers[i].speed *= Budger::FRICTION;
      budgers[i].roundAll();
      assert(isInMap(budgers[i].pos));
    }
  }

  void updateCoolDowns() {
    for (int i = 0; i < 4; ++i) {
      wiz[i].pickCooldown -= 1;
      // wiz[i].accioCd -= 1;
      // wiz[i].flipendoCd -= 1;
    }
    ++teamMana[0];
    ++teamMana[1];
  }

  void playTimeInterval(double intervalStart, double intervalEnd) {
    /* PLAYTIMEINTERVAL (currentTime, double simulGame.turnNumber)*/
    bool oneTurnFinished = false;
    double tmin = intervalStart;
    ++_simCounter;
    int nLoop = 0;

    for (int i = 0; i < 4; ++i) {
      if (wiz[i].pickCooldown == 0) {
        // The cooldown has gone down just this turn
        for (int j = 0; j < nSnaffles; ++j) {
          if (snaffles[j].canBeGrabbed() &&
              normSq(snaffles[j].pos - wiz[i].pos) < SQUARE(Wiz::RADIUS)) {
            applyCollision(Collision(EntityType::Wiz, EntityType::Snaffle, i, j,
                                     intervalStart));
            break;
          }
        }
      }
    }

    while (!oneTurnFinished) {
      ++nLoop;
      ++_bigSimCounter;
      oneTurnFinished = true;
      /*
      * Look for the next collision between two objects
      */
      Collision collision;
      collision.time = intervalEnd;

      /*
      * All wizard collisions
      */
      for (int i = 0; i < 4; ++i) {
        for (int j = i + 1; j < 4; ++j) {  // WIZARD WIZARD COLLISIONS
          double timeResult = diskDiskMobileIntersectionTime(
              wiz[i], wiz[j], SQUARE(Wiz::RADIUS + Wiz::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision =
                Collision(EntityType::Wiz, EntityType::Wiz, i, j, timeResult);
          }
        }

        for (int j = 0; j < nSnaffles; ++j) {
          // WIZARD SNAFFLE COLLISIONS (test for grab)
          if (wiz[i].canGrab() && snaffles[j].canBeGrabbed()) {
            double timeResult = diskDiskMobileIntersectionTime(
                wiz[i], snaffles[j], SQUARE(Wiz::RADIUS - 2.0));
            if (timeResult < collision.time && timeResult >= tmin) {
              collision = Collision(EntityType::Wiz, EntityType::Snaffle, i, j,
                                    timeResult);
            }
          }
        }

        for (int j = 0; j < 2; ++j) {  // WIZARD BUDGER COLLISIONS
          double timeResult = diskDiskMobileIntersectionTime(
              wiz[i], budgers[j], SQUARE(Wiz::RADIUS + Budger::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Wiz, EntityType::Budger, i, j,
                                  timeResult);
          }
        }

        for (int j = 0; j < 4; ++j) {  // WIZARD POLE COLLISIONS
          double timeResult = diskDiskFixedIntersectionTime(
              wiz[i], MapData::poles[j], SQUARE(Wiz::RADIUS + Pole::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision =
                Collision(EntityType::Wiz, EntityType::Pole, i, j, timeResult);
          }
        }

        // WIZARD WALL COLLISIONS
        if (wiz[i].speed.y > EPSILON) {
          // Hit bottom wall
          double timeResult =
              wiz[i].tLastUpdate +
              (MAPH - Wiz::RADIUS - wiz[i].pos.y) / (wiz[i].speed.y);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision =
                Collision(EntityType::Wiz, EntityType::Wall, i, 1, timeResult);
          }
        } else if (wiz[i].speed.y < -EPSILON) {
          // Hit top wall
          double timeResult = wiz[i].tLastUpdate +
                              (Wiz::RADIUS - wiz[i].pos.y) / (wiz[i].speed.y);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision =
                Collision(EntityType::Wiz, EntityType::Wall, i, 0, timeResult);
          }
        }
        if (wiz[i].speed.x > EPSILON) {
          // Hit right wall
          double timeResult =
              wiz[i].tLastUpdate +
              (MAPW - Wiz::RADIUS - wiz[i].pos.x) / wiz[i].speed.x;
          if (timeResult < collision.time && timeResult >= tmin) {
            collision =
                Collision(EntityType::Wiz, EntityType::Wall, i, 3, timeResult);
          }
        } else if (wiz[i].speed.x < -EPSILON) {
          // Hit left wall
          double timeResult = wiz[i].tLastUpdate +
                              (Wiz::RADIUS - wiz[i].pos.x) / (wiz[i].speed.x);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision =
                Collision(EntityType::Wiz, EntityType::Wall, i, 2, timeResult);
          }
        }
      }

      /*
      * All snaffles collisions
      */
      for (int i = 0; i < nSnaffles; ++i) {
        if (!snaffles[i].canCollide()) {
          continue;
        }

        for (int j = i + 1; j < nSnaffles; ++j) {  // SNAFFLE SNAFFLE COLLISIONS
          if (snaffles[j].canCollide()) {
            double timeResult = diskDiskMobileIntersectionTime(
                snaffles[i], snaffles[j],
                SQUARE(Snaffle::RADIUS + Snaffle::RADIUS));
            if (timeResult < collision.time && timeResult >= tmin) {
              collision = Collision(EntityType::Snaffle, EntityType::Snaffle, i,
                                    j, timeResult);
            }
          }
        }

        for (int j = 0; j < 2; ++j) {  // SNAFFLE BUDGER COLLISIONS
          double timeResult = diskDiskMobileIntersectionTime(
              snaffles[i], budgers[j],
              SQUARE(Snaffle::RADIUS + Budger::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Snaffle, EntityType::Budger, i, j,
                                  timeResult);
          }
        }

        for (int j = 0; j < 4; ++j) {  // SNAFFLE POLE COLLISIONS
          double timeResult = diskDiskFixedIntersectionTime(
              snaffles[i], MapData::poles[j],
              SQUARE(Snaffle::RADIUS + Pole::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Snaffle, EntityType::Pole, i, j,
                                  timeResult);
          }
        }
        // SNAFFLE WALL COLLISIONS
        if (snaffles[i].speed.y > EPSILON) {
          // Hit bottom wall
          double timeResult = snaffles[i].tLastUpdate +
                              (MAPH - Snaffle::RADIUS - snaffles[i].pos.y) /
                                  (snaffles[i].speed.y);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Snaffle, EntityType::Wall, i, 1,
                                  timeResult);
          }
        } else if (snaffles[i].speed.y < -EPSILON) {
          // Hit top wall
          double timeResult =
              snaffles[i].tLastUpdate +
              (Snaffle::RADIUS - snaffles[i].pos.y) / (snaffles[i].speed.y);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Snaffle, EntityType::Wall, i, 0,
                                  timeResult);
          }
        }
        if (snaffles[i].speed.x > EPSILON) {
          // Hit right wall
          double timeResult =
              snaffles[i].tLastUpdate +
              (MAPW - 1.0 - snaffles[i].pos.x) / snaffles[i].speed.x;
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Snaffle, EntityType::Wall, i, 3,
                                  timeResult);
          }
        } else if (snaffles[i].speed.x < -EPSILON) {
          // Hit left wall
          double timeResult = snaffles[i].tLastUpdate +
                              (1.0 - snaffles[i].pos.x) / (snaffles[i].speed.x);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Snaffle, EntityType::Wall, i, 2,
                                  timeResult);
          }
        }
      }

      /*
      * All Budger collisions
      */
      for (int i = 0; i < 2; ++i) {
        for (int j = i + 1; j < 2; ++j) {  // BUDGER BUDGER COLLISIONS
          double timeResult = diskDiskMobileIntersectionTime(
              budgers[i], budgers[j], SQUARE(Budger::RADIUS + Budger::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Budger, EntityType::Budger, i, j,
                                  timeResult);
          }
        }

        for (int j = 0; j < 4; ++j) {  // BUDGER POLE COLLISIONS
          double timeResult = diskDiskFixedIntersectionTime(
              budgers[i], MapData::poles[j],
              SQUARE(Budger::RADIUS + Pole::RADIUS));
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Budger, EntityType::Pole, i, j,
                                  timeResult);
          }
        }
        // BUDGER WALL COLLISIONS
        if (budgers[i].speed.y > EPSILON) {
          // Hit bottom wall
          double timeResult =
              budgers[i].tLastUpdate +
              (MAPH - Budger::RADIUS - budgers[i].pos.y) / (budgers[i].speed.y);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Budger, EntityType::Wall, i, 1,
                                  timeResult);
          }
        } else if (budgers[i].speed.y < -EPSILON) {
          // Hit top wall
          double timeResult = budgers[i].tLastUpdate +
                              (Budger::RADIUS - budgers[i].pos.y) /
                                  (budgers[i].speed.y - EPSILON);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Budger, EntityType::Wall, i, 0,
                                  timeResult);
          }
        }
        if (budgers[i].speed.x > EPSILON) {
          // Hit right wall
          double timeResult =
              budgers[i].tLastUpdate +
              (MAPW - Budger::RADIUS - budgers[i].pos.x) / budgers[i].speed.x;
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Budger, EntityType::Wall, i, 3,
                                  timeResult);
          }
        } else if (budgers[i].speed.x < -EPSILON) {
          // Hit left wall
          double timeResult = budgers[i].tLastUpdate +
                              (Budger::RADIUS - budgers[i].pos.x) /
                                  (budgers[i].speed.x - EPSILON);
          if (timeResult < collision.time && timeResult >= tmin) {
            collision = Collision(EntityType::Budger, EntityType::Wall, i, 2,
                                  timeResult);
          }
        }
      }

      /*
      *   Collision has been filled with the informations about the next event
      *   that will happen in the game
      */

      oneTurnFinished = (collision.time >= intervalEnd);

      if (!oneTurnFinished) {
        /*
        *  Apply the collision effect, and continue the calculation
        */
        tmin = collision.time - EPSILON;
        applyCollision(collision);
      }

      // if (nLoop > 20) {
      //   LOGA((int)collision.e1Type SPACE(int) collision.e2Type SPACE
      //            collision.e1Id SPACE collision.e2Id SPACE collision.time);
      // }
      assert(nLoop < 40);
      if (nLoop > 35) {
        break;
      }
    }
  }

  void applyCollision(const Collision &collision) {
    LOGD("COLLISION : " SPACE(int) collision.e1Type SPACE(int)
             collision.e2Type SPACE collision.e1Id SPACE
                 collision.e2Id SPACE collision.time);
    switch (collision.e1Type) {
      case EntityType::Wiz:
        switch (collision.e2Type) {
          case EntityType::Wiz:

            bounce(wiz[collision.e1Id], wiz[collision.e2Id], collision.time);

            break;
          case EntityType::Snaffle:
            pickUpSnaffle(collision.e1Id, collision.e2Id);
            break;
          case EntityType::Budger:
            bounce(wiz[collision.e1Id], budgers[collision.e2Id],
                   collision.time);
            budgers[collision.e2Id].lastWizId = collision.e1Id;
            break;
          case EntityType::Pole:
            bounce(wiz[collision.e1Id], MapData::poles[collision.e2Id],
                   collision.time);
            break;
          case EntityType::Wall:
            bounce(wiz[collision.e1Id], MapData::walls[collision.e2Id],
                   collision.time);
            break;
          default:
            assert(false);
        }
        break;
      case EntityType::Snaffle:
        switch (collision.e2Type) {
          case EntityType::Wiz:
            assert(false);
            break;
          case EntityType::Snaffle:
            bounce(snaffles[collision.e1Id], snaffles[collision.e2Id],
                   collision.time);
            break;
          case EntityType::Budger:
            bounce(snaffles[collision.e1Id], budgers[collision.e2Id],
                   collision.time);
            break;
          case EntityType::Pole:
            bounce(snaffles[collision.e1Id], MapData::poles[collision.e2Id],
                   collision.time);
            break;
          case EntityType::Wall:
            updatePosition(snaffles[collision.e1Id], collision.time);
            if (snaffles[collision.e1Id].pos.y < HIGHGOALH &&
                snaffles[collision.e1Id].pos.y > LOWGOALH) {
              if (MapData::walls[collision.e2Id].wallType == WallType::Left) {
                // Team 1 wins one point
                ++teamScore[1];
                snaffles[collision.e1Id].pos = Wiz::GOALCENTER[1];
                if (2 * teamScore[1] > nSnaffles && !gameFinished) {
                  // Team 1 wins the game
                  gameFinished = true;
                  turnGameFinished = gameTurn;
                  winningTeam = 1;
                }
              } else {
                // Team 0 wins one point
                ++teamScore[0];
                snaffles[collision.e1Id].pos = Wiz::GOALCENTER[0];
                if (2 * teamScore[0] > nSnaffles && !gameFinished) {
                  // Team 0 wins the game
                  gameFinished = true;
                  turnGameFinished = gameTurn;
                  winningTeam = 0;
                }
              }
              removeSnaffle(snaffles[collision.e1Id]);
            } else {
              bounce(snaffles[collision.e1Id], MapData::walls[collision.e2Id],
                     collision.time);
            }
            break;
          default:
            assert(false);
        }
        break;
      case EntityType::Budger:
        switch (collision.e2Type) {
          case EntityType::Wiz:
            assert(false);
            break;
          case EntityType::Snaffle:
            assert(false);
            break;
          case EntityType::Budger:
            bounce(budgers[collision.e1Id], budgers[collision.e2Id],
                   collision.time);
            break;
          case EntityType::Pole:
            bounce(budgers[collision.e1Id], MapData::poles[collision.e2Id],
                   collision.time);
            break;
          case EntityType::Wall:
            bounce(budgers[collision.e1Id], MapData::walls[collision.e2Id],
                   collision.time);
            break;
          default:
            assert(false);
        }
        break;
      case EntityType::Pole:
        assert(false);
        break;
      case EntityType::Wall:
        assert(false);
        break;
      default:
        assert(false);
    }
  }

  INLINE void applyBudgerActions() {
    for (int i = 0; i < 2; ++i) {
      int closestWizard = -1;
      double mindSq = BIGDOUBLE;
      for (int j = 0; j < 4; ++j) {
        if (wiz[j].noAggroBudger() || j == budgers[i].lastWizId) {
          // TODO implement the lastWizId calculation
          continue;
        }
        double dSq = normSq(wiz[j].pos - budgers[i].pos);
        if (dSq < mindSq) {
          closestWizard = j;
          mindSq = dSq;
        }
      }

      if (closestWizard != -1) {
        vec2 target = wiz[closestWizard].pos - budgers[i].pos;
        target.normalize();
        budgers[i].speed += (1000.0 * Budger::INVERSEMASS) * target;
      }
      // else {
      //   LOGD("BUDGER NO TARGET");
      // }
    }
  }

  INLINE void applySpellActions() {
    /*
    *   PETRIFICUS
    */

    for (int i = 0; i < 4; ++i) {
      if (wiz[i].petrificusTarget > -1) {
        if (wiz[i].petrificusTarget < 2) {
          // Target the opponent wizards
          int oppWizId = 2 * (1 - wiz[i].teamId) + wiz[i].petrificusTarget;
          assert(oppWizId >= 0 && oppWizId < 4 && (oppWizId / 2) != (i / 2));
          wiz[oppWizId].speed = vec2(0.0, 0.0);
          spellCost[wiz[i].teamId] += 5500.0;
          spellCost[wiz[i].teamId] += 300.0 * gameTurn;
          assert(isNotNan(wiz[oppWizId].speed));
        } else {
          // Target the snaffle
          int snaffleId = wiz[i].petrificusTarget - 2;
          if (!snaffles[snaffleId].isAlive) {
            spellCost[wiz[i].teamId] += BIGDOUBLE;
          } else {
            if (snaffles[snaffleId].wasGrabbed &&
                snaffles[snaffleId].grabberId / 2 != wiz[i].teamId) {
              // MagicShot
              spellCost[wiz[i].teamId] += 1800.0;
              spellCost[wiz[i].teamId] += 300.0 * gameTurn;
            } else {
              spellCost[wiz[i].teamId] += 4000.0;
              spellCost[wiz[i].teamId] += 300.0 * gameTurn;
            }
            snaffles[snaffleId].speed = vec2(0.0, 0.0);
          }
        }
        wiz[i].petrificusTarget = -1;
      } else if (wiz[i].petrificusTarget < -1000) {
        wiz[i].petrificusTarget += 2000;
      }
    }

    for (int i = 0; i < 4; ++i) {
      /*
      *   ACCIO
      */

      if (wiz[i].accioCd > 0) {
        // Target the snaffle
        int snaffleId = wiz[i].accioTarget;
        if (wiz[i].accioCd == 7) {
          if (!snaffles[snaffleId].isAlive) {
            spellCost[wiz[i].teamId] += BIGDOUBLE;
          }
        } else {
          vec2 target = wiz[i].pos - snaffles[snaffleId].pos;
          double dSq = normSq(target);
          if (dSq > 10.0) {
            double d = sqrt(dSq);
            snaffles[snaffleId].speed +=
                MIN((3000.0 * 1000.0 * 1000.0) / dSq, 1000.0) *
                (Snaffle::INVERSEMASS / d) * target;
          }
        }
        wiz[i].accioCd -= 1;
      }

      /*
      *   FLIPENDO
      */

      if (wiz[i].flipendoCd > 0) {
        if (wiz[i].flipendoTarget < 2) {
          // Target the opponent wizard
          int oppWizId = 2 * (1 - wiz[i].teamId) + wiz[i].flipendoTarget;
          if (wiz[i].flipendoCd < 4) {
            vec2 target = wiz[oppWizId].pos - wiz[i].pos;
            assert(i != oppWizId);
            double dSq = normSq(target);
            assert(dSq > 10.0);
            double d = sqrt(dSq);
            wiz[oppWizId].speed +=
                MIN((6000.0 * 1000.0 * 1000.0) / dSq, 1000.0) *
                (Wiz::INVERSEMASS / d) * target;
            assert(isNotNan(wiz[oppWizId].speed));
          }
        } else {
          // Target the snaffle
          int snaffleId = wiz[i].flipendoTarget - 2;
          if (wiz[i].flipendoCd == 4) {
            if (!snaffles[snaffleId].isAlive) {
              spellCost[wiz[i].teamId] += BIGDOUBLE;
            }
          } else {
            vec2 target = snaffles[snaffleId].pos - wiz[i].pos;
            double dSq = normSq(target);
            if (dSq > 10.0) {
              double d = sqrt(dSq);
              snaffles[snaffleId].speed +=
                  (MIN((6000.0 * 1000.0 * 1000.0) / dSq, 1000.0) *
                   Snaffle::INVERSEMASS / d) *
                  target;
              assert(isNotNan(snaffles[snaffleId].speed));
            }
          }
        }
        wiz[i].flipendoCd -= 1;
      }
    }
  }

  INLINE void removeSnaffle(Snaffle &snaffle) { snaffle.isAlive = false; }

  INLINE void pickUpSnaffle(int wId, int sId) {
    LOGD("PICKUP" SPACE wId SPACE sId);
    Wiz &wizard = wiz[wId];
    Snaffle &snaffle = snaffles[sId];
    assert(snaffle.isAlive);
    wizard.pickCooldown = Wiz::COOLDOWN;
    wizard.accioCd = 0;
    wizard.flipendoCd = 0;
    wizard.pickedSnaffleId = sId;
    wizard.carriesSnaffle = true;
    snaffle.isGrabbed = true;
    snaffle.grabberId = wId;
  }

  INLINE void releaseSnaffle(int wId, int sId) {
    Wiz &wizard = wiz[wId];
    Snaffle &snaffle = snaffles[sId];
    assert(snaffle.isAlive && wizard.carriesSnaffle &&
           wizard.pickedSnaffleId == sId && snaffle.isGrabbed &&
           snaffle.grabberId == wId);
    wizard.carriesSnaffle = false;
    wizard.pickedSnaffleId = -1;
    snaffle.isGrabbed = false;
    snaffle.wasGrabbed = true;
    snaffle.speed = wizard.speed;
    snaffle.pos = wizard.pos;
  }
};
class BB {
 public:
  static INLINE WizAction goTowardsSnaffle(const Game &game, int wId, int sId) {
    vec2 target = game.snaffles[sId].pos + 1.3*game.snaffles[sId].speed -
                  game.wiz[wId].pos - 1.3*game.wiz[wId].speed;
    target.normalize();
    return WizAction(ActionType::Move, Wiz::MOVETHRUST * target, sId);
  }
  static INLINE WizAction goTowardsClosestSnaffle(const Game &game, int wId) {
    int closestSnaffle = 0;
    double mindSq = BIGDOUBLE;
    for (int i = 0; i < game.nSnaffles; ++i) {
      if (!game.snaffles[i].isAlive) {
        continue;
      }
      double dSq = normSq(game.snaffles[i].pos + 1.3*game.snaffles[i].speed -
                          game.wiz[wId].pos - 1.3*game.wiz[wId].speed);
      if (dSq < mindSq) {
        closestSnaffle = i;
        mindSq = dSq;
      }
    }
    return goTowardsSnaffle(game, wId, closestSnaffle);
  }
  static INLINE WizAction throwSnaffleTowardsGoal(const Game &game, int wId) {
    // return WizAction(ActionType::Move, 10.0 * LUT::vec2_LUT[2], 0);
    assert(game.wiz[wId].pickCooldown == Wiz::COOLDOWN - 1 &&
           game.wiz[wId].hasSnaffle());
    vec2 target = Wiz::GOALCENTER[game.wiz[wId].teamId] - game.wiz[wId].pos;
    target.normalize();
    return WizAction(ActionType::Throw, Wiz::THROWTHRUST * target,
                     game.wiz[wId].pickedSnaffleId);
  }
  static INLINE WizAction dummyWizAction(const Game &game, int wId) {
    if (game.wiz[wId].hasSnaffle()) {
      return throwSnaffleTowardsGoal(game, wId);
    } else {
      return goTowardsClosestSnaffle(game, wId);
    }
  }
};

class DummyBot {
 public:
  GameAction getNextAction(const Game &game, int pId) {
    GameAction gameAction;
    assert(pId == 0 || pId == 1);
    gameAction.wizActions[0] = BB::dummyWizAction(game, 2 * pId);
    gameAction.wizActions[1] = BB::dummyWizAction(game, 2 * pId + 1);
    return gameAction;
  }
};
DummyBot _dummyBot;

class FBEvaluator {
 public:
  int populationSize;
  int genomeSize;

  Game referenceGame, simulGame;
  int simulCounter = 0;

  int teamId;

  std::pair<double, int> posValues[7];
  int nPosValues;

  std::pair<double, int> d0me[7];
  std::pair<double, int> d1me[7];
  std::pair<double, int> d0opp[7];
  std::pair<double, int> d1opp[7];

  FBEvaluator(){};
  FBEvaluator(Game referenceGamee, int populationSizee, int genomeSizee)
      : populationSize(populationSizee),
        genomeSize(genomeSizee),
        referenceGame(referenceGamee) {}

  void evaluateGenome(Genome &genome, Fitness &fitness) {
    simulGame = referenceGame;
    _fitness.init();

    /*
    * Play the simulation
    */
    for (int i = 0; i < GA_HORIZON; ++i) {
      GameAction gameAction = getGenomeGameAction(genome, simulGame, teamId);
      simulGame.applyGameAction(gameAction, teamId);
      assert(teamId == 0 || teamId == 1);
      GameAction gameActionOpp = _dummyBot.getNextAction(simulGame, 1 - teamId);
       simulGame.applyGameAction(gameActionOpp, 1 - teamId);
      simulGame.playOneTurn();
      evaluatePosition(simulGame, 0.07);
      _fitness.snafPosCrit += 20.0 * (simulGame.teamScore[teamId] -
                                      simulGame.teamScore[1 - teamId]);
    }
    for (int i = 0; i < DUMMY_HORIZON; ++i) {
      GameAction gameAction = _dummyBot.getNextAction(simulGame, teamId);
      simulGame.applyGameAction(gameAction, teamId);
      GameAction gameActionOpp = _dummyBot.getNextAction(simulGame, 1 - teamId);
      simulGame.applyGameAction(gameActionOpp, 1 - teamId);
      simulGame.playOneTurn();
      _fitness.snafPosCrit += 20.0 * (simulGame.teamScore[teamId] -
                                      simulGame.teamScore[1 - teamId]);
    }

    /*
    * Evaluate the game
    */

    _fitness.gameWon =
        simulGame.gameFinished && simulGame.winningTeam == teamId;
    _fitness.gameLost =
        simulGame.gameFinished && simulGame.winningTeam != teamId;
    _fitness.roundGameFinished = simulGame.turnGameFinished;

    evaluatePosition(simulGame, 1.0);

    /*
    *   Bonus snaffles marqués
    */
    _fitness.snafPosCrit += simulGame.teamScore[teamId] * 2.0 * MAPW;
    _fitness.snafPosCrit -= simulGame.teamScore[1 - teamId] * 2.0 * MAPW;

    /*
    *   MAGIE !
    */
    // _fitness.snafPosCrit += simulGame.teamMana[teamId] * 100.0;
    // _fitness.snafPosCrit -= simulGame.teamMana[1 - teamId] * 100.0;
    _fitness.snafPosCrit -= 1.25*simulGame.spellCost[teamId];

    /*
    *   Compensation position snaffles pour les snaffles portés
    */
    for (int i = 0; i < 4; ++i) {
      if (simulGame.wiz[i].hasSnaffle()) {
        if (simulGame.wiz[i].teamId == teamId) {
          _fitness.snafPosCrit += 500.0;
        } else {
          _fitness.snafPosCrit -= 500.0;
        }
      }
    }

    /*
    *   WizPosCrit
    */
    int index = 0;
    for (int i = 0; i < nPosValues; ++i) {
      Snaffle &snaffle = simulGame.snaffles[posValues[i].second];
      if (snaffle.isAlive) {
        d0me[index].first = norm(snaffle.pos - simulGame.wiz[2 * teamId].pos);
        d0me[index].second = i;

        d1me[index].first =
            norm(snaffle.pos - simulGame.wiz[2 * teamId + 1].pos);
        d1me[index].second = i;
        ++index;
      }
    }
    assert(index ==
           simulGame.nSnaffles - simulGame.teamScore[0] -
               simulGame.teamScore[1]);
    std::nth_element(d0me, d0me + 1, d0me + index);
    std::nth_element(d1me, d1me + 1, d1me + index);
    if (d0me[0].second != d1me[0].second || index < 2) {
      _fitness.wizPosCrit -= d0me[0].first;
      _fitness.wizPosCrit -= d1me[0].first;
    } else {
      _fitness.wizPosCrit -=
          MIN(d0me[0].first + d1me[1].first, d0me[1].first + d1me[0].first);
    }
    _fitness.snafPosCrit += 0.08 * _fitness.wizPosCrit;
    /*
    *   Eloigner l'adversaire des snaffles
    */
    index = 0;
    for (int i = 0; i < nPosValues; ++i) {
      Snaffle &snaffle = simulGame.snaffles[posValues[i].second];
      if (snaffle.isAlive) {
        d0opp[index].first =
            norm(snaffle.pos - simulGame.wiz[2 * (1 - teamId)].pos);
        d0opp[index].second = i;

        d1opp[index].first =
            norm(snaffle.pos - simulGame.wiz[2 * (1 - teamId) + 1].pos);
        d1opp[index].second = i;
        ++index;
      }
    }
    assert(index ==
           simulGame.nSnaffles - simulGame.teamScore[0] -
               simulGame.teamScore[1]);
    std::nth_element(d0opp, d0opp + 1, d0opp + index);
    std::nth_element(d1opp, d1opp + 1, d1opp + index);
    if (d0opp[0].second != d1opp[0].second || index < 2) {
      _fitness.snafPosCrit += 0.01 * (d0opp[0].first + d1opp[0].first);
    } else {
      _fitness.snafPosCrit += 0.01 * MIN(d0opp[0].first + d1opp[1].first,
                                         d0opp[1].first + d1opp[0].first);
    }

    for (int i = 0; i < nPosValues; ++i) {
      Snaffle &snaffle = simulGame.snaffles[posValues[i].second];
      if (snaffle.isAlive) {
        _fitness.snafPosCrit -=
            0.5 * CLAMP(MIN(d0me[i].first, d1me[i].first) -
                            MIN(d0opp[i].first, d1opp[i].first),
                        -600.0, 600.0);
      }
    }

    fitness = _fitness;
  }

  void evaluatePosition(const Game &simulGame, double epsilon) {
    /*
    *   Position des snaffles
    *   Précalcul des posValues
    */
    nPosValues = 0;
    for (int i = 0; i < simulGame.nSnaffles; ++i) {
      if (simulGame.snaffles[i].isAlive) {
        double posValue = snafflePosScore(simulGame.snaffles[i]);
        posValues[nPosValues].first = posValue;
        posValues[nPosValues].second = i;
        ++nPosValues;
        _fitness.snafPosCrit += epsilon * MAPW * posValue;
        vec2 driftPos =
            simulGame.snaffles[i].pos + simulGame.snaffles[i].speed * 3.5;
        if (((teamId == 0 && driftPos.x > MAPW) ||
             (teamId == 1 && driftPos.x < 0.0)) &&
            MapData::goals[teamId].intersect2(simulGame.snaffles[i].pos,
                                              driftPos)) {
          // If the snaffle continues without being picked up, it will enter the
          // good goal
          _fitness.snafPosCrit += epsilon * 5000.0;
          _fitness.snafPosCrit +=
              epsilon * fabs(driftPos.x - MapData::goals[teamId].start.x);
        } else if (((teamId == 0 && driftPos.x < 0.0) ||
                    (teamId == 1 && driftPos.x > MAPW)) &&
                   MapData::goals[1 - teamId].intersect2(
                       simulGame.snaffles[i].pos, driftPos)) {
          // If the snaffle continues without being picked up, it will enter the
          // bad goal
          _fitness.snafPosCrit -= epsilon * 5000.0;
          _fitness.snafPosCrit -=
              epsilon * fabs(driftPos.x - MapData::goals[1 - teamId].start.x);
        }
      }
    }
    /*
    *   Heuristique "favoriser les snaffle clé"
    */
    assert(nPosValues == (simulGame.nSnaffles - simulGame.teamScore[0] -
                          simulGame.teamScore[1]));

    std::nth_element(posValues, posValues + (simulGame.nSnaffles) / 2 -
                                    simulGame.teamScore[1 - teamId],
                     posValues + nPosValues);
    for (int i = 0;
         i < (simulGame.nSnaffles) / 2 - simulGame.teamScore[1 - teamId] + 1;
         ++i) {
      _fitness.snafPosCrit += epsilon * 0.4 * MAPW * posValues[i].first;
    }
    /*
    *   Compensation position snaffles marqués
    */
    _fitness.snafPosCrit += epsilon * simulGame.teamScore[teamId] * MAPW;
    _fitness.snafPosCrit += epsilon * simulGame.teamScore[1 - teamId] * 0.0;
  }

  INLINE double snafflePosScore(const Snaffle &snaffle) const {
    double x = ((teamId == 0) ? MAPW - snaffle.pos.x - snaffle.speed.x
                              : snaffle.pos.x + snaffle.speed.x);
    double y = snaffle.pos.y + snaffle.speed.y;
    return 0.5 - (x - MAPW / 2.0) * (1.0 / MAPW) +
           (x - MAPW / 2.0) * fabs(y - MAPH / 2.0) * (1.0 / (MAPW * MAPH));
    // 0.0 if the snaffle is near the
    // opponent's goal (bad), 1.0 if the
    // snaffle is near my goal (good)
  }

  static INLINE GameAction getGenomeGameAction(Genome &genome, const Game &game,
                                               int pId) {
    return GameAction(FBEvaluator::getGenomeWizAction(genome, game, pId, 0),
                      FBEvaluator::getGenomeWizAction(genome, game, pId, 1));
  };

  static INLINE WizAction getGenomeWizAction(Genome &genome, const Game &game,
                                             int pId, int wIdInTeam) {
    int magicGene = genome.genes[GA_HORIZON + wIdInTeam * (1 + GA_HORIZON)];
    int gene = genome.genes[game.gameTurn + wIdInTeam * (1 + GA_HORIZON)];
    int wId = 2 * pId + wIdInTeam;
    /*
    * Spell
    */
    if (((magicGene & 0xFF) % 16) == game.gameTurn && game.gameTurn < 3) {
      // Try to launch a spell
      int spellCode = (magicGene >> 8) & 0xFF;
      if (spellCode < 128 && game.teamMana[pId] >= 20) {
        // Flipendo
        int targetId = ((magicGene >> 16) & 0xFF) % (2 + game.nSnaffles);
        return WizAction(ActionType::Flip, vec2(1.0, 0.0), targetId);
      } else if (spellCode < 192 && game.teamMana[pId] >= 20) {
        // Accio
        int targetId = ((magicGene >> 16) & 0xFF) % (game.nSnaffles);
        return WizAction(ActionType::Accio, vec2(1.0, 0.0), targetId);
      } else if (spellCode < 256 && game.teamMana[pId] >= 10) {
        // Petrificus
        int targetId = ((magicGene >> 16) & 0xFF) % (2 + game.nSnaffles);
        return WizAction(ActionType::Petrif, vec2(1.0, 0.0), targetId);
      } else if (false) {
        // Obliviate
        assert(false);
        return WizAction(ActionType::Obliv, vec2(1.0, 0.0), 0);
      }
    }
    /*
    * Not a spell
    */
    if (((gene >> 24) & 0xFF) < 32) {
      // 2 chances out of 16 to use the basic bot
      return BB::dummyWizAction(game, wId);
    } else if (game.wiz[wId].hasSnaffle() && ((gene >> 16) & 0xFF) < 200) {
      // Quand on a le snaffle, 4 chances sur 5 de THROW
      if (((gene >> 16) & 0xFF) < 150) {
        // Privilégier devant
        int throwAngle = (gene >> 8) & 0x7F;  //[0, 128[
        if (pId == 0) {
          throwAngle += 64;
        } else {
          throwAngle -= 64;
        }
        if (throwAngle > 255) {
          throwAngle -= 256;
        } else if (throwAngle < 0) {
          throwAngle += 256;
        }
        return WizAction(ActionType::Throw,
                         Wiz::THROWTHRUST * LUT::vec2_LUT[throwAngle], 0);

      } else {
        // N'importe quelle direction
        int throwAngle = (gene >> 8) & 0xFF;
        return WizAction(ActionType::Throw,
                         Wiz::THROWTHRUST * LUT::vec2_LUT[throwAngle], 0);
      }
    } else {
      int moveAngle = (gene)&0xFF;
      return WizAction(ActionType::Move,
                       Wiz::MOVETHRUST * LUT::vec2_LUT[moveAngle], 0);
    }
  }

  void evaluatePopulation(Population &population,
                          std::vector<Fitness> &fitness) {
    for (int i = 0; i < populationSize; ++i) {
      evaluateGenome(population.genomes[i], fitness[i]);
    }
  }
};

class MetaBot {
  Genome savedGenome;
  bool useSavedGenome = false;

 public:
  GameAction getNextAction(const Game &game, int pId) {
    /*
    *   Create the genetic algorithm
    */
    GeneticAlgorithm<Fitness, FBEvaluator, GeneticEvolverBasic> genAlgo(
        GA_POPSIZE, GA_GENOMESIZE, 2, false, true);  // Verbose, timebased
    FBEvaluator &evaluator = genAlgo.entityEvaluator;
    evaluator.populationSize = GA_POPSIZE;
    evaluator.genomeSize = GA_GENOMESIZE;
    evaluator.referenceGame = game;
    evaluator.teamId = pId;
    genAlgo.setControls(92000, 92000);

    /*
    *   Initial game analysis
    */
    _nReplaceOrder = 0;
    for (int i = 0; i < game.nSnaffles; ++i) {
      if (game.snaffles[i].isAlive) {
        _replaceOrder[_nReplaceOrder].first =
            evaluator.snafflePosScore(game.snaffles[i]);
        _replaceOrder[_nReplaceOrder].second = i;
        ++_nReplaceOrder;
      }
    }
    std::sort(_replaceOrder, _replaceOrder + _nReplaceOrder,
              [](std::pair<double, int> a, std::pair<double, int> b) {
                return a.first < b.first;
              });
    for (int i = 0; i < _nReplaceOrder; ++i) {
      LOGA(i SPACE 4 + _replaceOrder[i].second SPACE _replaceOrder[i].first);
    }

    if (useSavedGenome) {
      genomeShift(savedGenome, genAlgo.population[0].genomes[0]);
    }

    // Seed with basic bot genome
    makeBasicBot(genAlgo.population[0].genomes[2]);
    /*
    *   Launch the genetic algorithm
    */
    genAlgo.doOptimize();
    Genome bestGenome = genAlgo.getBestGenome();
    savedGenome = bestGenome;
    useSavedGenome = true;
    return FBEvaluator::getGenomeGameAction(bestGenome, game, pId);
  }
};
MetaBot _metaBot;

/******************************************************************************
*
*     CLASS ENVIRONMENT
*
* Holds the input/output logic as well as the main Bot used for the game
*
*******************************************************************************/
