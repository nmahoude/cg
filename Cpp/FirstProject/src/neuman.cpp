/**
 *  COPY-PASTER PROOFED !
 *    several typos added on purpose ...
 */

#pragma GCC optimize("-O3")
#pragma GCC optimize("inline")
#pragma GCC optimize("omit-frame-pointer")

#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <algorithm>
#include <array>
#include <sys/time.h>

#define MIN(a, b) (((a) < (b)) ? (a) : (b))
#define MAX(a, b) (((a) > (b)) ? (a) : (b))
#define RANDOM_NUM ((float)rand() / (RAND_MAX + 1))

using namespace std;

// *** DECLARATION ***
class Entity;
class Snaffle;
class Wizzard;
class Bludger;
class Chromosome;
class GameState;
class Collision;

// *** CONSTANTS ***
const int GENETIC_DEPTH = 4;
const int GENETIC_POPSIZE = 64;
const double CROSSOVER_RATE = 0.3;
const double MUTATION_RATE = 0.001;
const int GENETIC_TN_SIZE = 3;

const float PI = 3.1415926535897932384626434;
const int WIDTH = 16001;
const int HEIGHT = 7501;
const int SNAFFLECD = 3;

const int TYPE_WIZZARD = 0;
const int TYPE_SNAFFLE = 1;
const int TYPE_BLUDGER = 2;
const int TYPE_POLE = 3;

const int TARGET_GOAL_CENTER[2] = { 16000, 0 };
const int SPELL_COST[4] = { 5, 10, 20, 20 };
const int SPELL_DURATION[4] = { 3, 1, 6, 3 };
const string SPELL_NAME[4] = { "OBLIVIATE", "PETRIFICUS", "ACCIO", "FLIPENDO" };

// *** GLOBAL ***
struct timeval timestart, timeend;
double COS[361];
double SIN[361];
int turn;
int myTeamId;
int hisTeamId;

int SNAFFLE_START;
int SNAFFLE_END;
int WIZZARD_START;
int WIZZARD_END;
int BLUDGER_START;
int BLUDGER_END;

int snaffleNumber;
int entitiesNumber;
int winningScore;

GameState* mainState;
Chromosome** pop = new Chromosome* [GENETIC_POPSIZE];
Chromosome** oldPop = new Chromosome* [GENETIC_POPSIZE];

Collision* collisionCache[200]; // TODO baisser
int collisionCacheE;

int TOTAL = 0;

Entity* poles[4];

// ***********************************************************

float elapsedMS()
{
    gettimeofday(&timeend, NULL);
    return ((timeend.tv_sec - timestart.tv_sec) * 1000 + (timeend.tv_usec - timestart.tv_usec) / 1000.0) + 0.5;
}

uint32_t xorshift128()
{
    static uint32_t x = 7878, y = 2, z = 3, w = 4;
    uint32_t t = x;
    t ^= t << 11;
    t ^= t >> 8;
    x = y;
    y = z;
    z = w;
    w ^= w >> 19;
    w ^= t;
    return w;
}

inline int randShort(int a, int b)
{
    return xorshift128() % ((b + 1) - a) + a;
}

float degToRad(float x)
{
    return x / 180 * PI;
}

float radToDeg(float x)
{
    return x / PI * 180;
}

double precos(int angle)
{
    while (angle < 0) {
        angle += 360;
    }
    while (angle > 360) {
        angle -= 360;
    }
    return COS[angle];
}

double presin(int angle)
{
    while (angle < 0) {
        angle += 360;
    }
    while (angle > 360) {
        angle -= 360;
    }
    return SIN[angle];
}

void precalculateCosSin()
{
    for (int i = 0; i <= 360; i++) {
        double rad = degToRad((float)i);
        COS[i] = cos(rad);
        SIN[i] = sin(rad);
    }
}

// ***********************************************************

class Collision {
public:
    int e1;
    int e2;
    double t;
    int type;

    Collision() {}
};

// ***********************************************************

class Spell {
public:
    int to;
    int duration;

    Spell()
    {
        this->to = -1;
        this->duration = -1;
    }

    void copy(Spell* other)
    {
        this->to = other->to;
        this->duration = other->duration;
    }
};

// ***********************************************************

class Entity {
public:
    int id, radius, type;
    double mass;
    double x, y, vx, vy;
    bool alive;

    Entity()
    {
        this->type = -1;
        this->alive = false;
    }

    virtual void copy(Entity* other)
    {
        this->id = other->id;
        this->radius = other->radius;
        this->type = other->type;
        this->id = other->id;
        this->mass = other->mass;
        this->x = other->x;
        this->y = other->y;
        this->vx = other->vx;
        this->vy = other->vy;
        this->alive = other->alive;
    }

    virtual void print()
    {
        cerr << "NOT IMPLEMENTED" << endl;
    }

    virtual inline bool canCollideWith(Entity* other)
    {
        return false;
    }

    inline int squaredDistanceToGoal(int teamId)
    {
        int ytar = MAX(MIN(y, HEIGHT - 2500), 2500);
        return (x - TARGET_GOAL_CENTER[teamId]) * (x - TARGET_GOAL_CENTER[teamId]) + (y - ytar) * (y - ytar);
    }

    inline int squaredDistanceToEntity(Entity* other)
    {
        return (x - other->x) * (x - other->x) + (y - other->y) * (y - other->y);
    }

    inline void moveDuring(double t)
    {
        x += vx * t;
        y += vy * t;
    }

    void collision(Collision* col, double from) // Collision avec mur
    {
        double coltH = 99999;
        double coltV = 99999;

        if (vx > 0) { // mur droit
            coltH = ((WIDTH - 1 - x) - (type == TYPE_SNAFFLE ? 0 : radius)) / vx;
        }
        else if (vx < 0) { // mur gauche
            coltH = -(x - (type == TYPE_SNAFFLE ? 0 : radius)) / vx;
        }

        if (vy > 0) { // mur bas
            coltV = ((HEIGHT - 1 - y) - radius) / vy;
        }
        else if (vy < 0) { // mur haut
            coltV = -(y - radius) / vy;
        }

        col->e1 = id;
        col->e2 = -1;
        col->t = 99999;
        if (coltH < coltV) {
            col->type = 10;
            col->t = from + coltH;
        }
        else if (coltH > coltV) {
            col->type = 11;
            col->t = from + coltV;
        }
    }

