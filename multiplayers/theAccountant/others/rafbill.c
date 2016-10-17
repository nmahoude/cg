// -*- compile-command: "make all"; -*-
#define NDEBUG 1
#define _GLIBCXX_USE_CXX11_ABI 0
#pragma GCC optimize "O3,omit-frame-pointer,inline"
#include <stdio.h>
#include <bits/stdc++.h>
#include <immintrin.h>
#include <x86intrin.h>

#define DESTRUCT2(p, a, b)                      \
  auto a = get<0>(p);                           \
  auto b = get<1>(p);

#define DESTRUCT3(p, a, b, c)                   \
  auto a = get<0>(p);                           \
  auto b = get<1>(p);                           \
  auto c = get<2>(p);

#define DESTRUCT4(p, a, b, c, d)                \
  auto a = get<0>(p);                           \
  auto b = get<1>(p);                           \
  auto c = get<2>(p);                           \
  auto d = get<3>(p);

#define FOR(i, n)     for(int i = 0; i < (int)(n); ++i)
#define FORU(i, j, k) for(int i = (j); i <= (int)(k); ++i)
#define FORD(i, j, k) for(int i = (j); i >= (int)(k); --i)

#define SQ(x) ((x)*(x))

#define all(x) begin(x), end(x)
#define rall(x) rbegin(x), rend(x)
#define mp make_pair
#define mt make_tuple
#define pb push_back
#define eb emplace_back

using namespace std;

template<typename... As>
struct tpl : public std::tuple<As...> {
  using std::tuple<As...>::tuple;

  template<typename T = tuple<As...> >
  typename tuple_element<0, T>::type const&
  x() const { return get<0>(*this); }
  template<typename T = tuple<As...> >
  typename tuple_element<0, T>::type&
  x() { return get<0>(*this); }

  template<typename T = tuple<As...> >
  typename tuple_element<1, T>::type const&
  y() const { return get<1>(*this); }
  template<typename T = tuple<As...> >
  typename tuple_element<1, T>::type&
  y() { return get<1>(*this); }

  template<typename T = tuple<As...> >
  typename tuple_element<2, T>::type const&
  z() const { return get<2>(*this); }
  template<typename T = tuple<As...> >
  typename tuple_element<2, T>::type&
  z() { return get<2>(*this); }

  template<typename T = tuple<As...> >
  typename tuple_element<3, T>::type const&
  w() const { return get<3>(*this); }
  template<typename T = tuple<As...> >
  typename tuple_element<3, T>::type&
  w() { return get<3>(*this); }
};

using vi=vector<int>;
using vii=vector<tpl<int,int>>;

template<class T>
ostream& print_collection(ostream& s, T const& a);

template<size_t... I>
struct my_index_sequence {
  using type = my_index_sequence;
  static constexpr array<size_t, sizeof...(I)> value = { {I...} };
};

namespace my_index_sequence_detail {
  template<typename I, typename J> struct concat;
  template<size_t... I, size_t... J>
  struct concat<my_index_sequence<I...>, my_index_sequence<J...> > :
    my_index_sequence<I..., (sizeof...(I)+J)...> { };
  template<size_t N> struct make_index_sequence :
    concat<typename make_index_sequence<N/2>::type, typename make_index_sequence<N-N/2>::type>::type { };
  template <> struct make_index_sequence<0> : my_index_sequence<>{};
  template <> struct make_index_sequence<1> : my_index_sequence<0>{};
}

template<class... A>
using my_index_sequence_for = typename my_index_sequence_detail::make_index_sequence<sizeof...(A)>::type;

template<class T, size_t... I>
void print_tuple(ostream& s, T const& a, my_index_sequence<I...>){
  using swallow = int[];
  (void)swallow{0, (void(s << (I == 0? "" : ", ") << get<I>(a)), 0)...};
}

template<class... A>
ostream& operator<<(ostream& s, tpl<A...> const& a){
  s << '(';
  print_tuple(s, a, my_index_sequence_for<A...>{});
  return s << ')';
}

template<class... A>
ostream& operator<<(ostream& s, tuple<A...> const& a){
  s << '(';
  print_tuple(s, a, my_index_sequence_for<A...>{});
  return s << ')';
}

template<class A, class B>
ostream& operator<<(ostream& s, pair<A, B> const& a){
  return s << "(" << get<0>(a) << ", " << get<1>(a) << ")";
}

