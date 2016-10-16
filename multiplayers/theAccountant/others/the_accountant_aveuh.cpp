// NB pour les lecteurs : Il y'a des extern et des duplicata de variables globales
// c'est simplement lie au fait que je code dans plusieurs fichiers et teste en compilation
// separee (donc global dans un .h ca aime pas)

#pragma GCC optimize "O3,omit-frame-pointer,inline"
#include <chrono>
#include <iostream>
#include <cmath>
#include <string>
#include <cstring>

// Constantes
#define WIDTH         16000
#define HEIGHT         9000
#define MAX_DP          100
#define MAX_EN          100
#define EN_VEL          500
#define MY_VEL         1000
#define MIN_DIST       2000 // Distance a laquelle on meurt
#define MIN_DIST2   4000000 // Idem, au carre
#define THRESHOLD   4000000
#define INF       336000000 // W*W*H*H

// Etat de la partie
#define STATE_GAME_OVER 0
#define STATE_ONGOING   1
#define STATE_FINISHED  2

// Mode d'IA
#define AI_MODE_MC 0

// MC/AG
#define N_RANDOM 225
const int rand_x[N_RANDOM] = {-10, -80, -151, -222, -292, -363, -434, -504, -575, -646, -717, -787, -858, -929, -1000, -9, -72, -136, -200, -263, -327, -391, -454, -518, -582, -646, -709, -773, -837, -900, -6, -50, -94, -138, -182, -226, -270, -314, -358, -403, -447, -491, -535, -579, -623, -2, -17, -33, -49, -65, -80, -96, -112, -128, -143, -159, -175, -191, -206, -222, 2, 17, 33, 49, 65, 80, 96, 112, 128, 143, 159, 175, 191, 206, 222, 6, 50, 94, 138, 182, 226, 270, 314, 358, 403, 447, 491, 535, 579, 623, 9, 72, 136, 200, 263, 327, 391, 454, 518, 582, 646, 709, 773, 837, 900, 10, 80, 151, 222, 292, 363, 434, 504, 575, 646, 717, 787, 858, 929, 1000, 9, 72, 136, 200, 263, 327, 391, 454, 518, 582, 646, 709, 773, 837, 900, 6, 50, 94, 138, 182, 226, 270, 314, 358, 403, 447, 491, 535, 579, 623, 2, 17, 33, 49, 65, 80, 96, 112, 128, 143, 159, 175, 191, 206, 222, -2, -17, -33, -49, -65, -80, -96, -112, -128, -143, -159, -175, -191, -206, -222, -6, -50, -94, -138, -182, -226, -270, -314, -358, -403, -447, -491, -535, -579, -623, -9, -72, -136, -200, -263, -327, -391, -454, -518, -582, -646, -709, -773, -837, -900, -10, -80, -151, -222, -292, -363, -434, -504, -575, -646, -717, -787, -858, -929, -1000};
const int rand_y[N_RANDOM] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -4, -35, -65, -96, -127, -157, -188, -219, -249, -280, -311, -341, -372, -403, -433, -7, -63, -118, -173, -228, -284, -339, -394, -450, -505, -560, -615, -671, -726, -781, -9, -78, -147, -216, -285, -354, -423, -492, -561, -630, -699, -768, -837, -905, -974, -9, -78, -147, -216, -285, -354, -423, -492, -561, -630, -699, -768, -837, -905, -974, -7, -63, -118, -173, -228, -284, -339, -394, -450, -505, -560, -615, -671, -726, -781, -4, -35, -65, -96, -127, -157, -188, -219, -249, -280, -311, -341, -372, -403, -433, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 35, 65, 96, 127, 157, 188, 219, 249, 280, 311, 341, 372, 403, 433, 7, 63, 118, 173, 228, 284, 339, 394, 450, 505, 560, 615, 671, 726, 781, 9, 78, 147, 216, 285, 354, 423, 492, 561, 630, 699, 768, 837, 905, 974, 9, 78, 147, 216, 285, 354, 423, 492, 561, 630, 699, 768, 837, 905, 974, 7, 63, 118, 173, 228, 284, 339, 394, 450, 505, 560, 615, 671, 726, 781, 4, 35, 65, 96, 127, 157, 188, 219, 249, 280, 311, 341, 372, 403, 433, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