    void collision(Collision* col, Entity* other, double from) // Collision avec entité
    {
        col->e1 = id;
        col->e2 = other->id;

        double radiusAlpha;

        if (type == TYPE_WIZZARD && other->type == TYPE_SNAFFLE) {
            col->type = 0;
            col->t = from;
            if ((other->x - x) * (other->x - x) + (other->y - y) * (other->y - y) < 159201) {
                return;
            }
            radiusAlpha = 159201;
        }
        else {
            col->t = from;
            col->type = 1;
            radiusAlpha = (radius + other->radius) * (radius + other->radius);
        }

        double a, b, c, d;

        a = (vx - other->vx) * (vx - other->vx) + (vy - other->vy) * (vy - other->vy);
        b = 2 * (x * vx - other->x * vx - x * other->vx + other->x * other->vx + y * vy - other->y * vy - y * other->vy + other->y * other->vy);
        c = (other->x - x) * (other->x - x) + (other->y - y) * (other->y - y) - radiusAlpha;
        d = b * b - 4 * a * c;

        if (a == 0 || d < 0) {
            col->t += 999999;
        }
        else {
            double t = (-b - sqrt(d)) / (2 * a);
            col->t += t > 0 ? t : 999999;
        }
    }

    double pickTime(Entity* other)
    {
        if ((other->x - x) * (other->x - x) + (other->y - y) * (other->y - y) < 160000) {
            return 0;
        }

        double a, b, c, d;

        a = (vx - other->vx) * (vx - other->vx) + (vy - other->vy) * (vy - other->vy);
        b = 2 * (x * vx - other->x * vx - x * other->vx + other->x * other->vx + y * vy - other->y * vy - y * other->vy + other->y * other->vy);
        c = (other->x - x) * (other->x - x) + (other->y - y) * (other->y - y) - 160000;
        d = b * b - 4 * a * c;

        if (a == 0 || d < 0) {
            return 999999;
        }
        else {
            double t = (-b - sqrt(d)) / (2 * a);
            return t > 0 ? t : 999999;
        }
    }

    double collisionTime(Entity* other)
    {
        double a, b, c, d;

        a = (vx - other->vx) * (vx - other->vx) + (vy - other->vy) * (vy - other->vy);
        b = 2 * (x * vx - other->x * vx - x * other->vx + other->x * other->vx + y * vy - other->y * vy - y * other->vy + other->y * other->vy);
        c = (other->x - x) * (other->x - x) + (other->y - y) * (other->y - y) - (radius + other->radius) * (radius + other->radius);
        d = b * b - 4 * a * c;

        if (a == 0 || d < 0) {
            return 999999;
        }
        else {
            double t = (-b - sqrt(d)) / (2 * a);
            return t > 0 ? t : 999999;
        }
    }
};

// ***********************************************************

class Snaffle : public Entity {
public:
    bool carried;

    Snaffle()
    {
        this->type = TYPE_SNAFFLE;
        this->radius = 150;
        this->mass = 0.5;
        this->carried = false;
    }

    void copy(Entity* otherr)
    {
        Entity::copy(otherr);
        Snaffle* other = (Snaffle*)otherr;
        this->carried = other->carried;
    }

    inline bool canCollideWith(Entity* other)
    {
        return !carried;
    }

    void print()
    {
        cerr << "S[" << id << "]";
        if (alive) {
            cerr << x << ":" << y << " " << vx << ":" << vy;
            cerr << " " << (carried ? "C" : "");
        }
        else {
            cerr << "DEAD";
        }
        cerr << endl;
    }
};

// ***********************************************************

class Bludger : public Entity {
public:
    int lastCollidedWith;

    Bludger()
    {
        this->type = TYPE_BLUDGER;
        this->lastCollidedWith = -1;
        this->radius = 200;
        this->mass = 8;
        this->alive = true;
    }

    void copy(Entity* otherr)
    {
        Entity::copy(otherr);
        Bludger* other = (Bludger*)otherr;
        this->lastCollidedWith = other->lastCollidedWith;
    }

    inline bool canCollideWith(Entity* other)
    {
        return other->type != TYPE_SNAFFLE || !((Snaffle*)other)->carried;
    }

    void print()
    {
        cerr << "B[" << id << "]";
        cerr << x << ":" << y << " " << vx << ":" << vy;
        cerr << " -> " << lastCollidedWith;
        cerr << endl;
    }
};

// ***********************************************************

// 0 OBLIVIATE  1 PETRIFICUS  2 ACCIO  3 FLIPENDO
class Wizzard : public Entity {
public:
    int teamId, holding, snaffleId, snaffleCd;

    Wizzard()
    {
        this->type = TYPE_WIZZARD;
        this->radius = 400;
        this->mass = 1;
        this->holding = false;
        this->snaffleId = -1;
        this->snaffleCd = 0;
    }

    void copy(Entity* otherr)
    {
        Entity::copy(otherr);
        Wizzard* other = (Wizzard*)otherr;
        this->teamId = other->teamId;
        this->holding = other->holding;
        this->snaffleId = other->snaffleId;
        this->snaffleCd = other->snaffleCd;
    }

    inline bool canCollideWith(Entity* other)
    {
        return other->type != TYPE_SNAFFLE || snaffleCd == 0;
    }

    void print()
    {
        cerr << "W[" << id << "]" << x << ":" << y << " " << vx << ":" << vy;
        if (snaffleCd == 3)
            cerr << " carries " << snaffleId;
        cerr << endl;
    }
};

// ***********************************************************

class Chromosome {
public:
    /**
    *   99  DUMMY
    *   0   MOVE thrust angle
    *   1   MOVE thrust (closest snaffle)
    *   10   THROW (500 sur point plus proche de la ligne de but)
    *   11   THROW power angle
    *
    *             spells
    *   20   OBLIVIATE x
    *   21   PETRIFICUS x
    *   22   ACCIO x
    *   23   FLIPENDO x
    */
    int type[2][GENETIC_DEPTH];
    int a[2][GENETIC_DEPTH];
    int b[2][GENETIC_DEPTH];
    double score;
    // string DBG;

    Chromosome()
    {
        for (int i = 0; i < GENETIC_DEPTH; i++) {
            this->type[0][i] = 99;
            this->type[1][i] = 99;
            this->a[0][i] = 0;
            this->a[1][i] = 0;
            this->b[0][i] = 0;
            this->b[1][i] = 0;
        }
        this->score = 0;
    }