template<class T, size_t I>
ostream& operator<<(ostream& s, array<T, I> const& a) { return print_collection(s, a); }
template<class T>
ostream& operator<<(ostream& s, vector<T> const& a) { return print_collection(s, a); }
template<class T, class U>
ostream& operator<<(ostream& s, multimap<T, U> const& a) { return print_collection(s, a); }
template<class T>
ostream& operator<<(ostream& s, multiset<T> const& a) { return print_collection(s, a); }
template<class T, class U>
ostream& operator<<(ostream& s, map<T, U> const& a) { return print_collection(s, a); }
template<class T>
ostream& operator<<(ostream& s, set<T> const& a) { return print_collection(s, a); }

template<class T>
ostream& print_collection(ostream& s, T const& a){
  s << '[';
  for(auto it = begin(a); it != end(a); ++it){
    s << *it;
    if(it != prev(end(a))) s << " ";
  }
  return s << ']';
}

uint32_t xorshift128() {
  static uint32_t x = 7878, y = 2, z = 3, w = 4;
  uint32_t t = x;
  t ^= t << 11;
  t ^= t >> 8;
  x = y; y = z; z = w;
  w ^= w >> 19;
  w ^= t;
  return w;
}

inline int random(int N) {
  return xorshift128()%N;
}

inline double randomDouble() {
  return (double)xorshift128()/4294967296.0;
}

// Terminal related
#ifdef LOCAL
#define P_BEGIN   "\033[2K\r" // Used to get back to the beginning of the line, e.g. for a progress counter
#define C_RED     "\033[31m" // Colors
#define C_GREEN   "\033[32m"
#define C_YELLOW  "\033[33m"
#define C_BLUE    "\033[34m"
#define C_MAGENTA "\033[35m"
#define C_CYAN    "\033[36m"
#define C_BRED     "\033[31;1m" // Bright
#define C_BGREEN   "\033[32;1m"
#define C_BYELLOW  "\033[33;1m"
#define C_BBLUE    "\033[34;1m"
#define C_BMAGENTA "\033[35;1m"
#define C_BCYAN    "\033[36;1m"
#define C_RESET   "\033[0m"  // Reset the terminal state
#else
#define P_BEGIN   "" // Used to get back to the beginning of the line, e.g. for a progress counter
#define C_RED     "" // Colors
#define C_GREEN   ""
#define C_YELLOW  ""
#define C_BLUE    ""
#define C_MAGENTA ""
#define C_CYAN    ""
#define C_BRED     "" // Bright
#define C_BGREEN   ""
#define C_BYELLOW  ""
#define C_BBLUE    ""
#define C_BMAGENTA ""
#define C_BCYAN    ""
#define C_RESET   ""  // Reset the terminal state
#endif

// Time

static struct timeInfoS {
  chrono::high_resolution_clock::time_point c_start;
  timeInfoS(){ c_start = chrono::high_resolution_clock::now(); }
  double getTime() {
    chrono::high_resolution_clock::time_point c_now =
      chrono::high_resolution_clock::now();
    return chrono::duration<double>(c_now-c_start).count();
  }
} timeInfo;

#define MIN_INT numeric_limits<int>::min()
#define MAX_INT numeric_limits<int>::max()

const int H = 16000;
const int W = 9000;

struct pos {
  pos(){ }
  pos(int x_, int y_) : x(x_), y(y_) { }
  int x = 0, y = 0;

  bool operator==(pos const& b) const { return tie(x,y)==tie(b.x,b.y); }
  bool operator!=(pos const& b) const { return tie(x,y)!=tie(b.x,b.y); }
  bool operator<(pos const& b) const { return tie(x,y)<tie(b.x,b.y); }
  // bool operator<=(pos const& b) const { return memcmp(this, &b, sizeof(pos)) <= 0; }
  // bool operator>(pos const& b) const { return memcmp(this, &b, sizeof(pos)) > 0; }
  // bool operator>=(pos const& b) const { return memcmp(this, &b, sizeof(pos)) >= 0; }

  bool valid() const { return 0<=x&&0<=y&&x<H&&y<W; }
  int dist2(pos const& o) const { return SQ(x-o.x)+SQ(y-o.y); }
  void clamp() {
    if(x<0) x=0;
    if(x>=H) x=H-1;
    if(y<0) y=0;
    if(y>=W) y=W-1;
  }
};

ostream& operator<<(ostream& s, pos const& p) {
  return s << mt(p.x,p.y);
}

const int damageDists[14] = {337000000,158988089,67860440,38732002,25477833,18235270,13803665,10874632,8827108,7333489,6206801,5333598,4641588,4082821};

struct adata {
  int  id;
  pos  xy;
};