// Les probas associees aux actions (/\ RELATIVES)
extern int PROB_GO_DP, PROB_GO_EN, PROB_GO_RD, PROB_SHOOT;
#define DMG_THRESHOLD 12

// Coeff associe au calcul des vitesses
#define VEL_MIN 300
#define VEL_MAX 1000
#define VEL_DIF 700


// Macros
//#define DEBUG
#define VERBOSE_MODE 0

#define min(a, b) (a < b ? a : b)
#define max(a, b) (a > b ? a : b)
#define RAND(x)   ((int)(rand() / (float)RAND_MAX * x))

// Timing
using time_point = std::chrono::time_point<std::chrono::system_clock>;
using duration = std::chrono::duration<float>;
#define now std::chrono::system_clock::now
#define TIME_LIMIT 0.096f
#define LARGE_TIME_LIMIT 0.996f

// Action du joueur
#define ACTION_MOVE  0
#define ACTION_SHOOT 1
#define ACTION_NONE  2

struct Action {
  int type;   // Si type == MOVE, p1 = x, p2 = y; si SHOOT; p1 = id, p2 = unused
  int p1, p2;
};

#define SOL_SIZE 200
struct Solution {
  int nactions;
  int final_state;
  int dist_from_killer;
  Action actions[SOL_SIZE];
  int score;
};

// Distances
inline int dist2(int x1, int y1, int x2, int y2) {
  int dx = x1-x2;
  int dy = y1-y2;
  return dx*dx+dy*dy;
}

// Deplacement d'une entite
inline void move(int &x, int &y, int tx, int ty, int velocity) {
  int dx = tx - x;
  int dy = ty - y;
  
  if (dx != 0 || dy != 0) {
  float L = sqrt(dx*dx+dy*dy);

  if (L < velocity) {
    x = tx;
    y = ty;
  }
  else {
    float d = velocity / L;
    x += dx*d;
    if (x < 0)
      x = 0;
    else if (x >= WIDTH)
      x = WIDTH-1;

    y += dy * d;
    if (y < 0)
      y = 0;
    else if (y >= HEIGHT)
      y = HEIGHT-1;
  }
  }
}

// Degats infliges a une certaine distance au carre
inline int damage_dealt(int d2) {
  if (d2 < 4082821)
    return 14;
  if (d2 < 4641588)
    return 13;
  if (d2 < 5333598)
    return 12;
  if (d2 < 6206801)
    return 11;
  if (d2 < 7333489)
    return 10;
  if (d2 < 8827108)
    return 9;
  if (d2 < 10874632)
    return 8;
  if (d2 < 13803665)
    return 7;
  if (d2 < 18235270)
    return 6;
  if (d2 < 25477833)
    return 5;
  if (d2 < 38732002)
    return 4;
  if (d2 < 67860440)
    return 3;
  if (d2 < 158988089)
    return 2;
  else
    return 1;
}

// Constantes de jeu
extern int n_dp;
extern int n_enemies;
extern int total_life;
extern int dp_x[MAX_DP];
extern int dp_y[MAX_DP];
extern int ai_mode;
extern int turn;
extern Solution current_solution;
extern int turn_in_sol;
extern time_point start;
extern duration elapsed;


struct State {
  int en_alive;
  int dp_alive;
  int shots;
  int x, y;
  int en_x[MAX_EN];
  int en_y[MAX_EN];
  int en_l[MAX_EN]; // Vie
  int en_t[MAX_EN]; // Cible
  int dp_a[MAX_DP];
  int dist2[MAX_EN];

  // Stats pour gerer la fin de partie / defaite
  int killer; // Si Wolff meurt, on renvoie son tueur
  int dist_from_killer;
  
  State() {
    en_alive = 0;
    dp_alive = 0;
    shots = 0;
    x = 0;
    y = 0;
    killer = -1;
  }
};

void read_state_from_stdin(State &s, bool first);