    void copy(Chromosome* other)
    {
        for (int i = 0; i < GENETIC_DEPTH; i++) {
            this->type[0][i] = other->type[0][i];
            this->type[1][i] = other->type[1][i];
            this->a[0][i] = other->a[0][i];
            this->a[1][i] = other->a[1][i];
            this->b[0][i] = other->b[0][i];
            this->b[1][i] = other->b[1][i];
        }
        this->score = other->score;
        // this->DBG = other->DBG;
    }

    void presimulationRandom()
    {
        for (int i = 0; i < GENETIC_DEPTH; i++) {
            randomMove(0, i, true);
            randomMove(1, i, true);
        }
    }

    void randomMove(int wid, int depth, bool canSpell)
    {
        if (!canSpell || randShort(0, 100) < 70) { // move
            type[wid][depth] = 0;
            a[wid][depth] = 150;
            b[wid][depth] = randShort(0, 17) * 20;
        }
        else { // spell
            type[wid][depth] = 20 + randShort(1, 3);
            a[wid][depth] = -1; // Target générer pendant le preturn
        }
    }

    void randomThrow(int wid, int depth)
    {
        short rnd = randShort(0, 100);
        if (rnd < 60) {
            type[wid][depth] = 10;
            a[wid][depth] = 500;
        }
        else {
            type[wid][depth] = 11;
            a[wid][depth] = 500;
            b[wid][depth] = randShort(0, 17) * 20;
        }
    }
};

// ***********************************************************

class GameState {
public:
    int score[2];
    int mana[2];
    Entity* entities[17];
    Spell* spells[16];

    GameState(int snaffleNumber)
    {
        score[0] = 0;
        score[1] = 0;
        mana[0] = 0;
        mana[1] = 0;
        for (int i = WIZZARD_START; i < WIZZARD_END; i++) {
            entities[i] = new Wizzard();
        }
        for (int i = SNAFFLE_START; i < SNAFFLE_END; i++) {
            entities[i] = new Snaffle();
        }
        for (int i = BLUDGER_START; i < BLUDGER_END; i++) {
            entities[i] = new Bludger();
        }
        for (int i = 0; i < 16; i++) {
            this->spells[i] = new Spell();
        }
        entities[13] = poles[0];
        entities[14] = poles[1];
        entities[15] = poles[2];
        entities[16] = poles[3];
    }

    void copy(GameState* other)
    {
        this->score[0] = other->score[0];
        this->score[1] = other->score[1];
        this->mana[0] = other->mana[0];
        this->mana[1] = other->mana[1];
        for (int i = 0; i < entitiesNumber; i++) {
            this->entities[i]->copy(other->entities[i]);
        }
        for (int i = 0; i < 16; i++) {
            this->spells[i]->copy(other->spells[i]);
        }
    }

    void print()
    {
        // cerr << "SCORE " << score[0] << ":" << score[1] << endl;
        // for (int i = 0; i < BLUDGER_END; i++)
        //     entities[i]->print();
        for (int i = 0; i < SNAFFLE_END; i++) {
            if (entities[i]->alive)
                entities[i]->print();
        }
    }

    double evaluate()
    {
        if (score[myTeamId] >= winningScore) {
            return 100000;
        }
        if (score[hisTeamId] >= winningScore) {
            return -100000;
        }

        double snaffleToGoal[2] = { 0, 0 };
        double teamToSnaffleSum[2] = { 0, 0 };
        double teamToClosestSnaffle[2] = { 0, 0 };
        double snaffleToWizzard[SNAFFLE_END][4];
        double dominatedSnaffles[2] = { 0, 0 };
        int aliveSnaffle = 0;

        double distance;

        for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {
            if (entities[sid]->alive) {
                aliveSnaffle++;
                snaffleToGoal[0] += sqrt(entities[sid]->squaredDistanceToGoal(0));
                snaffleToGoal[1] += sqrt(entities[sid]->squaredDistanceToGoal(1));

                int closestWid = -1;
                for (int wid = 0; wid < 4; wid++) {
                    distance = sqrt(entities[wid]->squaredDistanceToEntity(entities[sid]));
                    snaffleToWizzard[sid][wid] = distance;
                    if (closestWid == -1 || distance < snaffleToWizzard[sid][closestWid]) {
                        closestWid = wid;
                    }
                    teamToSnaffleSum[wid < 2 ? 0 : 1] += distance;
                }
                dominatedSnaffles[closestWid < 2 ? 0 : 1]++;
            }
            else {
                for (int wid = 0; wid < 4; wid++) {
                    snaffleToWizzard[sid][wid] = 99999999;
                }
            }
        }

        if (aliveSnaffle > 1) {
            // team 0
            int closestWid = -1;
            double smallest = 99999999;
            int closestSid = -1;
            for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {
                if (snaffleToWizzard[sid][0] < smallest) {
                    smallest = snaffleToWizzard[sid][0];
                    closestWid = 0;
                    closestSid = sid;
                }
                if (snaffleToWizzard[sid][1] < smallest) {
                    smallest = snaffleToWizzard[sid][1];
                    closestWid = 1;
                    closestSid = sid;
                }
            }
            teamToClosestSnaffle[0] += smallest;
            int secondWid = closestWid == 0 ? 1 : 0;

            smallest = 99999999;
            for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {

                if (sid == closestSid)
                    continue;

                if (snaffleToWizzard[sid][secondWid] < smallest) {
                    smallest = snaffleToWizzard[sid][secondWid];
                }
            }
            teamToClosestSnaffle[0] += smallest;

            // team 1
            closestWid = -1;
            smallest = 99999999;
            closestSid = -1;
            for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {
                if (snaffleToWizzard[sid][2] < smallest) {
                    smallest = snaffleToWizzard[sid][2];
                    closestWid = 2;
                    closestSid = sid;
                }
                if (snaffleToWizzard[sid][3] < smallest) {
                    smallest = snaffleToWizzard[sid][3];
                    closestWid = 3;
                    closestSid = sid;
                }
            }
            teamToClosestSnaffle[1] += smallest;
            secondWid = closestWid == 2 ? 3 : 2;

            smallest = 99999999;
            for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {

                if (sid == closestSid)
                    continue;

                if (snaffleToWizzard[sid][secondWid] < smallest) {
                    smallest = snaffleToWizzard[sid][secondWid];
                }
            }
            teamToClosestSnaffle[1] += smallest;
        }
        else {
            teamToClosestSnaffle[0] = teamToSnaffleSum[0];
            teamToClosestSnaffle[1] = teamToSnaffleSum[1];
        }

        double result = 0;

        // Position
        if (aliveSnaffle > 0) {
            result -= 1 * (snaffleToGoal[myTeamId] / aliveSnaffle);
            result += 0.5 * (snaffleToGoal[hisTeamId] / aliveSnaffle);

            result -= 0.1 * (teamToSnaffleSum[myTeamId] / aliveSnaffle);
            result += 0.05 * (teamToSnaffleSum[hisTeamId] / aliveSnaffle);

            result -= 0.1 * teamToClosestSnaffle[myTeamId];
            result += 0.05 * teamToClosestSnaffle[hisTeamId];

            if (dominatedSnaffles[hisTeamId] + score[hisTeamId] >= winningScore) {
                result -= 4000;
            }
        }

        // Mana cost
        result += 100 * (mana[myTeamId] - mainState->mana[myTeamId]);

        // Goals
        result += 10000 * (score[myTeamId] - mainState->score[myTeamId]);
        result -= 10000 * (score[hisTeamId] - mainState->score[hisTeamId]);

        return result;
    }

