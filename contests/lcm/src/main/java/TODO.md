□ ☑  ☒  ☓  ✓  ✓  ✕  ✖  ✗  ✘ 
------------------------------------------

□ simulation avec des cartes dans sa main
	-> toutes les affordables ?
	-> une selection
	-> random ?
	
  => multiplier les scores du MINIMIZER par un % de probabilités, prendre le meilleur ?

□ revoir value de #14 (29  pour une (4)9/1)

□ BeamSearch : verifier le chrono à chaque tour de minimizer

□ Aggressor ou stabilizer (il faut jouer agro ou board control) 
		-> à updater à chaque tour !
□ valeur des cartes pour un jeu board control ?
		voir si il faut aussi eval & cardpicker
		
		
□ Prendre en compte les runes ?
	-> ne pas donner de freecard si il en a plus bcp et  
	-> if you have boardControl and don't run for lethal, don't break a rune
	=> y'a un truc à faire !
	
□ P2 : donner de la valeur aux cartes guard & suppress quand on est joueur 2 ?
□ P2 : décaller la manacurve / changer l'eval ?

		
□ mana curve -> implémenter la gaussienne 
□ mana curve -> method manuelle avec les decomposition  ? (6 eq (3+3,2+2+2, 4+2, 5+1)
 
□ Eval : malus sur le break des runes et que je suis en retard sur le board ou que je joue board control

□ Lethal score : en fonction du board ou du turn ???
+ Breakthrough : idem que lethal, plus la diff est grande, mieux c'est


□ Comme on peux avoir un endturn après chaque noeud, autant eval chaque nouveau noeud directement. Si l eval est trop pourrie on peut pruner le noeud ou le downgrade en proba d être rejoué ( comme du mcts) ?


---------------------------------------------
☒ Optimisation du hash des action pour la map des noeuds ?

☑ Update des noeuds de fins.
Si on a déjà calculé un noeud de fin on ne recommence pas et on ne le prends pas en compte dans le top 20 🔥🔥🔥

☑ Mise au point du zobrist hash

☑ Output du meilleur score toutes les 5 ms => est ce qu on améliore significativement ? -> non !

☑ Prendre des coups des tops player et les replayer  => scoring par rapport au coup que j aurais calculé (en 100ms, en 10s ? ). Ca donnera une indication sur mon eval et son tuning. 🌡

☑ Zobrist : Essayer de merger les noeuds a faible depth.  Si b+a équivalent à a+b il faut absolument merger !









TODO : faire une premiere simu, all on HERO !
TODO : ajouter un bucket item :)
TODO : merger les états identiques dans le MCTS !

* IDEES DE PRUNING
	* idées potables
		
	- si on fait A+ B et que c'est équivalent à B+A !
-	
	* idées débiles
	
	- carte rouge avec des suppression only sur une carte qui n'a rien ...
	- carte verte avec ajout only qui met sur un truc qu'on a déjà ?
	
	


* Ameliorer le retaliate 
	-> si on a un guard, on essaye de le killer pour ensuite face
	-> si deux, ....
	
	
	
* Trouver un moyen de vérifier la simu completement !
	=> Coder un test de bout en bout ! 
	=> Comment on gère la simu coté opponent ??? (cf SimulationEndToEndTest)
	
== PICKER
☑ Revoir le pick : mana curve & beaucoup de choix mauvais
	=> CardPickerV2
	
== BATTLE
☑ Joueur toutes les cartes summon/use en 1er => 2 phases
  ==> NOGO cas particulier : il y a 6 cartes sur le board !
  
☑ Creer une action ENDTURN qui met fin au turn, toujours disponible !

* Faire un dummy 'retaliation' qui est une simple heureistique pour voir la next action du joueur

* est-ce qu'il est possible de faire 1.5 tour, 2 tours ?

* remplacer le MC par autre chose ?
    -> bruteforce, BFS, DFS, beam search, ..... ?

* Avoir différent 'mode de jeu' (différentes eval) pour board control, aggro, balance ...

* l'eval3 semble privéliger les guard sur l'attaque, à vérifier & réduire un peu    

* inverser le score si on a trop de HP par rapport aux runes et qu'on manque de cartes ?

== TECHNIQUE
* rendre les cartes reellement immutables ?
* distinguer cartes & creatures ?
* transformer les card on board en creature et les coder avec un 'int' !

== PERF
* Toujours savoir si il y a des guards pour un joueur parce que ca coute cher de chercher (int guardCount, si on pose ++, si on tue --) 
* Toujours savoir le nombre de cartes sur le board pour chaque joueur ! (raison idem si dessus)
* Remplacer l'appel à Action.xxx à une recuperation du cache
* verifier pourquoi les compute sont si long
* Tester unsafe & bytebuffer pour les caches, mais surtout pour les actions



== EXPLORE
1. (REF ?)
• Minion advantage (MA): number of minions the player controls over her opponent.
• Tough Minion advantage (TMA): number of powerful minions the player controls over her opponent.
• Hand advantage (HA): number of hand cards the player has minus her opponent’s hand cards.
• Trade advantage (TrA): factor that represents how good the minions on the board are to lead to
advantageous trades.
H(s) = 1.75 ∗ MA(s) + 2.50 ∗ TMA(s) + 1.00 ∗ HA(s) + 3.75 ∗T rA (s).