int evolve(State &s, const Action &a, bool check_for_death);

int game_score(State &s);

// Utils
void output_ICs(State &s);

inline int random_enemy(State &s) {
  int eids[MAX_EN];
  int n_en = 0;
  for (int eid=0; eid < n_enemies; ++eid) {
    if (s.en_l[eid] > 0)
      eids[n_en++] = eid;
  }
  if (n_en == 0)
    return -1;
  return eids[RAND(n_en)];
}

// TODO : inclure les distances des ennemis au joueur dans state ?

int n_dp;
int n_enemies;
int total_life;
int dp_x[MAX_DP];
int dp_y[MAX_DP];
int turn;
int ai_mode;
Solution current_solution;
int turn_in_sol;

void read_state_from_stdin(State &s, bool first) {
  std::cin >> s.x >> s.y;

  // Reset des donnees 
  memset(s.en_x, 0, MAX_EN*sizeof(int));
  memset(s.en_y, 0, MAX_EN*sizeof(int));
  memset(s.en_l, 0, MAX_EN*sizeof(int));
  memset(s.dp_a, 0, MAX_EN*sizeof(int));
  
  // Lecture des data points
  int tmp;
  std::cin >> tmp;
  s.dp_alive = (int8_t)tmp;
  for (int i=0; i < s.dp_alive; ++i) {
    int id, x, y;
    std::cin >> id >> x >> y;

#ifdef DEBUG
    if (VERBOSE_MODE >= 2)
      std::cerr <<  "Data point #" << id << " at position : " << x << " " << y << std::endl;
#endif
  
    s.dp_a[id] = 1;
    dp_x[id] = x;
    dp_y[id] = y;
  }

  if (first)
    n_dp = s.dp_alive;

  // Lecture des ennemis
  int t = 0;
  std::cin >> tmp;
  s.en_alive = (int)tmp;
  for(int i=0; i < s.en_alive; ++i) {
    int id, x, y, life;
    std::cin >> id >> x >> y >> life;

    s.en_x[id] = x;
    s.en_y[id] = y;
    s.en_l[id] = life;

    // On calcule la distance a Wolff
    int d = dist2(s.en_x[id], s.en_y[id], s.x, s.y);
    s.dist2[id] = d;

    // On calcule aussi la prochaine cible de chaque ennemi :
    int best_dist = INF;

    // On cherche le point le plus proche
    for (int pid=0; pid < n_dp; ++pid) {
      //std::cerr << "pid = " << pid << "; Alive = " << s.dp_a[pid] << std::endl;
      if (s.dp_a[pid]) {
	int d = dist2(dp_x[pid], dp_y[pid], s.en_x[id], s.en_y[id]);
	//std::cerr << d << std::endl;
	if (d < best_dist) {
	  best_dist = d;
	  s.en_t[id] = pid;
	  //std::cerr << id << " -> " << s.en_t[id] << std::endl;
	}
      }
    }

#ifdef DEBUG
    std::cerr <<  "Enemy #" << id << " at position : " << x << " " << y << "; Life left : "
	      << life << "; Target = " << s.en_t[id] << std::endl;
#endif
    
    t += life;
  }

  if (first) {
    n_enemies = s.en_alive;
    total_life = t;

#ifdef DEBUG
    std::cerr << "First turn init :" << std::endl;
    std::cerr << " - " << n_enemies << " enemies" << std::endl;
    std::cerr << " - Cumulating " << total_life << " life" << std::endl;
    std::cerr << " - Trying to capture " << n_dp << " data points " << std::endl;
#endif

  }
}