    void simulate(Chromosome* myChromosome, Chromosome* hisChromosome, int depth)
    {
        for (int turn = 0; turn < depth; turn++) {
            // Application des nouvelles vitesses en fonction des commandes
            preTurnUpdates(turn, myChromosome, hisChromosome);

            int cole1 = -1;
            int cole2 = -1;
            double colt = -1;
            int type = -1;
            Collision* next = NULL;
            Collision* current = NULL;

            double t = 0;
            while (t < 1) {

                next = NULL;
                collisionCacheE = 0;

                for (int eid1 = 0; eid1 < entitiesNumber; eid1++) {

                    if (!entities[eid1]->alive)
                        continue;

                    // walls
                    current = collisionCache[collisionCacheE++];
                    entities[eid1]->collision(current, t);
                    if (current->t < 1) {
                        if (next == NULL || current->t < next->t) {
                            next = current;
                        }
                    }

                    // poles
                    for (int eid2 = 13; eid2 < 17; eid2++) {
                        current = collisionCache[collisionCacheE++];
                        entities[eid1]->collision(current, entities[eid2], t);
                        if (current->t < 1) {
                            if (next == NULL || current->t < next->t) {
                                next = current;
                            }
                        }
                    }

                    // other entities
                    for (int eid2 = eid1 + 1; eid2 < entitiesNumber; eid2++) {

                        if (!entities[eid2]->alive || !entities[eid1]->canCollideWith(entities[eid2]))
                            continue;

                        current = collisionCache[collisionCacheE++];
                        entities[eid1]->collision(current, entities[eid2], t);
                        if (current->t < 1) {
                            if (next == NULL || current->t < next->t) {
                                next = current;
                            }
                        }
                    }
                }

                if (next != NULL) {

                    type = next->type;
                    cole1 = next->e1;
                    cole2 = next->e2;
                    colt = next->t;

                    // On vérifie si un but a été marqué
                    if (type == 10 && entities[cole1]->type == TYPE_SNAFFLE) {
                        int colY = entities[cole1]->y + entities[cole1]->vy * (colt - t);

                        if (colY > 2200 && colY < 5301) {
                            // GOAL
                            entities[cole1]->alive = false;
                            if (entities[cole1]->vx > 0) {
                                score[0]++;
                            }
                            else {
                                score[1]++;
                            }
                            continue;
                        }
                    }

                    // Entities are moved until the collision time
                    for (int wid = 0; wid < entitiesNumber; wid++) {
                        if (entities[wid]->alive) {
                            entities[wid]->moveDuring(colt - t);
                        }
                    }

                    // Bounce is computed and speeds are updated
                    if (type > 9) {
                        if (type == 10) {
                            if (entities[cole1]->vx == 0) {
                            }
                            entities[cole1]->vx *= -1;
                        }
                        else {
                            if (entities[cole1]->vy == 0) {
                            }
                            entities[cole1]->vy *= -1;
                        }
                    }
                    else if (type == 0) {
                        picking(((Wizzard*)entities[cole1]), ((Snaffle*)entities[cole2]));
                    }
                    else {
                        computeBounce(entities[cole1], entities[cole2]);
                        if (entities[cole1]->type == TYPE_WIZZARD) {
                            if (entities[cole2]->type == TYPE_BLUDGER) {
                                ((Bludger*)entities[cole2])->lastCollidedWith = cole1;
                            }
                            if (((Wizzard*)entities[cole1])->snaffleId != -1) {
                                int snafId = ((Wizzard*)entities[cole1])->snaffleId;
                                entities[snafId]->vx = entities[cole1]->vx;
                                entities[snafId]->vy = entities[cole1]->vy;
                            }
                        }
                    }

                    t = colt;
                }
                else {
                    break;
                }
            }

            // Drones are moved until the end of the turn
            for (int wid = 0; wid < entitiesNumber; wid++) {
                if (entities[wid]->alive) {
                    entities[wid]->moveDuring(1 - t);
                }
            }

            // Trunc/round des vitesses et positions, application de la friction
            postTurnUpdates(turn);
        }
    }

    void computeBounce(Entity* e1, Entity* e2)
    {
        double e1mass = e1->mass;
        double e2mass = e2->mass;

        double nvx = e1->x - e2->x;
        double nvy = e1->y - e2->y;
        double length = sqrt(nvx * nvx + nvy * nvy);
        if (length != 0) {
            nvx /= length;
            nvy /= length;
        }
        double rvx = e1->vx - e2->vx;
        double rvy = e1->vy - e2->vy;
        double dp = nvx * rvx + nvy * rvy;
        double scaleFactor = dp / ((1 / e1mass) + (1 / e2mass));
        nvx *= scaleFactor;
        nvy *= scaleFactor;

        e1->vx -= nvx * (1 / e1mass);
        e1->vy -= nvy * (1 / e1mass);
        e2->vx += nvx * (1 / e2mass);
        e2->vy += nvy * (1 / e2mass);

        int imp = sqrt(nvx * nvx + nvy * nvy);
        if (imp < 100) {
            double scaleFactor = ((double)100) / imp;
            nvx *= scaleFactor;
            nvy *= scaleFactor;
        }

        e1->vx -= nvx * (1 / e1mass);
        e1->vy -= nvy * (1 / e1mass);
        e2->vx += nvx * (1 / e2mass);
        e2->vy += nvy * (1 / e2mass);
    }

