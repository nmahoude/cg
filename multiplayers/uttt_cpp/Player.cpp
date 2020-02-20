#include <iostream>
#include <string>
#include <vector>
#include <algorithm>

using namespace std;

int turn = 0;
long start;

class Node;

class PMove {
  int x, y;
};

class Grid {
public:
  const int allCells = 0b111111111;
  const int[] completedLines = new int[] { 0b111000000, 0b000111000, 0b000000111, 0b100100100, 0b010010010, 0b001001001, 0b100010001, 0b001010100 };
  PMove possibleMoves[512][81];
  int possibleMovesFE[512];
  
  static init() {
    for (int mask = 0; mask < 512; mask++) {
      possibleMovesFE[mask] = 0;
      for (int i = 0; i < 9; i++) {
        if ((mask & (1 << i)) == 0) {
          PMove move = &possibleMoves[mask][possibleMovesFE[mask]++];
          move.x = i % 3;
          move.y = i / 3;
        }
      }
    }
  }
  
  int winner = -1;
  int myGrid;
  int hisGrid;
  bool full = false;
  int baseX, baseY;
  
  int get(bool b) {
    if (b) return myGrid; else return hisGrid;
  }
  
  int getComplete() {
    return myGrid | hisGrid;
  }
  
  void set(bool who, int x, int y) {
    int temp = get(who);
    temp = temp | ( 1 << x << 3*y);
    if (who) {
      myGrid = temp;
    } else {
      hisGrid = temp;
    }
    for (int i=0;i<8;i++) {
      if (completedLines[i] & myGrid == completedLines[i]) {
        winner = 0;
        full = true;
        return;
      }
      if (completedLines[i] & hisGrid == completedLines[i]) {
        winner = 1;
        full = true;
        return;
      }
    }
    if (getComplete() & allCells == allCells) {
      full = true;
      return;
    }
  }
  
  void copyFrom(Grid *grid) {
    baseX = grid->baseX;
    baseY = grid->baseY;
    myGrid = grid->myGrid;
    hisGrid = grid->hisGrid;
    winner = grid->winner;
    full = grid->full;
  }
};

class State {
public:
  Grid mainGrid;
  Grid grids[9];
  Grid* nextPlayGrid = null;
  
  State() {
    for (int y=0;y<3;y++) {
      for (int x=0;x<3;x++) {
        grids[x + 3*y].baseX = 3 * x;
        grids[x + 3*y].baseY = 3 * y;
      }
    }
  }
  
  bool terminated() {
    return mainGrid.winner != -1 || mainGrid.full;
  }
  
  int winner() {
    if (mainGrid.winner != -1) {
      return mainGrid.winner;
    } else if (mainGrid.full) {
      return -1;
    } else {
      int score = 0;
      for (int i=0;i<9;i++) {
        if (grids[i].winner == 0) {
          score++;
        } else if (grids[i].winner == 2) {
          score--;
        }
      }
      if (score > 0) return 0; else if (score < 0) return 1; else return -1;
    }
  }
 
  void set(bool b, int opponentRow, int opponentCol) {
    set(b, (int) (opponentCol / 3), (int) (opponentRow / 3), opponentCol % 3, opponentRow % 3);
  }
  
  void set(bool b, int gridX, int gridY, int x, int y) {
    int gridIndex = gridX + 3*gridY;
    Grid* grid = &(grids[gridIndex]);
    grid->set(b, x, y);
    if (grid.winner != -1) {
      mainGrid.set(b, gridX, gridY);
    }
    if (grid.full) {
      bool full = true;
      for (int i=0;i<9;i++) {
        if (grids[i].winner == -1 && !grids[i].full) {
          full = false;
          break;
        }
      }
      mainGrid.full = full;
    }
    
    nextPlayGrid = &(grids[x + 3*y]);
    if (nextPlayGrid->winner != -1) {
      nextPlayGrid = null;
    } else if (nextPlayGrid->full) {
      nextPlayGrid = null;
    }
  }
  