// Fait evoluer d'un step le State et renvoie l'etat de la partie
int evolve(State &s, const Action &a, bool check_for_death) {
  // 1- Deplacement de Wolff
  if (a.type == ACTION_MOVE)
    move(s.x, s.y, a.p1, a.p2, MY_VEL);
  
  // 2- Deplacement des ennemis + check si Wolff est mort
  for (int eid=0; eid < n_enemies; ++eid) {
    // Ennemi en vie ?
    if (s.en_l[eid] > 0) {

      // Pas de cible ? On en trouve une
      int tid;
      //std::cerr << eid << " " << s.en_t[eid] << std::endl;
      if (s.en_t[eid] == -1 || !s.dp_a[s.en_t[eid]]) {
	int best_dist = INF;
	tid = -1;

	// On cherche le point le plus proche
	for (int pid=0; pid < n_dp; ++pid) {
	  if (s.dp_a[pid]) {
	    int d = dist2(dp_x[pid], dp_y[pid], s.en_x[eid], s.en_y[eid]);
	    if (d < best_dist) {
	      best_dist = d;
	      tid = pid;
	    }
	  }
	}
	// On l'associe a l'ennemi
	s.en_t[eid] = tid;
      }
      else
	tid = s.en_t[eid];

      // On se deplace
      move(s.en_x[eid], s.en_y[eid], dp_x[tid], dp_y[tid], EN_VEL);

      // On check si Wolff est mort
      s.dist2[eid] = dist2(s.en_x[eid], s.en_y[eid], s.x, s.y); // On stocke ca pour la suite
      
      if (check_for_death && s.dist2[eid] <= MIN_DIST2) {
	s.killer = eid;
	s.dist_from_killer = s.dist2[eid];
	return STATE_GAME_OVER;
      }
    }
  }
  
  // 3- Tir + Mort
  if (a.type == ACTION_SHOOT) {
    s.en_l[a.p1] -= damage_dealt(s.dist2[a.p1]);
    s.shots++;
    if (s.en_l[a.p1] <= 0 && --s.en_alive == 0)
      return STATE_FINISHED;
  }			   
  
  // 4- Collection des DP
  for (int eid=0; eid < n_enemies; ++eid) {
    if (s.en_l[eid] > 0) {
      int tid = s.en_t[eid];
      if (s.dp_a[tid] && s.en_x[eid] == dp_x[tid] && s.en_y[eid] == dp_y[tid]) {
	s.dp_a[tid] = false;
	if (--s.dp_alive == 0) {
	  return (s.en_alive < n_enemies ? STATE_FINISHED : STATE_GAME_OVER);
	}
      }
    }
  }
  
  // On continue
  return STATE_ONGOING;
}

int game_score(State &s) {
  return s.dp_alive * (100 + max(0, total_life - 3*s.shots) * 3) + 10*(n_enemies - s.en_alive);
}

#define IC_OUT(k, v) (std::cerr << k << " = " << v << ";" << std::endl)

void output_ICs(State &s) {
  IC_OUT("n_enemies", n_enemies);
  IC_OUT("n_dp", n_dp);
  IC_OUT("s.dp_alive", s.dp_alive);
  IC_OUT("s.en_alive", s.en_alive);
  IC_OUT("s.x", s.x);
  IC_OUT("s.y", s.y);
  for (int i=0; i < n_enemies; ++i) {
    IC_OUT("s.en_x["+std::to_string(i)+"]", s.en_x[i]);
    IC_OUT("s.en_y["+std::to_string(i)+"]", s.en_y[i]);
    IC_OUT("s.en_l["+std::to_string(i)+"]", s.en_l[i]);
    IC_OUT("s.en_t["+std::to_string(i)+"]", -1);
  }
  for (int i=0; i < n_dp; ++i) {
    IC_OUT("dp_x["+std::to_string(i)+"]", dp_x[i]);
    IC_OUT("dp_y["+std::to_string(i)+"]", dp_y[i]);
    IC_OUT("s.dp_a["+std::to_string(i)+"]", s.dp_a[i]);
  }
}


void play_turn(State &s);
int play_mc(State &s, float time_limit);
void generate_random_solution(State &s, Solution &sol, float time_limit);





int PROB_OS    = 90; // Proba separee, proba de tuer un adversaire si on a une opportunite d'OS

int PROB_GO_DP = 25;
int PROB_GO_EN = 50;
int PROB_GO_RD = 75;
int PROB_SHOOT = 100;

int MAX_REPS = 5;

