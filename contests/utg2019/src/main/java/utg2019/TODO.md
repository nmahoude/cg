# Thinking ...

## SEEDS :
	mines à droite : 	seed=5922412675218315300,  
						seed=6211780854455674900
					    seed=4191708785142069800 (vraiment loin la)
					    
	Fluxor bleu + seed=-4977201844970576900 => trade négatif vers tour 83
	Gevater_Tod4 : malade des bombes :)
	Blasterpoard, Askelas : pas de traps
	JohnRambo : wall
	Avatar256, seed=1893654010916920830 : frame 30 je le crois pas dangereux (FIXED)


Jooo (red) + seed seed=-1533099402375977220 vers 150

### PRIORITE 1 : implementer les micro mouvements multiples
	Objectif : eviter les traps à plusieurs 

1. avoir un liste de micro actions, avec une seule action (celle actuelle)
2. renvoyer toutes les microactions qui sont optimales pour le deplacement, en choisir une au hazard
3. tester les microactions pour leur resultats par rapport aux traps (ATTENTION IL FAUT UN ENEMI DANS LA ZONE)
	a. si on a une micro actions hors explosionMAP, on la choisie
	b. si elles sont toutes dans une explosionMap,on en choisit une et on la tag comme dangereuse (on est encore à 1vs1)
		un 2eme robot devra l'éviter sans impossibilité
	c. faire un brute force sur les actions
	
4. renvoyer plus de microactions (meme pas opti) pour laisser des choix si on veut vraiment eviter ls chaintraps (mais on perd du temps)


















### Wall of traps
	example : hyphz, seed=211633165087746560
	TODO : si on peut peter le wall pour un échange 1 vs 1, il faut le faire
	TODO : Comment detecter quels sont les mines petee ?
* J'ai l'impression que ca donne un avantage plus que conséquent :
 => contre 1 robot +/- bloqué à mettre des bombes, il peut ralentir tous les membres de l'autre equipe (un seul peut passer)
 => si en plus on apporte le minerai dans la derniere tranche, on pourrait l'utiliser de temps en temps pour ramener de l'ore ?

### Detection of trap chains
	Reussir à supprimer les traps une fois qu'elles ont explosées !
	
	MTM
	 T
	 TH     <- si je suis mort au prochain tour, je sais que les 3 traps du haut ne sont plus la
	 T 	              (seule solution pour expliquer)
	
### Dernier mouvement avant DIG :
	Si on doit faire un dernier mouvement avant de dig, il faut le choisir pour nous mettre un autre dig potentiel
	=> si il est dig par l'ennemi, on aura alors une solution de secours
	=> il ne faut pas que ca nous coute un deplacement de plus (ni pour repartir !)	

### ORE Clusters
	Comment profiter du fait que l'ore est distribuée en clusters
	=> est-ce qu'on peut en déduire quelquechoses sur les 'veines'
	Exmple : je place mon radar en (7,9) et je ne vois que de l'ore en (4,11), (4,12) qui ramene le filon vers le début(x=0)
		=> il faut que je pose un radar pour check cette zone, meme si c'est contre intuitif


### First turn gamble
	Tous les robots font un wait le 1er tour, et ils ne dig que si ils sont cachés 
	(ie : on ne peut pas detecter si ils ont laché une bombe)
	on peut le faire en milieu de game aussi : si on voit qu'on va dig sur une case propice, alors on fait un stop sur le 0
	et on prend (ou pas) une trap!
	
### Follow radars
	Si on a rien à faire, et que le dig random rapport pas enormement, on peut follow le radar pour etre dans son rayon au moment où il le posera, ca permet d'opti la recherche 	 
	 
	 

### Eviter les pieges
* -DONE- Construire une explosionMap qui simule l'explosion de chaque cellule dangereuse et donne aux zones des ids
* ne pas avoir 2 robots dans la meme zone (path finding ? simple micro actions ?) dans tous les cas, les microActions sont nécessaires

### DIG RANDOM
* Il y a des dig random tres bizarre alors qu'on pourrait poser des RADAR ou des TRAPs...


### Extra radar
* si on est en col0 et que notre goal est dig target, et que la target a une bonne couverture et qu'il n'y a pas un radar incomming (un qui retourne à la maison)
* 	=> prendre un radar peut etre avantageux !

### Premiers tours
Regarder des parties du top pour savoir les actions dans les 1ers tours (hard codées ?)
	-> radar, mine, random, feintes de mine ...
	-> follow radar plutot que random ?


### Risques
* Prendre plus de risque si tous les robots font un wait au tour 0 ... ?
* Si desperé, il faut prendre plus de risque (fin de partie, en retard ...) => coefficient de sécurité (0 -> 1 (+ secure))

### Placer des trap
* quels sont les criteres pour les bonnes trap ?
* => indetectables (via les trous déjà presents ?)
* => bien placer pour "proteger" du minerai 
*    => notion de minerai proteger, on peut le garder pour plus tard ????

### Trap les ennemis
* si on pense que c'est un good trade, on peut ttrigger les trap si un ennemi (ou plusieurs) est à coté. pré-requis : poser des pieges
* Baser sur la potentialTraps, on eput également tenter de tuer l'ennemi avec ses propres traps ....

### Detecter le FARM ennemi
* si on fait une heatmap, on peut detecter le farm et trouver des zones sympa sans radar
* On cherche les robots qui font un stay sur l'aire de jeu (pas forcement dangereux)

### Ronde / Soldat
* Utiliser un robot qui fait la ronde sur la bande d'arrivée et explose ce qu'il peut
* Utiliser un robot avec une trap sur lui pour pieger 2 robots ou plus

	