    void picking(Wizzard* w, Snaffle* s)
    {
        w->snaffleId = s->id;
        w->snaffleCd = SNAFFLECD;
        w->holding = true;
        s->x = w->x;
        s->y = w->y;
        s->vx = w->vx;
        s->vy = w->vy;
        s->carried = true;

        // If snaffle is grabbed, ACCIO stops
        if (spells[8]->to == s->id) {
            spells[8]->duration = -1;
        }
        else if (spells[9]->to == s->id) {
            spells[9]->duration = -1;
        }
        else if (spells[10]->to == s->id) {
            spells[10]->duration = -1;
        }
        else if (spells[11]->to == s->id) {
            spells[11]->duration = -1;
        }
    }

    // TODO obliviate => bludger choisi sa cible => application du thrust / throw => petrificus => flipendo => accio => mouvements
    void preTurnUpdates(int depth, Chromosome* myChromosome, Chromosome* hisChromosome)
    {
        bool bludgerIgnoreTeam[2] = { false, false };

        bool extractAgain = false;
        Chromosome* currentChromosome;
        for (int wid = 0; wid < 4; wid++) {

            Wizzard* wizzard = (Wizzard*)entities[wid];

            // extract move data
            extractAgain = false;
            int ct, ca, cb;
            currentChromosome = (wizzard->teamId == myTeamId) ? myChromosome : hisChromosome;
            if (currentChromosome == NULL) {
                if (wizzard->snaffleId != -1) {
                    ct = 10;
                    ca = 500;
                }
                else {
                    ct = 1;
                    ca = 150;
                }
            }
            else {
                ct = currentChromosome->type[wid % 2][depth];
                ca = currentChromosome->a[wid % 2][depth];
                cb = currentChromosome->b[wid % 2][depth];
            }

            // ****** Gestion des coups impossibles/prioritaires *****
            // TODO voir en fonction eval pour le throw non systématique && randShort(0, 100) > 50
            if (wizzard->snaffleId != -1 && (ct < 10 || ct > 19)) { // Un MOVE ou un SPELL était programmé mais je carry un snaffle
                currentChromosome->randomThrow(wid % 2, depth);
                extractAgain = true;
            }
            else if (ct > 9 && ct < 20 && wizzard->snaffleId == -1) { // Un THROW était programmé mais je carry que dalle
                currentChromosome->randomMove(wid % 2, depth, false);
                extractAgain = true;
            }
            else if (ct > 19) { // Un SPELL est programmé, on vérifie qu'il est faisable
                if (mana[wizzard->teamId] < SPELL_COST[ct - 20]) { // Pas assez de mana
                    // On mets un MOVE à la place
                    currentChromosome->randomMove(wid % 2, depth, false);
                    extractAgain = true;
                }
                else {
                    mana[wizzard->teamId] -= SPELL_COST[ct - 20];

                    if (ca == -1 || !entities[ca]->alive) { // La target n'est pas legit, on la recompute
                        if (ct == 20) { // Obliviate
                            ca = randShort(BLUDGER_START, BLUDGER_END - 1);
                        }
                        else if (ct == 21) { // Petrificus
                            ca = randShort(SNAFFLE_START, SNAFFLE_END - 1);
                            if (!entities[ca]->alive) {
                                for (int i = 0; i < snaffleNumber; i++) {
                                    ca++;
                                    if (ca == SNAFFLE_END) {
                                        ca = SNAFFLE_START;
                                    }
                                    if (entities[ca]->alive) {
                                        break;
                                    }
                                }
                            }
                        }
                        else if (ct == 22) { // Accio
                            ca = randShort(SNAFFLE_START, SNAFFLE_END - 1);
                            if (!entities[ca]->alive) {
                                for (int i = 0; i < snaffleNumber; i++) {
                                    ca++;
                                    if (ca == SNAFFLE_END) {
                                        ca = SNAFFLE_START;
                                    }
                                    if (entities[ca]->alive) {
                                        break;
                                    }
                                }
                            }
                        }
                        else { // Flipendo
                            ca = randShort(SNAFFLE_START, SNAFFLE_END - 1);
                            if (!entities[ca]->alive) {
                                for (int i = 0; i < snaffleNumber; i++) {
                                    ca++;
                                    if (ca == SNAFFLE_END) {
                                        ca = SNAFFLE_START;
                                    }
                                    if (entities[ca]->alive) {
                                        break;
                                    }
                                }
                            }
                        }

                        // On écrase dans le chromosome
                        currentChromosome->a[wid % 2][depth] = ca;
                    }
                }
            }

            if (extractAgain) {
                ct = currentChromosome->type[wid % 2][depth];
                ca = currentChromosome->a[wid % 2][depth];
                cb = currentChromosome->b[wid % 2][depth];
            }
            // ****** FIN Gestion des coups impossibles/prioritaires *****

            // computes move influence
            if (ct == 0) {
                wizzard->vx += precos(cb) * ca;
                wizzard->vy += presin(cb) * ca;
            }
            else if (ct == 1) {
                int tx = -1;
                int ty = -1;
                int closestSnaffleId = -1;
                int closestSnaffleSqD = -1;
                for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {
                    if (entities[sid]->alive) {
                        int sqd = (entities[sid]->x - wizzard->x) * (entities[sid]->x - wizzard->x) + (entities[sid]->y - wizzard->y) * (entities[sid]->y - wizzard->y);
                        if (sqd > 0 && (closestSnaffleId == -1 || sqd < closestSnaffleSqD)) {
                            closestSnaffleSqD = sqd;
                            closestSnaffleId = sid;
                        }
                    }
                }

                if (closestSnaffleId != -1) {
                    tx = entities[closestSnaffleId]->x;
                    ty = entities[closestSnaffleId]->y;
                }
                else {
                    tx = WIDTH / 2;
                    ty = HEIGHT / 2;
                }

                double dx = tx - wizzard->x;
                double dy = ty - wizzard->y;
                double ratio = ca / sqrt(dx * dx + dy * dy);
                wizzard->vx += dx * ratio;
                wizzard->vy += dy * ratio;
            }
            else if (ct > 9 && ct < 20) { // THROW
                int tx, ty;
                if (ct == 10) {
                    tx = wid < 2 ? 16000 : 0;
                    ty = MAX(MIN(wizzard->y, HEIGHT - 3000), 3000);
                }
                else {
                    tx = wizzard->x + precos(cb) * 5000;
                    ty = wizzard->y + presin(cb) * 5000;
                }

                double dx = tx - wizzard->x;
                double dy = ty - wizzard->y;
                double ratio = (2 * ca / sqrt(dx * dx + dy * dy));

                entities[wizzard->snaffleId]->vx += dx * ratio;
                entities[wizzard->snaffleId]->vy += dy * ratio;
            }
            else {
                // Spells, will be applied later
            }

            // Release des snaffles
            if (wizzard->snaffleCd > 0) {
                wizzard->snaffleCd--;
            }
            if (wizzard->snaffleId != -1) {
                ((Snaffle*)entities[wizzard->snaffleId])->carried = false;
            }
            wizzard->snaffleId = -1;
            wizzard->holding = false;
        }

        // Bludgers chase
        for (int bid = BLUDGER_START; bid < BLUDGER_END; bid++) {
            int closestWizzard = -1;
            int closestWizzardSqD = -1;
            for (int wid = 0; wid < 4; wid++) {
                if ((wid < 2 && !bludgerIgnoreTeam[0] || wid > 1 && !bludgerIgnoreTeam[1]) && wid != ((Bludger*)entities[bid])->lastCollidedWith) {
                    int sqd = (entities[wid]->x - entities[bid]->x) * (entities[wid]->x - entities[bid]->x) + (entities[wid]->y - entities[bid]->y) * (entities[wid]->y - entities[bid]->y);
                    if (closestWizzard == -1 || sqd < closestWizzardSqD) {
                        closestWizzardSqD = sqd;
                        closestWizzard = wid;
                    }
                }
            }

            if (closestWizzard != -1) {
                int tx = entities[closestWizzard]->x;
                int ty = entities[closestWizzard]->y;
                double dx = tx - entities[bid]->x;
                double dy = ty - entities[bid]->y;
                double ratio = 125 / sqrt(dx * dx + dy * dy);
                entities[bid]->vx += dx * ratio;
                entities[bid]->vy += dy * ratio;
            }
        }

        // computes active spells influence
        for (int i = 0; i < 16; i++) {
            if (spells[i]->duration > 0) {
                int fromId = i % 4;
                int spellId = i / 4;
                int toId = spells[i]->to;
                if (entities[toId]->type == TYPE_SNAFFLE && ((Snaffle*)entities[toId])->carried) {
                    continue;
                }

                if (spellId == 0) {
                    bludgerIgnoreTeam[fromId < 2 ? 0 : 1] = true;
                }
                else if (spellId == 1) {
                    entities[toId]->vx = 0.0;
                    entities[toId]->vy = 0.0;
                }
                else if (spellId == 2 && entities[toId]->alive) {
                    double dx = entities[toId]->x - entities[fromId]->x;
                    double dy = entities[toId]->y - entities[fromId]->y;
                    double dist = sqrt(dx * dx + dy * dy);
                    double power = MIN((6000 / ((dist * dist) / 1000000)), 1000);
                    double ratio = (power / entities[toId]->mass) / dist;
                    entities[toId]->vx += dx * ratio;
                    entities[toId]->vy += dy * ratio;
                }
                else if (spellId == 3 && entities[toId]->alive) {
                    double dx = entities[fromId]->x - entities[toId]->x;
                    double dy = entities[fromId]->y - entities[toId]->y;
                    double dist = sqrt(dx * dx + dy * dy);
                    double power = MIN((3000 / ((dist * dist) / 1000000)), 1000);
                    double ratio = (power / entities[toId]->mass) / dist;
                    entities[toId]->vx += dx * ratio;
                    entities[toId]->vy += dy * ratio;
                }
            }
        }

        // Spell
        currentChromosome = myChromosome;
        for (int wid = (myTeamId == 0 ? 0 : 2); wid < (myTeamId == 0 ? 2 : 4); wid++) { // TODO retirer hack si double play
            int t = currentChromosome->type[wid % 2][depth] - 20;
            if (t > -1) {
                spells[t * 4 + wid]->to = currentChromosome->a[wid % 2][depth];
                spells[t * 4 + wid]->duration = SPELL_DURATION[t] + 1;
            }
        }
    }