struct enemy {
  int id;
  pos xy;
  int life;
  int tgt=-1;
};

void moveTo(pos &p, pos const& q, int dist) {
  int d = p.dist2(q);
  if(d<=SQ(dist)) {
    p = q;
  }else{
    double sqrtd = sqrt((double)d);
    p.x = p.x+(int)floor(dist*(q.x-p.x)/sqrtd);
    p.y = p.y+(int)floor(dist*(q.y-p.y)/sqrtd);
  }
}

struct mov{
  mov() = default;
  int type,x,y;
  mov(pos p) : type(0), x(p.x), y(p.y) { }
  mov(int u) : type(1), x(u) { }
  void print(){
    if(type==0){
      cout << "move " << x << " " << y << endl;
    }else{
      cout << "shoot " << x << endl;
    }
  }
};

struct state {
  int   nshoot;
  pos   player;
  int   activeData[100]; int nactive;
  adata datas[100];
  enemy enemies[100]; int senemies;
  bool  lost=0;

  bool ended() const {
    return lost||nactive==0||senemies==0;
  }

  void read() {
    lost=0; nshoot=0;
    cin >> player.x >> player.y;
    int nd; cin >> nd;
    nactive=0;
    FOR(i, nd) {
      int id, ix, iy; cin >> id >> ix >> iy;
      activeData[nactive++]=id;
      datas[id] = adata{id,pos(ix,iy)};
    }

    int ne; cin >> ne;
    senemies=0;
    FOR(i, ne) {
      auto& e = enemies[senemies++];
      cin >> e.id >> e.xy.x >> e.xy.y >> e.life;
    }
  }

  void print() {
    cerr << player.x << " " << player.y << endl;
    cerr << nactive << endl;
    FOR(i_, nactive) {
      int i = activeData[i_];
      auto const& dt = datas[i];
      cerr << dt.id << " " << dt.xy.x << " " << dt.xy.y << endl;
    }
    cerr << senemies << endl;
    FOR(ei, senemies) {
      auto &en = enemies[ei];
      cerr << en.id << " " << en.xy.x << " " << en.xy.y << " " << en.life << endl;
    }
  }

  void updateTgt(){
    FOR(ei, senemies) {
      auto& e = enemies[ei];
      int bd=MAX_INT;
      FOR(i_, nactive) {
        int i = activeData[i_];
        int d = e.xy.dist2(datas[i].xy);
        if(d<bd) { bd=d; e.tgt=i; }
      }
    }
  }

  bool update(int shootId, int* killingEnemy = 0, int* takingEnemy = 0) {
    bool killed=false;
    bitset<100> takenData;
    int sIx=-1;
    FOR(ei, senemies) {
      auto& e = enemies[ei];
      if(e.id==shootId) sIx=ei;
      moveTo(e.xy, datas[e.tgt].xy, 500);
      if(takingEnemy==0 && e.xy.dist2(player) <= SQ(2001)) {
        if(killingEnemy) *killingEnemy = e.id;
        lost=1;
        return killed;
      }
    }
    if(sIx!=-1) {
      int di=enemies[sIx].xy.dist2(player);
      int d=0;
      FOR(i,14) if(di<=damageDists[i]) d=i+1;
      enemies[sIx].life -= d;
      if(enemies[sIx].life <= 0) {
        killed=1;
        swap(enemies[senemies-1], enemies[sIx]);
        senemies-=1;
      }
    }
    FOR(ei, senemies) {
      auto& e = enemies[ei];
      if(e.xy == datas[e.tgt].xy) {
        if(takingEnemy) *takingEnemy = e.id;
        takenData[e.tgt]=1;
      }
    }
    if(takenData.any()) {
      int l=0;
      FOR(i, nactive) if(!takenData[activeData[i]]) {
        activeData[l++] = activeData[i];
      }
      nactive=l;
    }
    FOR(ei, senemies) {
      auto& e = enemies[ei];
      if(takenData[e.tgt]) {
        int bd=MAX_INT;
        FOR(i_, nactive) {
          int i = activeData[i_];
          int d = e.xy.dist2(datas[i].xy);
          if(d<bd) { bd=d; e.tgt=i; }
        }
      }
    }
    return killed;
  }
};

struct strategy {
  int param0; int param1;
  bool doMoveTo=false;
  pos moveTo;
  bool intercept[100]={0};
  int order[100]; int sorder=0;
  void addOrder(int o) { order[sorder++]=o; }
  void remFirst() { sorder-=1; FOR(i, sorder) { order[i]=order[i+1]; } }
};

struct ai {

