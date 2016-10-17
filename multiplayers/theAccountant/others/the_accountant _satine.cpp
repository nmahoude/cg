#include <iostream>
#include <cmath>
#include <cstring>
#include <sys/time.h>

#pragma GCC optimize "-O3"
using namespace std;

#define DIST2(x1,y1,x2,y2)	((((x1)-(x2))*((x1)-(x2)))+(((y1)-(y2))*((y1)-(y2))))

typedef struct Pos_ {
	int id;
	int x, y;
	int stamina;
} Pos;

class Situation
{
private:
	Pos _wolff;
	int _nbData;
	Pos _data[100];
	int _nbEnemies;
	Pos _enemies[100];
	int _nbShoots;
	int _nbKills;
	int _totalStamina;
	
public:
	Situation()
	: _nbData(0), _nbEnemies(0), _nbShoots(0), _nbKills(0), _totalStamina(0)
	{ }
	
	virtual ~Situation() { }
	
	Situation &operator=(const Situation &s)
	{
		if(this != &s) {
			_wolff = s._wolff;
			_nbData = s._nbData;
			for(int i=0;i<_nbData;++i)
				_data[i] = s._data[i];
			_nbEnemies = s._nbEnemies;
			for(int i=0;i<_nbEnemies;++i)
				_enemies[i] = s._enemies[i];
			_nbShoots = s._nbShoots;
			_nbKills = s._nbKills;
			_totalStamina = s._totalStamina;
		}
		return *this;
	}
	
	void init(int wolffX,int wolffY)
	{
		_wolff.id = 0;
		_wolff.x = wolffX;
		_wolff.y = wolffY;
		_wolff.stamina = 0;
	}
	
	bool enemiesAlive() __attribute__((always_inline))
	{
		return _nbEnemies > 0;
	}
	
	int idOfEnemy(int idx) __attribute__((always_inline))
	{
		return _enemies[idx].id;
	}
	
	void addData(int id,int x,int y)
	{
		_data[_nbData].id = id;
		_data[_nbData].x = x;
		_data[_nbData].y = y;
		_data[_nbData].stamina = 0;
		++_nbData;
	}
	
	void addEnemy(int id,int x,int y,int stamina)
	{
		_enemies[_nbEnemies].id = id;
		_enemies[_nbEnemies].x = x;
		_enemies[_nbEnemies].y = y;
		_enemies[_nbEnemies].stamina = stamina;
		_totalStamina += stamina;
		++_nbEnemies;
	}
	
	void move(Pos &pos,int targetX,int targetY,int dist) __attribute__((always_inline))
	{
		if(dist==0) return;
		int d2 = DIST2(pos.x,pos.y,targetX,targetY);
		if(d2<=dist*dist) {
			pos.x = targetX;
			pos.y = targetY;
			return;
		}
		long double d = sqrtl(DIST2(pos.x,pos.y,targetX,targetY));
		long double factor = dist/d;
		pos.x += (targetX - pos.x) * factor;
		pos.y += (targetY - pos.y) * factor;
	}
	
	static int nbDamages(int dist2) __attribute__((always_inline))
	{
		if(dist2<=4082821) return 14;
		if(dist2<=4641588) return 13;
		if(dist2<=5333598) return 12;
		if(dist2<=6206801) return 11;
		if(dist2<=7333489) return 10;
		if(dist2<=8827108) return 9;
		if(dist2<=10874632) return 8;
		if(dist2<=13803665) return 7;
		if(dist2<=18235270) return 6;
		if(dist2<=25477833) return 5;
		if(dist2<=38732002) return 4;
		if(dist2<=67860440) return 3;
		if(dist2<=158988089) return 2;
		return 1;
	}
	
	bool shootEnemy(int idx) __attribute__((always_inline))
	{
		++_nbShoots;
		_enemies[idx].stamina -= nbDamages(DIST2(_wolff.x,_wolff.y,_enemies[idx].x,_enemies[idx].y));
		// Kill enemy?
		if(_enemies[idx].stamina<=0) {
			--_nbEnemies;
			if(idx!=_nbEnemies) _enemies[idx] = _enemies[_nbEnemies];
			++_nbKills;
			return true;
		}
		return false;
	}
	
	void removeData() __attribute__((always_inline))
	{
		for(int d=0;d<_nbData;) {
			bool removed = false;
			for(int e=0;e<_nbEnemies;++e) {
				if(_enemies[e].x==_data[d].x && _enemies[e].y==_data[d].y) {
					removed = true;
					break;
				}
			}
			// No ennemy on this data
			if(!removed) {
				++d;
				continue;
			}
			// Remove
			--_nbData;
			if(d!=_nbData) _data[d] = _data[_nbData];
		}
	}
	
	bool moveEnemies(bool check) __attribute__((always_inline))
	{
		// Enemies moves
		for(int e=0;e<_nbEnemies;++e) {
			int tX = _data[0].x, tY = _data[0].y;
			int minD2 = DIST2(_enemies[e].x,_enemies[e].y,_data[0].x,_data[0].y);
			for(int d=1;d<_nbData;++d) {
				int d2 = DIST2(_enemies[e].x,_enemies[e].y,_data[d].x,_data[d].y);
				if(d2<minD2) {
					tX = _data[d].x;
					tY = _data[d].y;
					minD2 = d2;
				}
			}
			move(_enemies[e],tX,tY,500);
			if(check&&DIST2(_wolff.x,_wolff.y,_enemies[e].x,_enemies[e].y)<=4000000) return false;
		}
		return true;
	}
	