    void postTurnUpdates(int depth)
    {
        if (mana[0] < 100) {
            mana[0]++;
        }
        if (mana[1] < 100) {
            mana[1]++;
        }

        // Spells duration
        for (int i = 0; i < 16; i++) {
            spells[i]->duration--;
        }

        // friction and rounding
        for (int wid = 0; wid < 4; wid++) {
            entities[wid]->vx *= 0.75;
            entities[wid]->vy *= 0.75;
            entities[wid]->vx = round(entities[wid]->vx);
            entities[wid]->vy = round(entities[wid]->vy);
            entities[wid]->x = round(entities[wid]->x);
            entities[wid]->y = round(entities[wid]->y);
        }

        for (int bid = BLUDGER_START; bid < BLUDGER_END; bid++) {
            entities[bid]->vx *= 0.9;
            entities[bid]->vy *= 0.9;
            entities[bid]->vx = round(entities[bid]->vx);
            entities[bid]->vy = round(entities[bid]->vy);
            entities[bid]->x = round(entities[bid]->y);
            entities[bid]->x = round(entities[bid]->y);
        }

        for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {
            if (entities[sid]->alive) {
                entities[sid]->vx *= 0.75;
                entities[sid]->vy *= 0.75;
                entities[sid]->vx = round(entities[sid]->vx);
                entities[sid]->vy = round(entities[sid]->vy);
                entities[sid]->x = round(entities[sid]->x);
                entities[sid]->y = round(entities[sid]->y);
            }
        }
    }
};

// ***********************************************************