// Comparaison de deux solutions
bool operator>(Solution &s1, Solution &s2) {
  // Si les deux solutions ont le meme etat final
  if (s1.final_state == s2.final_state) {
    // Si terminee des deux cotes
    if (s1.final_state == STATE_FINISHED) {
      // Si egalite de score, on prend la solution la plus rapide
      if (s1.score == s2.score)
	return s1.nactions < s2.nactions;
      // On garde celle qui a le meilleur score
      else 
	return s1.score > s2.score;
    }
    // Si mort des deux cotes
    else {
      // En cas d'egalite du tour de fin
      if (s1.nactions == s2.nactions) {
	// On garde celui aui meurt le plus loin de l'adversaire
	return s1.dist_from_killer > s2.dist_from_killer;
      }
      // Sinon on garde celui qui meurt le plus tard
      else
	return s1.nactions > s2.nactions; 
    }
  }
  // Sinon elles ont un etat different, on renvoie si s1 est terminee
  // (ie, si s1.state = FINISHED && s2.state = GAMEOVER)
  else 
    return s1.final_state == STATE_FINISHED;
}

duration elapsed;
time_point start;

void play_turn(State &s) {
  float time_limit = (turn == 1 ? LARGE_TIME_LIMIT : TIME_LIMIT);
  switch(ai_mode) {
  case AI_MODE_MC: play_mc(s, time_limit); break;
  }
}

int play_mc(State &s, float time_limit) {
  start = now();
  bool done = false;
  int ite = 0;
  int nbok = 0;
  Solution sol;
    
  while (!done) {
    ite++;
    State ns(s);

    generate_random_solution(ns, sol, time_limit);

    // Tous les deux fini ? On regarde qui a le meilleur score
    // Si la nouvelle est meilleure, on reset le compteur de tours et on remplace
    if (sol > current_solution) {
      current_solution = sol;
      turn_in_sol = 0;
    }
    
    if (sol.final_state == STATE_FINISHED)
      nbok++;
    
    elapsed = now() - start;
    done = elapsed.count() > time_limit;
  }

  std::cerr << "Enemies alive : " << s.en_alive << std::endl;
  std::cerr << "DP alive : " << s.dp_alive << std::endl;
  std::cerr << "MC, score expected : " << current_solution.score << std::endl;
  std::cerr << "MC, state expected : ";
  switch(current_solution.final_state) {
  case STATE_ONGOING: std::cerr << "On going ...... ?!!" << std::endl; break;
  case STATE_GAME_OVER: std::cerr << "Lost !" << std::endl; break;
  case STATE_FINISHED: std::cerr << "Finished !" << std::endl; break;
  }
  
  std::cerr << ite << " solutions ; " << nbok << " solutions that survive" << std::endl;
  Action best_action = current_solution.actions[turn_in_sol];
  if (best_action.type == ACTION_MOVE)
    std::cout << "MOVE " << best_action.p1 << " " << best_action.p2 << " " << ite << std::endl;
  else {
    std::cout << "SHOOT " << best_action.p1 << " " << ite << std::endl;
    s.shots++;
  }
  turn_in_sol++;

  int res = (turn_in_sol < current_solution.nactions ? STATE_ONGOING : current_solution.final_state);
  return res;
}