	bool moveWolff(int x,int y) __attribute__((always_inline))
	{
		move(_wolff,x,y,1000);
		for(int e=0;e<_nbEnemies;++e)
			if(DIST2(_wolff.x,_wolff.y,_enemies[e].x,_enemies[e].y)<=4000000)
				return false;
		return true;
	}
	
	bool playWithShoot(int idx,bool &killed) __attribute__((always_inline))
	{
		if(!moveEnemies(true)) return false;
		killed = shootEnemy(idx);
		removeData();
		return true;
	}
	
	bool playWithMove(int x,int y) __attribute__((always_inline))
	{
		moveEnemies(false);
		if(!moveWolff(x,y)) return false;
		removeData();
		return true;
	}
	
	int simule(int nbRandMoves,int radius,Pos tabl[],int &lg) __attribute__((always_inline))
	{
		lg = 0;
		int n = rand()%(1+nbRandMoves);
		for(int i=0;i<n&&_nbEnemies>0&&_nbData>0;) {
			int p = rand()%2;
			if(p==0) {
				int mX = _wolff.x+(rand()%(2*radius+1))-radius;
				int mY = _wolff.y+(rand()%(2*radius+1))-radius;
				if(mX<0||mX>=16000||mY<0||mY>=9000) continue;
				while(_wolff.x!=mX||_wolff.y!=mY) {
					if(!playWithMove(mX,mY)) return 0;
					tabl[lg].x = mX;
					tabl[lg].y = mY;
					++lg;
				}
			} else {
				int r = rand()%_nbEnemies;
				bool killed = false;
				while(!killed) {
					tabl[lg].x = -1;
					tabl[lg].y = r;
					++lg;
					if(!playWithShoot(r,killed)) return 0;
				}
			}
			++i;
		}
		while(_nbEnemies>0&&_nbData>0) {
			int r = rand()%_nbEnemies;
			bool killed = false;
			while(!killed) {
				tabl[lg].x = -1;
				tabl[lg].y = r;
				++lg;
				if(!playWithShoot(r,killed)) return 0;
			}
		}
		if(_nbData==0||3*_nbShoots>=_totalStamina) return 10*_nbKills + 100*_nbData;
		return 10*_nbKills + 100*_nbData + _nbData*(_totalStamina-3*_nbShoots)*3;
	}
	
};

int main()
{
	// Init
	srand(time(0));
	Situation iniSituation;
	int bestPts = 0;
	Pos bestSimulation[128];
	int nDepls = 0;
	// game loop
	for(int t=0;t<400;++t) {
		int x,y;
		cin >> x >> y; cin.ignore();
		if(t==0) iniSituation.init(x,y);
		int dataCount;
		cin >> dataCount; cin.ignore();
		for(int i=0;i<dataCount;++i) {
			int dataId, dataX, dataY;
			cin >> dataId >> dataX >> dataY; cin.ignore();
			if(t==0) iniSituation.addData(dataId,dataX,dataY);
		}
		int enemyCount;
		cin >> enemyCount; cin.ignore();
		for(int i=0;i<enemyCount;++i) {
			int enemyId, enemyX, enemyY, enemyLife;
			cin >> enemyId >> enemyX >> enemyY >> enemyLife; cin.ignore();
			if(t==0) iniSituation.addEnemy(enemyId,enemyX,enemyY,enemyLife);
		}
		while(iniSituation.enemiesAlive()) {
			struct timeval tS,tE;
			gettimeofday(&tS,NULL);
			// Simulations
			for(int k=0;k<10000000;++k) {
				if((k%100)==0) {
					gettimeofday(&tE,NULL);
					int ms = (tE.tv_sec-tS.tv_sec)*1000+(tE.tv_usec-tS.tv_usec)/1000;
					if(ms>(k==0?900:90)) break;
				}
				Situation s = iniSituation;
				Pos currSimulation[128];
				int lg, pts = s.simule(5,3000,currSimulation,lg);
				if(pts>bestPts) {
					bestPts = pts;
					memcpy(bestSimulation+nDepls,currSimulation,lg*sizeof(Pos));
					cerr << pts << " - ";
					for(int i=0;i<lg;++i) cerr << currSimulation[i].x << " " << currSimulation[i].y << " ; ";
					cerr << endl;
				}
			}
			if(bestSimulation[nDepls].x==-1) {
				int id = iniSituation.idOfEnemy(bestSimulation[nDepls].y);
				bool killed = false;
				iniSituation.playWithShoot(bestSimulation[nDepls].y,killed);
				cout << "SHOOT " << id << endl;
			} else {
				iniSituation.playWithMove(bestSimulation[nDepls].x,bestSimulation[nDepls].y);
				cout << "MOVE " << bestSimulation[nDepls].x << " " << bestSimulation[nDepls].y << endl;
			}
			++nDepls;
		}
	}
	return 0;
}