  void copyFrom(State oldState) {
    this->mainGrid->copyFrom(oldState->mainGrid);
    for (int i=0;i<9;i++) {
      grids[i].copyFrom(oldState->grids[i]);
    }
    if (oldState->nextPlayGrid == null) {
      nextPlayGrid = null;
    } else {
      nextPlayGrid = &grids[oldState->nextPlayGrid->baseX / 3 + 3 * (oldState->nextPlayGrid->baseY / 3)];
    }
  }
  
};


class Node {
public:
  const double SCORE_C = Math.sqrt(2);
  const State* tempState = new State();
  Node* parent = null;
  State state = new State();
  int childArrayFE = -1;
  const Node* childArray[81];
  int unexploredFE = 0;
  const Node* unexplored[81];
  bool player = true;
  int won=0, lose=0, tie=0;
  int totalTrials = 0;
  int col, row;
  
public:
  void release() {
    for (int i=childArrayFE-1;i>=0;i--) {
      childArray[i].release();
    }
    NodeCache.push(this);
  }
  
  void update(Node* parent, bool player, int row,int col) {
    this->parent = parent;
    this->player = player;
    this->col = col;
    this->row = row;
    reset();
  }
  void reset() {
    this->won = 0;
    this->lose = 0;
    this->tie = 0;
    this->totalTries = 0;
  }
  
  void chooseChild() {
    if (childArrayFE == -1) {
      this->getAllChildren();
    }
    if (childArrayFE == 0) {
      int result = state->winner();
      backPropagate(result);
      return;
    } else {
      Node* bestChild = childArray[0];
      double bestScore = score(bestChild);
      double score =0;
      for (int i=childArrayFE-1;i>=0;i--) {
        Node* child = childArray[i];
        score = score(child);
        if (score > bestScore) {
          bestScore = score;
          bestNode = child;
        }
      }
      bestChild->chooseChild();
    }
  }
  
  void runSimulation() {
    int winner = randomSimulate();
    this->backPropagate(winner);
  }
  
  static int possibleFE = 0;
  static int possibleGrid[9*9];
  int randomSimulate() {
    tempState->copyFrame(state);
    bool currentPlayer = this->player;
    while (!tempState->teminated()) {
      getPossibleMoves(tempState);
      int rand = Player.random(possibleFE);
      int full = possibleGrid[rand];
      int row = full / 10;
      int col = full - (row * 10);
      tempState->set(currentPlayer, row, col);
      currentPlayer = !currentPlayer;
    }
    return tempState->winner();
  }
  
  void getPossibleMoves(State* state) {
    possibleFE = 0;
    if (state->nextPlayGrid != null) {
      getPossibleMovesForGrid(state->nextPlayGrid);
    } else {
      getPossibleMovesForGrid(state->grids[0]);
      getPossibleMovesForGrid(state->grids[1]);
      getPossibleMovesForGrid(state->grids[2]);
      getPossibleMovesForGrid(state->grids[3]);
      getPossibleMovesForGrid(state->grids[4]);
      getPossibleMovesForGrid(state->grids[5]);
      getPossibleMovesForGrid(state->grids[6]);
      getPossibleMovesForGrid(state->grids[7]);
      getPossibleMovesForGrid(state->grids[8]);
    }
  }
  
  void getPossibleMovesForGrid(Grid* grid) {
    if (grid->full) return;
    
    int all = grid->getComplete();
    PMove** moves = Grid.possibleMoves[all];
    for (int i= Grid.possibleMovesFE[all]-1;i>=0;i--) {
      PMove* move = moves[i];
      possibleGrid[possibleFE++] = 10 * (grid->baseY + move->y)
                                    + (grid->baseX + move->x);
    }
  }

  void getAllChildren() {
    childArrayFE = 0;
    unexploredFE = 0;
    getPossibleMoves(state);
    int full, col, row;
    for (int i=0;i<possibleFE; i++) {
      Node* node = NodeCache.pop();
      node->state->copyFrom(this->state);
      full = possibleGrid[i];
      row = full / 10;
      col = full - (row * 10);
      node->state->set(this->player, row, col);
      node->update(this, !player, row, col);
      childArray[childArrayFE++] = node;
      unexplored[unexploredFE++] = node;
    }
  }
  