void outputChromosomeCommand(int wid, int ct, int ca, int cb)
{
    cerr << "output [" << wid << "] " << ct << " " << ca << " " << cb << endl;
    if (ct == 0) {
        int tx = mainState->entities[wid]->x + precos(cb) * 100000;
        int ty = mainState->entities[wid]->y + presin(cb) * 100000;
        cout << "MOVE " << tx << " " << ty << " " << ca << endl;
    }
    else if (ct == 1) {
        int closestSnaffleId = -1;
        int closestSnaffleSqD = -1;
        for (int sid = SNAFFLE_START; sid < SNAFFLE_END; sid++) {
            if (mainState->entities[sid]->alive) {
                int sqd = (mainState->entities[sid]->x - mainState->entities[wid]->x) * (mainState->entities[sid]->x - mainState->entities[wid]->x) + (mainState->entities[sid]->y - mainState->entities[wid]->y) * (mainState->entities[sid]->y - mainState->entities[wid]->y);
                if (sqd > 0 && (closestSnaffleId == -1 || sqd < closestSnaffleSqD)) {
                    closestSnaffleSqD = sqd;
                    closestSnaffleId = sid;
                }
            }
        }

        int tx = mainState->entities[closestSnaffleId]->x;
        int ty = mainState->entities[closestSnaffleId]->y;
        cout << "MOVE " << tx << " " << ty << " " << ca << endl;
    }
    else if (ct == 10) {
        int tx = wid < 2 ? 16000 : 0;
        int ty = MAX(MIN(mainState->entities[wid]->y, HEIGHT - 3000), 3000);
        cout << "THROW " << tx << " " << ty << " " << ca << endl;
    }
    else if (ct == 11) {
        int tx = mainState->entities[wid]->x + precos(cb) * 100000;
        int ty = mainState->entities[wid]->y + presin(cb) * 100000;
        cout << "THROW " << tx << " " << ty << " " << ca << endl;
    }
    else if (ct > 19) {
        int spellId = ct - 20;
        cout << SPELL_NAME[spellId] << " " << ca << endl;
    }
    else {
        cout << "CANNOT COMPUTE OUTPUT FOR " << wid << " " << ct << " " << ca << " " << cb << endl;
    }
}

void initGeneticPopulation(Chromosome** population)
{
    for (int chromId = 0; chromId < GENETIC_POPSIZE; chromId++) {
        population[chromId]->presimulationRandom();
    }
}

void getNextGeneticPopulation(Chromosome** oldPopulation, Chromosome** newPopulation)
{
    // for (int i = 0; i < (7 * GENETIC_POPSIZE) / 8; i += 2) {
    for (int i = 0; i < GENETIC_POPSIZE; i++) {
        Chromosome* ch1 = NULL;
        Chromosome* ch2 = NULL;

        // Tournament selection
        short tn;
        for (int j = 0; j < GENETIC_TN_SIZE; j++) {
            tn = randShort(0, GENETIC_POPSIZE - 1);
            if (ch1 == NULL || oldPopulation[tn]->score > ch1->score) {
                ch1 = oldPopulation[tn];
            }
        }
        for (int j = 0; j < GENETIC_TN_SIZE; j++) {
            tn = randShort(0, GENETIC_POPSIZE - 1);
            if (ch2 == NULL || oldPopulation[tn]->score > ch2->score) {
                ch2 = oldPopulation[tn];
            }
        }

        Chromosome* nch1 = newPopulation[i];
        nch1->copy(ch1);
        nch1->score = 0;

        for (int i = 0; i < GENETIC_DEPTH; i++) {
            if (RANDOM_NUM < CROSSOVER_RATE) {
                nch1->a[0][i] = ch2->a[0][i];
                nch1->b[0][i] = ch2->b[0][i];
                nch1->type[0][i] = ch2->type[0][i];
            }
            else {
                nch1->a[0][i] = ch1->a[0][i];
                nch1->b[0][i] = ch1->b[0][i];
                nch1->type[0][i] = ch1->type[0][i];
            }

            if (RANDOM_NUM < CROSSOVER_RATE) {
                nch1->a[1][i] = ch2->a[1][i];
                nch1->b[1][i] = ch2->b[1][i];
                nch1->type[1][i] = ch2->type[1][i];
            }
            else {
                nch1->a[1][i] = ch1->a[1][i];
                nch1->b[1][i] = ch1->b[1][i];
                nch1->type[1][i] = ch1->type[1][i];
            }
        }

        if (RANDOM_NUM < MUTATION_RATE) {
            nch1->randomMove(i % 2, randShort(0, GENETIC_DEPTH - 1), true);
        }
    }
}

void play()
{
    gettimeofday(&timestart, NULL);

    GameState* simulated = new GameState(snaffleNumber);
    Chromosome* best = new Chromosome();
    double bestScore = -9999999;
    int bestScoreBatchN = 0;
    int simulationDone = 0;
    int geneticBatch = 0;
    bool popInit = false;

    while (elapsedMS() < 95) {
        if (!popInit) {
            initGeneticPopulation(pop);
            popInit = true;
        }
        else {
            getNextGeneticPopulation(oldPop, pop);
        }

        for (int chromId = 0; chromId < GENETIC_POPSIZE; chromId++) {
            if (chromId % 32 == 0 && elapsedMS() > 95)
                break;

            Chromosome* chromosome = pop[chromId];
            simulated->copy(mainState);
            simulated->simulate(chromosome, NULL, GENETIC_DEPTH);
            chromosome->score = simulated->evaluate();

            if (chromosome->score > bestScore) {
                bestScore = chromosome->score;
                bestScoreBatchN = geneticBatch;
                best->copy(chromosome);
            }

            simulationDone++;
        }
        Chromosome** temp = pop;
        pop = oldPop;
        oldPop = temp;

        geneticBatch++;
    }

    cerr << "best score = " << bestScore << " from batch " << bestScoreBatchN << "/" << geneticBatch << endl;
    cerr << simulationDone << " chromosome simulated (" << (simulationDone * GENETIC_DEPTH) << " turns) in " << elapsedMS() << endl;
    // cerr << "DBG " << best->DBG << endl;

    outputChromosomeCommand(myTeamId ? 2 : 0, best->type[0][0], best->a[0][0], best->b[0][0]);
    outputChromosomeCommand(myTeamId ? 3 : 1, best->type[1][0], best->a[1][0], best->b[1][0]);

    // Update des données du mainState
    simulated->copy(mainState);
    simulated->simulate(best, NULL, 1);
    ((Bludger*)mainState->entities[BLUDGER_START])->lastCollidedWith = ((Bludger*)simulated->entities[BLUDGER_START])->lastCollidedWith;
    ((Bludger*)mainState->entities[BLUDGER_START + 1])->lastCollidedWith = ((Bludger*)simulated->entities[BLUDGER_START + 1])->lastCollidedWith;
    mainState->mana[0] = simulated->mana[0];
    mainState->mana[1] = simulated->mana[1];
    for (int i = 0; i < 16; i++) {
        mainState->spells[i]->copy(simulated->spells[i]);
    }

    delete simulated;
    delete best;
}