void generate_random_solution(State &s, Solution &sol, float time_limit) {
  int state = STATE_ONGOING;
  int t = 0;
  Action a;
  
  // On itere jusqu'a la fin de la partie
  while (state == STATE_ONGOING) {
    
    int best_id      = -1;
    int best_dist    =  INF;
    int best_life    =  0;
    int closest_id   = -1;
    int closest_dist = INF;
    bool can_os   = false;

    // On a besoin de la position de l'ennemi au prochain tour
    State ns(s);
    a.type=ACTION_NONE;
    evolve(ns, a, false);
    
    // Si on peut one-shot un adversaire on le fait
    // Pr ordre : on prefere les adversaires le plus loin, et si possible avec le plus de vie
    for (int eid=0; eid < n_enemies; ++eid) {
      if (s.en_l[eid] > 0) {
	int d = ns.dist2[eid];
	int dmg = damage_dealt(d);

	// One-shot
	if (dmg >= s.en_l[eid]) {
	  can_os = true;
	  
	  // Priorite a l'adversaire le plus proche ?
	  if (d < best_dist) {
	    best_id = eid;
	    best_dist = d;
	    best_life = s.en_l[eid];
	  }
	  else if (d == best_dist && s.en_l[eid] > best_life) {
	    best_id = eid;
	    best_dist = d;
	    best_life = s.en_l[eid];  
	  }
	}

	// On en profite aussi pour stocker l'ennemi le plus proche
	if (d < closest_dist) {
	  closest_dist = d;
	  closest_id   = eid;
	}
      }
    }

    if (can_os && RAND(100) < PROB_OS) {
      a = Action{ACTION_SHOOT, best_id, 0};
      sol.actions[t++] = a;
      state = evolve(s, a, true);
      continue;
    }

    // Si pas de One shot, alors on tire au sort les coups possibles entre :
    // 1 - Aller en direction d'un CP vise par un adversaire (a pleine vitesse)
    // 2 - Aller en direction d'un adversaire (vitesse variable)
    // 3 - Effectuer un mouvement aleatoire
    // 4 - Abattre un adversaire a distance
    
    // On commence par regarder quels degats on fait a l'ennemi le plus proche
    int closest_dmg = damage_dealt(closest_dist);
    int prob = 150; // Pas chercher
    int action = RAND(prob);
    int nreps = RAND(5);
    int ite = 0;

      // 1- Aller sur un DP a fond
      if (action <= PROB_GO_DP) {
	// On tire un adversaire au hasard
	/*int eid = random_enemy(s);
	int tid = s.en_t[eid];*/
	int pids[MAX_DP];
	int ndp=0;
	for (int i=0; i < n_dp; ++i)
	  if (s.dp_a[i])
	    pids[ndp++] = i;
	    
	int tid = pids[RAND(ndp)];
    while (state == STATE_ONGOING && ite < nreps) {
          
	// On se deplace en direction de sa cible
	a = Action{ACTION_MOVE, dp_x[tid], dp_y[tid]};
	sol.actions[t++] = a;
	state = evolve(s, a, true);
	ite++;
    }
      }
      // 2- Aller sur un adversaire
      else if (action <= PROB_GO_EN) {
	// On tire un adversaire au hasard
	int eid = random_enemy(s);
      
	// On se deplace dans sa direction
	    while (state == STATE_ONGOING && ite < nreps) {
    
	int x = s.x;
	int y = s.y;
	move(x, y, s.en_x[eid], s.en_y[eid], MY_VEL);
	
	// On cree l'action
	a = Action{ACTION_MOVE, x, y};
	sol.actions[t++] = a;
	state = evolve(s, a, true);
	ite++;
	    }
      }
      // 3- Random move
      else if (action <= PROB_GO_RD) {
	int move = RAND(N_RANDOM);
	int rx = rand_x[move];
	int ry = rand_y[move];
	
	// On cree l'action
	    while (state == STATE_ONGOING && ite < nreps) {
	a = Action{ACTION_MOVE, s.x + rx, s.y + ry};
	sol.actions[t++] = a;
	state = evolve(s, a, true);
	ite++;
	    }
      }
      // 4- Abattre l'ennemi le plus proche
      else {
	a = Action{ACTION_SHOOT, closest_id, 0};
	while (state == STATE_ONGOING && s.en_l[closest_id] > 0) {
	  sol.actions[t++] = a;
	  state = evolve(s, a, true);
	}
    }
  }

  // Recopie de l'etat et infos supplementaires liees au dernier tour
  sol.final_state = state;
  sol.score = game_score(s);
  if (state == STATE_GAME_OVER)
    sol.dist_from_killer = s.dist_from_killer;
  sol.nactions = t;
}


int main() {
  srand(26111996);
  turn = 0;
  ai_mode = AI_MODE_MC;
  
  State s;
  turn_in_sol = 0;
  current_solution.score = -INF;
  
  while (true) {
    read_state_from_stdin(s, turn==0);
    turn++;
    play_turn(s);
  }
}