  double score(Node* child) {
    double w = 0;
    if (player) {
      w = child->won + child->tie / 2 - child->lose;
    } else {
      w = child->lose + child->tie / 2 - child->won;
    }
    int n = child->totalTrials;
    double t = this->totalTrials;
    return w / n + SCORE_C * Math.sqrt(Math.log(t) / n);
  }
  
  void backPropagate(int winner) {
    totalTrials++;
    if (winner == 0) {
      won++;
    } else if (winner == 1){
      lose++;
    } else {
      tie++;
    }
    if (parent != null)  {
      parent->backPropagate(winner);
    }
  }
  
  Node* getBest() {
    double best = -1000000000; // TODO enough ?
    Node* bestNode = null;
    for (int i=childArrayFE-1;i>=0;i--) {
      Node* node = childArray[i];
      double score = node->totalTrials;
      if (score > best) {
        best = score;
        bestNode = node;
      }
    }
    return bestNode;
  }
  
  void setState(State* old) {
    this->state->copyFrom(old);
    this->state->nextPlayGrid = old->nextPlayGrid; // TODO needed ? not in copy
  }
};

class NodeCache {
  static const Node* nodes[200100];
  static int nodesIndex = 200100;
public:  
  void init() {
    for (int i=0;i<200100;i++) {
      nodes[i] = new Node();
    }
  }
  
  static Node* pop() {
    Node* node = nodes[nodesIndex--];
    node->childArrayFE = -1;
    node->parent = null;
    return node;
  }
  static void push(Node *node) {
    nodes[nodeIndex++] = node;
  }
};
class MCTS {
public:
  Node *root, *best;
  MCTS() {
    root = NodeCache.pop();
  }
  
  State* getCurrentState() {
    return root->state;
  }
  void think() {
    long duration = time() - start;
    while (root->totalTrials < 200_000 && duration < 95) {
      root->chooseChild();
      if (100 * (root->totalTrials / 100) == root->totalTrials) {
        duration = time() - start;
      }
    }
    best = root->getBest();
  }
  
  void output() {
    cout << "0 0" << endl;
  }
  
  void doAction(bool who, int row, int col) {
    Node* toKeep = null;
    for (int i=0;i<root->childArrayFE;i++) {
      Node* node = root->childArray[i];
      if (node->row == row && node->col == col) {
        toKeep = node;
      } else {
        node.release();
      }
    }
    if (toKeep == null) {
      // no node for this ply, create one
      root->state->set(who, row, col);
      root->player = !who;
      root.childArrayFE = -1;
    } else {
      root = toKeep;
      root->parent = null;
    }
  }
  
  void firstToPlay() {
    root->childArrayFE = 0;
    root->unexploredFE = 0;
    Node* node = NodeCache.pop();
    node->state->copyFrom(root->state);
    node->state->set(true, 4, 4);
    node->update(root, false, 4, 4);
    root->childArray[root->childArrayFE++] = node;
    root->unexplored[root->unexploredFE++] = node;
  }
  
};

int main() {
  MCTS* ai = 0;
  
  // game loop
  while (1) {
    int opponentRow;
    int opponentCol;

    cin >> opponentRow >> opponentCol;
    cin.ignore();

    start = ??
    
    if (turn ==0) {
      ai = new MCTS();
      NodeCache.init();
      Player.start += 900;
    }
    
    int validActionCount;
    cin >> validActionCount;
    cin.ignore();
    for (int i = 0; i < validActionCount; i++) {
      int row;
      int col;
      cin >> row >> col;
      cin.ignore();
    }
    
    if (opponentCol == -1) {
      ai->firstToPlay();
      ai->think();
      ai->doAction(true, ai->best->row, ai->best->col);
      ai->output();
    } else {
      ai->doAction(false, opponentRow, opponentCol);
      ai->think();
      ai->doAction(true, ai->best->row, ai->best->col);
      ai->output();
    }
    
    turn++;
  }
}