  int next_taking_enemy(state s) {
    while(!s.ended()) {
      int ne=-1;
      s.update(-1, 0, &ne);
      if(ne!=-1) return ne;
    }
    return -1;
  }

  int next_killing_enemy(state s) {
    while(!s.ended()) {
      int ne=-1;
      s.update(-1, &ne);
      if(ne!=-1) return ne;
    }
    return -1;
  }

  int closest_enemy(state const& s) {
    int ie=-1; int bd=MAX_INT;
    FOR(ei, s.senemies) {
      auto const& e = s.enemies[ei];
      int d = e.xy.dist2(s.player);
      if(d<bd) { bd=d; ie=e.id; }
    }
    return ie;
  }

  int closest_enemy_(state const& s, bool (&targeted)[100]) {
    int ie=-1; int bd=MAX_INT;
    FOR(ei, s.senemies) {
      auto const& e = s.enemies[ei];
      if(targeted[e.id]) continue;
      int d = e.xy.dist2(s.player);
      if(d<bd) { bd=d; ie=e.id; }
    }
    return ie;
  }


  bool has_enemy(state const& s, int id, enemy const** p=0) {
    FOR(ei, s.senemies) if(s.enemies[ei].id==id) {
      if(p) *p = &s.enemies[ei];
      return 1;
    }
    return 0;
  }