void preloop()
{
    for (int i = SNAFFLE_START; i < SNAFFLE_END; i++) {
        mainState->entities[i]->alive = false;
        ((Snaffle*)mainState->entities[i])->carried = false;
    }
    for (int i = 0; i < 4; i++) {
        ((Wizzard*)mainState->entities[i])->snaffleId = -1;
        if (((Wizzard*)mainState->entities[i])->snaffleCd > 0) {
            ((Wizzard*)mainState->entities[i])->snaffleCd--;
        }
    }
    // if (mainState->mana[0] < 100) {
    //     mainState->mana[0]++;
    // }
    // if (mainState->mana[1] < 100) {
    //     mainState->mana[1]++;
    // }
    poles[0]->id = 13;
    poles[0]->x = 0;
    poles[0]->y = 3750 - 2000;
    poles[1]->id = 14;
    poles[1]->x = 0;
    poles[1]->y = 3750 + 2000;
    poles[2]->id = 15;
    poles[2]->x = 16000;
    poles[2]->y = 3750 - 2000;
    poles[3]->id = 16;
    poles[3]->x = 16000;
    poles[3]->y = 3750 + 2000;
}

void initStuff(int entities)
{
    snaffleNumber = entities == 13 ? 7 : 5;
    winningScore = (snaffleNumber / 2) + 1;
    entitiesNumber = entities;
    WIZZARD_START = 0;
    WIZZARD_END = 4;
    SNAFFLE_START = 4;
    if (snaffleNumber == 5) {
        SNAFFLE_END = 9;
        BLUDGER_START = 9;
        BLUDGER_END = 11;
    }
    else {
        SNAFFLE_END = 11;
        BLUDGER_START = 11;
        BLUDGER_END = 13;
    }

    mainState = new GameState(snaffleNumber);
    for (int i = 0; i < GENETIC_POPSIZE; i++) {
        pop[i] = new Chromosome();
        oldPop[i] = new Chromosome();
    }

    for (int i = 0; i < 200; i++) {
        collisionCache[i] = new Collision();
    }

    for (int i = 0; i < 4; i++) {
        poles[i] = new Entity();
        poles[i]->radius = 300;
        poles[i]->mass = 9999999;
        poles[i]->vx = 0;
        poles[i]->vy = 0;
    }

    poles[0]->id = 13;
    poles[0]->x = 0;
    poles[0]->y = 3750 - 2000;
    poles[1]->id = 14;
    poles[1]->x = 0;
    poles[1]->y = 3750 + 2000;
    poles[2]->id = 15;
    poles[2]->x = 16000;
    poles[2]->y = 3750 - 2000;
    poles[3]->id = 16;
    poles[3]->x = 16000;
    poles[3]->y = 3750 + 2000;
}

int main()
{
    srand(time(NULL));
    GameState* oldGameState;
    turn = 0;
    precalculateCosSin();
    cin >> myTeamId;
    cin.ignore();
    hisTeamId = myTeamId == 0 ? 1 : 0;

    // game loop
    while (1) {
        int entities; // number of entities still in game
        cin >> entities;
        if (turn == 0) {
            initStuff(entities);
            oldGameState = new GameState(snaffleNumber);
        }
        cin.ignore();
        preloop();
        for (int i = 0; i < entities; i++) {
            int entityId; // entity identifier
            string entityType; // "WIZARD", "OPPONENT_WIZARD", "SNAFFLE" or "BLUDGER"
            int x; // position
            int y; // position
            int vx; // velocity
            int vy; // velocity
            int state; // 1 if the wizard is holding a Snaffle, 0 otherwise
            cin >> entityId >> entityType >> x >> y >> vx >> vy >> state;

            if (entityType == "WIZARD") {
                Wizzard* wizzard = (Wizzard*)mainState->entities[entityId];
                wizzard->alive = true;
                wizzard->teamId = myTeamId;
                wizzard->holding = state;
            }
            else if (entityType == "OPPONENT_WIZARD") {
                Wizzard* wizzard = (Wizzard*)mainState->entities[entityId];
                wizzard->alive = true;
                wizzard->teamId = myTeamId ? 0 : 1;
                wizzard->holding = state;
            }
            else if (entityType == "SNAFFLE") {
                Snaffle* snaffle = (Snaffle*)mainState->entities[entityId];
                snaffle->alive = true;
            }
            else if (entityType == "BLUDGER") {
                Bludger* bludger = (Bludger*)mainState->entities[entityId];
                bludger->alive = true;
            }
            mainState->entities[entityId]->id = entityId;
            mainState->entities[entityId]->x = x;
            mainState->entities[entityId]->y = y;
            mainState->entities[entityId]->vx = vx;
            mainState->entities[entityId]->vy = vy;

            cin.ignore();
        }

        for (int i = 0; i < 4; i++) {
            Wizzard* wizzard = (Wizzard*)mainState->entities[i];
            if (wizzard->holding) {
                for (int j = SNAFFLE_START; j < SNAFFLE_END; j++) {
                    if (wizzard->x == mainState->entities[j]->x && wizzard->y == mainState->entities[j]->y
                        && wizzard->vx == mainState->entities[j]->vx && wizzard->vy == mainState->entities[j]->vy) {
                        wizzard->snaffleId = j;
                        wizzard->snaffleCd = SNAFFLECD;
                        ((Snaffle*)mainState->entities[j])->carried = true;
                        break;
                    }
                }
            }
        }
        for (int i = SNAFFLE_START; i < SNAFFLE_END; i++) {
            if (!mainState->entities[i]->alive && oldGameState->entities[i]->alive) {
                if (oldGameState->entities[i]->x > 8000) {
                    mainState->score[0]++;
                }
                else {
                    mainState->score[1]++;
                }
            }
        }

        // PRINT SHIT
        cerr << "turn " << turn;
        cerr << " score " << mainState->score[0] << ":" << mainState->score[1];
        cerr << " mana " << mainState->mana[0] << ":" << mainState->mana[1] << endl;

        play();
        turn++;
        // mainState->print();
        oldGameState->copy(mainState);
    }
}