  template<int K, int W>
  vector<mov> improveMoveseq(state const& s, vector<mov> const& moves, int movScore, int& newScore) {
    if(s.nactive>30&&s.senemies>30) {
      newScore = movScore;
      return moves;
    }
    // cerr << C_YELLOW << s.player << " ";
    // FOR(i,s.senemies) cerr << C_BLUE << mt(i,s.enemies[i].life) << C_RESET << " ";
    // cerr << endl;
    vi T(ne);
    { vi E(ne);
      FORD(i,moves.size()-1, 0){
        auto m = moves[i];
        if(m.type==1&&!E[m.x]){
          E[m.x]=1;
          T[m.x]=i;
        }
      }
    }
    vector<tpl<pos,vi>> M;
    { pos p=s.player;
      vi g;
      FOR(i, moves.size()) {
        auto m = moves[i];
        if(m.type==0){
          if(!g.empty()) { M.pb(mt(p,move(g))); g.clear(); }
          moveTo(p,pos(m.x,m.y),1000);
        }else{
          if(i==T[m.x]) g.pb(m.x);
        }
      }
      if(!g.empty()) { M.pb(mt(p,move(g))); g.clear(); }
    }
    // { pos p=s.player;
    //   cerr << C_BRED;
    //   for(auto const& m : M) {
    //     cerr << sqrt(p.dist2(m.x())) << " ";
    //     p=m.x();
    //   }
    //   cerr << C_RESET << endl;
    // }
    // cerr << C_BBLUE << M << C_RESET << endl;

    // extended state
    struct eState {
      state s;
      int i,j;
      int from;
      mov m;
    };
    auto score = [&](state const& t) {
      return (100+3*max(0, tlife-3*t.nshoot))*t.nactive+10*(ne-t.senemies);
    };

    const int MP=100;
    static eState BEAM[MP+1][K]; int nb=1; BEAM[0][0]=eState{s,0,0,-1,mov(-1)};
    static eState ARRAY[K*W]; int na=0;
    static int H[K*W];
    static int I[K*W];
    set<tpl<int,int> > S;
    int nwinn=0;
    int cp=0;
    int bestScore=0; int bestCp=0; int bestI=0; mov bestM;
    while(nb&&cp<MP&&((gturn==1&&timeInfo.getTime()<t00+0.95)||(timeInfo.getTime()<t00+0.095))){
      FOR(i, nb) {
        int nnew=0;
        auto addNew = [&](){
          if(nwinn>10) return;
          if(ARRAY[na].s.ended()) {
            if(!ARRAY[na].s.lost){
              tpl<int,int> p=mt(ARRAY[na].s.nactive, ARRAY[na].s.nshoot);
              if(!S.count(p)) {
                nwinn++;
                S.insert(p);
                int s = score(ARRAY[na].s);
                if(s>movScore&&s>bestScore){
                  bestScore=s;
                  bestCp=cp;
                  bestI=ARRAY[na].from;
                  bestM=ARRAY[na].m;
                }
                // cerr << C_BRED << "winnable " << p << " " << score(ARRAY[na].s) << C_RESET << endl;
              }
            }
            return;
          }
          if(ARRAY[na].i<(int)M.size()) {
            // assert(ARRAY[na].j<M[ARRAY[na].i].y().size());
            // assert(M[ARRAY[na].i].y()[ARRAY[na].j]<T.size());
            H[na] = ((T[M[ARRAY[na].i].y()[ARRAY[na].j]]
                      - floor(sqrt(ARRAY[na].s.player.dist2(M[ARRAY[na].i].x()))/1000.0)
                      + ARRAY[na].s.nactive)
                     // * 1024
                     // + ARRAY[na].s.nshoot
                     );
            na+=1; nnew+=1;
          }
        };
        // shoot
        { ARRAY[na]=BEAM[cp][i]; ARRAY[na].from=i; ARRAY[na].m=mov(M[ARRAY[na].i].y()[ARRAY[na].j]);
          if(ARRAY[na].s.update(M[ARRAY[na].i].y()[ARRAY[na].j])) {
            ARRAY[na].j+=1;
            while(ARRAY[na].i<(int)M.size() && ARRAY[na].j==(int)M[ARRAY[na].i].y().size()) {
              ARRAY[na].i+=1;
              ARRAY[na].j=0;
            }
          }
          ARRAY[na].s.nshoot+=1;
          addNew();
        }
        // move to next
        { ARRAY[na]=BEAM[cp][i]; ARRAY[na].from=i; ARRAY[na].m=mov(M[ARRAY[na].i].x());
          moveTo(ARRAY[na].s.player, M[ARRAY[na].i].x(), 1000); ARRAY[na].s.update(-1); addNew();
        }
        // move to enemy / dp
        { enemy const* e=0; has_enemy(BEAM[cp][i].s, M[BEAM[cp][i].i].y()[BEAM[cp][i].j], &e);
          { ARRAY[na]=BEAM[cp][i]; ARRAY[na].from=i;
            pos mTo=e->xy;
            ARRAY[na].m=mov(mTo);
            moveTo(ARRAY[na].s.player, mTo, 1000); ARRAY[na].s.update(-1); addNew();
          }
          { ARRAY[na]=BEAM[cp][i]; ARRAY[na].from=i;
            pos mTo=ARRAY[na].s.datas[e->tgt].xy;
            ARRAY[na].m=mov(mTo);
            moveTo(ARRAY[na].s.player, mTo, 1000); ARRAY[na].s.update(-1); addNew();
          }
        }
        // move random
        FOR(ii, (W-4)){
          int d = 998.0;
          double a = randomDouble()*2*M_PI;
          pos mTo(BEAM[cp][i].s.player.x+d*cos(a), BEAM[cp][i].s.player.y+d*sin(a)); mTo.clamp();
          ARRAY[na]=BEAM[cp][i]; ARRAY[na].from=i; ARRAY[na].m=mov(mTo);
          moveTo(ARRAY[na].s.player, mTo, 1000); ARRAY[na].s.update(-1); addNew();
        }
        // FOR(ii, (W-4)/2){
        //   int d = random(500);
        //   double a = randomDouble()*2*M_PI;
        //   ARRAY[na]=BEAM[cp][i];
        //   pos p = M[ARRAY[na].i].x();
        //   pos mTo(p.x+d*cos(a), p.y+d*sin(a)); mTo.clamp();
        //   ARRAY[na].from=i; ARRAY[na].m=mov(mTo);
        //   moveTo(ARRAY[na].s.player, mTo, 1000); ARRAY[na].s.update(-1); addNew();
        // }
      }
      nb = min(na, K);
      iota(I,I+na,0); random_shuffle(I,I+na);
      partial_sort(I,I+nb,I+na,[&](int i, int j){
          return H[i]>H[j];
        });
      FOR(i, nb) BEAM[cp+1][i]=ARRAY[I[i]];
      // cerr << cp << " " << C_BBLUE << na << " " << nb << C_RESET << endl;
      na=0; cp+=1;
    }
    // cerr << C_BBLUE << cp << " " << bestScore << C_RESET << endl;
    if(bestScore>movScore) {
      vector<mov> ms;
      do {
        ms.pb(bestM);
        bestM = BEAM[bestCp][bestI].m;
        bestI = BEAM[bestCp][bestI].from;
        bestCp -= 1;
      } while(bestCp!=-1);
      reverse(all(ms));
      newScore = bestScore;
      return ms;
    }else{
      newScore=movScore;
      return moves;
    }
  }



  int gturn=0;
  int tlife=-1;
  int ne=-1,nd=-1;
  vector<mov> curSeq; int curScore=-1;
  double t00;
  mov step(state const& s0) {
    gturn+=1;
    int turn=0;
    strategy lastP;
    vector<mov> movseq; int movseqScore=0;
    t00=-1;
    state s = s0;
    if(gturn>1&&s.nactive>30&&s.senemies>30) {
      mov mv=curSeq.back(); curSeq.pop_back();
      cerr << "Score: " << C_YELLOW << curScore << C_RESET << endl;
      return mv;
    }
    while(1) {
      turn+=1;

      double t0 = timeInfo.getTime();
      if(turn==1) t00=t0;
      bool skip = (gturn==1) ? t0>t00+0.76 : (t0>t00+0.055 || (turn>1&&s.senemies>30&&s.nactive>30));

      int reduceE[ne];
      FOR(i, s.senemies) reduceE[s.enemies[i].id] = i;

      vector<strategy> ps;

      auto makeP = [&](pos dir) {
        dir.clamp();
        strategy p; p.param0=4; p.param1=0; p.doMoveTo=dir!=s.player;; p.moveTo=dir;
        state t = s;
        while(!t.ended() && t.player != dir) {
          moveTo(t.player, dir, 1000);
          t.update(-1);
        }
        int e[s.senemies]; FOR(i,s.senemies) e[i]=0;
        FOR(i, s.senemies) {
          int ne = next_killing_enemy(t);
          if(ne == -1) ne = closest_enemy(t);
          p.addOrder(ne); assert(e[reduceE[ne]]==0); e[reduceE[ne]]=1;
          while(!t.ended() && has_enemy(t, ne)) {
            t.update(ne);
          }
          if(t.ended()) break;
        }
        FOR(i, s.senemies) if(!e[i]) p.addOrder(s.enemies[i].id);
        ps.pb(p);
      };

      auto makeP2 = [&](pos dir) {
        dir.clamp();
        strategy p; p.param0=4; p.param1=0; p.doMoveTo=dir!=s.player;; p.moveTo=dir;
        state t = s;
        while(!t.ended() && t.player != dir) {
          moveTo(t.player, dir, 1000);
          t.update(-1);
        }
        int e[s.senemies]; FOR(i,s.senemies) e[i]=0;
        FOR(i, s.senemies) {
          int ne = next_taking_enemy(t);
          if(ne == -1) ne = closest_enemy(t);
          p.addOrder(ne); assert(e[reduceE[ne]]==0); e[reduceE[ne]]=1;
          while(!t.ended() && has_enemy(t, ne)) {
            t.update(ne);
          }
          if(t.ended()) break;
        }
        FOR(i, s.senemies) if(!e[i]) p.addOrder(s.enemies[i].id);
        ps.pb(p);
      };

      if(!skip) {
        if(turn!=1) { ps.pb(lastP); }
        makeP(s.player);
        makeP2(s.player);
        FOR(i, 16) {
          makeP(pos(s.player.x+999*cos((double)i*M_PI/8.0), s.player.y+999*sin((double)i*M_PI/8.0)));
        }
        FOR(i, 16) {
          makeP(pos(s.player.x+1998*cos((double)i*M_PI/8.0), s.player.y+1998*sin((double)i*M_PI/8.0)));
        }
      }

      auto initialInterception = [&](state const& s, enemy const& e, pos& res) -> bool {
        int ns=(e.life+13)/14;
        pos epos=e.xy;
        pos tpos=s.datas[e.tgt].xy;
        vector<pos> ps;
        while(epos != tpos) {
          ps.pb(epos);
          moveTo(epos, tpos, 500);
        } ps.pb(tpos);

        // test if already in range !
        int nr=0;
        bool ok=1;
        FOR(i, ps.size()) {
          int di=ps[i].dist2(s.player);
          int d=0;
          FOR(i,14) if(di<=damageDists[i]) d=i+1;
          if(di<SQ(2000)) ok=0;
          nr+=d;
        }

        // check if can be reached !
        if((int)ps.size()<ns) return false;
        { int i = ps.size()-ns;
          pos p1=ps[i];
          pos p2=ps[i+ns-1];
          const int RES=4;
          FOR(k,4*RES) {
            int c1=k-RES;
            int c2=3*RES-k;
            pos middle((c1*p1.x+c2*p2.x)/(2*RES), (c1*p1.y+c2*p2.y)/(2*RES));
            auto test = [&](int v){
              pos p3=middle; moveTo(p3, s.player, v);
              int nr=0;
              FORU(j, i, i+ns-1) {
                int di=ps[j].dist2(p3);
                int d=0;
                FOR(i,14) if(di<=damageDists[i]) d=i+1;
                nr+=d;
              }
              return nr>=e.life;
            };
            int lo=0, hi=4096;
            if(!test(lo)) continue;
            while(lo!=hi) {
              int mi=(lo+hi+1)/2;
              if(test(mi)) lo=mi;
              else hi=mi-1;
            }
            pos p3=middle; moveTo(p3,s.player,lo);
            if(sqrt(s.player.dist2(p3))/1000 >= i) continue;
            bool ok=1;
            FORU(j,i,i+ns-1) {
              if(p3.dist2(ps[j]) <= SQ(2000)) { ok=0; break; }
            }
            if(ok) {
              res=p3;
              return res!=s.player;
            }
          }
        }
        return false;
      };

      if(turn==1||(s.senemies<=10)) {
        FOR(i, s.senemies) {
          pos r; if(initialInterception(s, s.enemies[i], r)) {
            makeP(r);
          }
        }
      }

      auto interception = [&](int param0, int param1, state const& s, enemy const& e, pos& res) -> bool {
        if(e.xy.dist2(s.player) <= damageDists[param0]) { return false; }
        else if(param1==0) { res = s.datas[e.tgt].xy; return true; }
        else { res = e.xy; return true; }
      };

      auto step = [&](strategy const& p) -> bool {
        bool targeted[100]; FOR(i,ne) targeted[i]=0;
        FOR(i,p.sorder) if(p.order[i]!=-1)targeted[p.order[i]]=1;
        if(p.doMoveTo && s.player != p.moveTo) {
          movseq.pb(mov(p.moveTo));
          moveTo(s.player, p.moveTo, 1000);
          s.update(-1);
          return false;
        }
        int i = p.order[0];
        if(i==-1) i = closest_enemy_(s, targeted);
        enemy const* e=0;
        has_enemy(s, i, &e);
        pos pI;
        if(p.intercept[i] && interception(p.param0, p.param1, s, *e, pI)) {
          movseq.pb(mov(pI));
          moveTo(s.player, pI, 1000);
          s.update(-1);
        }else{
          movseq.pb(mov(i));
          s.update(i);
          s.nshoot+=1;
        }
        return !has_enemy(s, i, &e);
      };

      auto evaluate = [&](strategy const& p) {
        bool targeted[100]; FOR(i,ne) targeted[i]=0;
        FOR(i,p.sorder) if(p.order[i]!=-1)targeted[p.order[i]]=1;
        state t = s;
        if(p.doMoveTo) while(!t.ended() && t.player != p.moveTo) {
          moveTo(t.player, p.moveTo, 1000);
          t.update(-1);
        }
        FOR(i_, p.sorder) {
          int i = p.order[i_];
          if(i==-1) i = closest_enemy_(t, targeted);
          bool eFail=0;
          enemy const* e;
          while(!t.ended() && has_enemy(t, i, &e)) {
            pos pI;
            if(eFail==0 && p.intercept[i] && interception(p.param0, p.param1, t, *e, pI)) {
              moveTo(t.player, pI, 1000);
              t.update(-1);
            }else{
              eFail=1;
              t.update(i);
              t.nshoot+=1;
            }
          }
        }
        int score=0;
        if(!t.lost) score+=(100+3*max(0, tlife-3*t.nshoot))*t.nactive;
        score+=10*(ne-t.senemies);
        if(t.lost) score-=100000;
        return score;
      };

      strategy p=lastP;
      if(!skip){
        int niter=0;
        int best=MIN_INT; strategy bestP;
        int cur;
        int nWithoutImp=0;
        if(turn==1) {
          for(auto const& q : ps) {
            int s = evaluate(q); if(s>best) { best=s; bestP=q; }
          }
          p=bestP; cur=best;
        }else{
          p=bestP=lastP; cur=best=evaluate(p);
        }
        auto trySwap = [&](){
          int i=0,j=0;
          while(i==j) { i=random(p.sorder); j=random(p.sorder); }
          if(i>j) swap(i,j);
          swap(p.order[i],p.order[j]);
          int v=evaluate(p);
          if(v>cur) {
            cur=v; nWithoutImp=0;
            if(cur>best){
              best=cur; bestP=p;
            }
          }else{
            swap(p.order[i],p.order[j]);
          }
        };
        auto doTryMove = [&](pos mTo){
          auto old=p.moveTo;
          bool oldDo = p.doMoveTo;
          p.moveTo=mTo; p.doMoveTo=mTo!=s.player;
          int v=evaluate(p);
          if(v>cur) {
            cur=v; nWithoutImp=0;
            if(cur>best){
              best=cur; bestP=p;
            }
          }else{
            p.moveTo=old; p.doMoveTo=oldDo;
          }
        };
        auto tryMove = [&](){
          int d = 1000+random(3000);
          double a = randomDouble()*2*M_PI;
          pos mTo(p.moveTo.x+d*cos(a), p.moveTo.y+d*sin(a)); mTo.clamp();
          doTryMove(mTo);
        };
        auto tryStay = [&](){
          doTryMove(s.player);
        };
        auto tryIntercept = [&](){
          int i=random(p.sorder);
          p.intercept[p.order[i]] ^= 1;
          int v=evaluate(p);;
          if(v>cur) {
            cur=v; nWithoutImp=0;
            if(cur>best){
              best=cur; bestP=p;
            }
          }else{
            p.intercept[p.order[i]] ^= 1;
          }
        };
        double maxTime = (gturn==1) ? ((turn==1) ? 0.40 : 0.012) : 0.01;
        { while(1) {
            niter+=1; nWithoutImp+=1;
            if(!(niter&15)) {
              if(timeInfo.getTime() > t0 + maxTime) break;
            }
            if(nWithoutImp>100) {
              p=ps[random(ps.size())];
              if(random(2)) {
                p.param0=random(14);
                p.param1=random(2);
              }
              cur=evaluate(p);
            }
            int r=random(16);
            if(r==0) tryMove();
            else if(r==1) tryStay();
            else if(r<=7) tryIntercept();
            else if(p.sorder>1) trySwap();
          }
        }
        p=bestP;
        // cerr << C_YELLOW << niter << " " << best << C_RESET << endl;
        movseqScore=best;
      }

      lastP=p;
      if(step(lastP)){
        lastP.remFirst();
      }

      if(s.player == lastP.moveTo) {
        lastP.doMoveTo=false;
      }

      // cerr << "Elapsed: " << timeInfo.getTime()-t0 << endl;
      if(s.ended()) break;
    }
    int newScore;
    auto movseq_ = (gturn==1) ? improveMoveseq<400,10>(s0, movseq, movseqScore, newScore) : improveMoveseq<128,10>(s0, movseq, movseqScore, newScore);
    if(newScore > curScore) {
      curScore = newScore;
      curSeq = movseq_;
      reverse(all(curSeq));
    }
    if(gturn==1) {
      while(timeInfo.getTime()<t00+0.92) {
        movseq_ = improveMoveseq<80,10>(s0, movseq_, newScore, newScore);
        if(newScore > curScore) {
          curScore = newScore;
          curSeq = movseq_;
          reverse(all(curSeq));
        }
      }
    }else{
      while(timeInfo.getTime()<t00+0.080) {
        movseq_ = improveMoveseq<80,10>(s0, movseq_, newScore, newScore);
        if(newScore > curScore) {
          curScore = newScore;
          curSeq = movseq_;
          reverse(all(curSeq));
        }
      }
    }
    mov mv=curSeq.back(); curSeq.pop_back();
    cerr << "Elapsed: " << timeInfo.getTime()-t00 << endl;
    cerr << "Score: " << C_YELLOW << curScore << C_RESET << endl;
    return mv;
    // for(auto m : movseq_) { m.print(); }
    // cerr << "END: turn=" << turn << " " << "data=" << s.nactive << " " << "shoot=" << s.nshoot << endl;
  }

  void run0(){
    state s; s.read(); s.lost=0; s.updateTgt();
    tlife=0; FOR(ei, s.senemies) tlife += s.enemies[ei].life;
    nd=s.nactive;
    ne=s.senemies;
    int turn=0;
    while(!s.ended()) {
      turn+=1;
#ifndef LOCAL
      if(turn>1) { state ss; ss.read(); }
#endif
      future<mov> fm = async(launch::async, [&](){return step(s);});
      auto res = fm.wait_for(std::chrono::milliseconds(turn==1?999:99));
      mov m;
      if(res == future_status::ready) {
        m = fm.get();
      }else if(!curSeq.empty()) {
        cerr << C_RED << "RECOVER TIMEOUT" << C_RESET << endl;
        m=curSeq.back(); curSeq.pop_back();
      }else{
        cerr << C_BRED << "BAD TIMEOUT" << C_RESET << endl;
        assert(false);
      }
      m.print();
      if(m.type==0) {
        moveTo(s.player, pos(m.x,m.y), 1000);
        s.update(-1);
      }else{
        s.update(m.x);
        s.nshoot+=1;
      }
    }
  }
};

int main(int, char**) {
  ai ia; ia.run0();
  return 0;
}
