# TODO

## Seeds
2 ugly au fond sur les cotés : seed=-7617258148391018000
2 uglies bien profond (ne devraient pas géner): seed=6210846707932643000
2 uglies au centre : seed=-4120664220188534000

4 uglies au centre : seed=647059406402761500

6 uglies: seed=1270410807845496000
6 uglies en triangle : seed=-6592011069702663000

je me fais chopper ! seed=6547777459243405000

## GET BACK (pour les prochains)
- [ ] dessiner les distances & afficher x,y
- [ ] zone pour le score
- [ ] pouvoir faire un copier depuis le replayer (pour le viewer !)

## UX / Analysers
- [ ] récupérer toutes les last battles, afficher victoire/defaite et pouvoir lancer un Replayer
- [ ] faire une analyse des defaites ! (remontée trop tard, deny de poisson, rencontre avec ugly ...)


##  Big ideas
- [ ] quand on découvre un poisson et qu'on l'ajoute par symmetrie, il faut vérifer le code qui l'ajoute au tour en cours (via les discoveredFishes)
- [ ] Le minimax fonctionne, mais il faut bien l'utiliser (quand  on sait qu'on a perdu meme en ramenant les fish en 1er, il faut faire autre chose (kill fish)
- [ ] il faut encore plus profiter de la symmétrie pour detecter les poissons
- [ ] il ne faut pas remonter au 1er T3 trouvé .... (Minimax pour la décision)
- [ ] plus aggressif sur les killfish (plus que 1 tour ?)
- [ ] en situation déseperée, envoyer un bot pour lure UGLY sur lui et un autre pour kill fish ?
- [ ] Est-ce qu'il y a un moyen d'éviter le deny ???




## Triangulation
- [ ] quand on voit un poisson pour la 1ere fois, est-ce qu'on peut remonter le temps pour trouver son symétrique ?
- [X] si opp vient de scanner un fish et que notre potentiel fish n'est pas dans son rayon, il faut oublier
- [ ] ne pas faire bouger les ugly au début ! (tant que personne n'est dans leur rayon de detection)
- [ ] quand on a vu un fish et qu'on ne le voit plus, son déplacement fait que sa position potentielle n'est pas un rectangle, mais un cercle !
- [ ] quand la simulation d'un poisson ne tombe pas dans la triangulation, oublier ?

- [X] Quand on est sensé voir un poisson et qu'il n'est pas là, il faut rebasculer sur la triangulation !
- [X] les poissons peuvent aller à 400u si ils sont effrayés ! => vérifier si il est potentiellement effrayé pour expand la zone
- [X] Triangulation: 
	ajouter la triangulation avec la position de nos drones + la lampe si on l'a mise (pareil pour lui) : 
		si il a découvert un poisson  -> il est dans le range,
		si il n'a pas découvert de poisson (ou qu'il l'avait déjà! (current & full scans) il n'est pas dans le range
- [X] se servir de la diff de ses currentsScans à lui pour réduire le champs des possibles pour la position des poissons
- [X] detecter les poissons disparus via le no blip


## Strategy
- [ ] à distance +/- équivalente, il faut privilégier le poisson qui complete des lignes/colonnes !
- [ ] si on deny 2 poissons alignés (row), on peut arriver dernier partout ????
- [ ] si on deny 2 poissons pas alignés, on peut arriver dernier partout ????
- [ ] ne pas pousser les poissons hors du jeu si on a pas de batterie pour les voir
- [ ]  choisir les poissons qui rapportent des points sur la 2eme descente (pas forcement ceux du fond !)
- [ ] si le drone n'a pas de poisson commun avec nous ou si notre wingman est plus haut et les a, on a le temps!
- [ ] quel poisson choisir ? quels poissons viser ?
- [ ] si on a plus rien à faire, il faut aller pousser les poissons qu'il n'a pas ! et attirer des ugly vert lui
- [ ] pousser les poissons hors de la carte
	- [ ] etre prudent quand on attaque les poissons qui sont sur l'exterieur (si on ne les a pas)
	- [ ] ne pas faire fuir les poissons qu'on a pas encore ...
	- [ ] pousser les poissons en dehors de la map quand on les a récupéré


- [ ] attention au taux de couverture pour allumer la light quand on a plus trop de batterie
- [ ] Ne pas forcement remonter quand on a notre cible si il y a un autre poisson à proximité
- [ ] Team work
- [ ] interaction avec opp, notamment sur les Ugly
- [ ] Evaluation : what is good ???
- [ ] prévoir la mouvement des poissons ? à peu pres ?
- [ ] remonter pour securiser des fishs ou continuer à chercher si on est déjà pas mal enfoncé ?
- [ ] essayer de ne pas garder les 2 drones l'un pres de l'autre  (pour maximiser la triangulation)

- [X] allumer la lighe en remontant aussi 
- [X] quel avantage a essayer d'avoir une vision des poissons (tout type ?)


## State
- [X] revoir la triangulation from BLips quand on ne trouve pas d'intersection (updateTriangulationFromBlips)
- [X] Evaluer le nombre de tour (à 0, -600) pour remonter à la 'surface'. S'en servir pour évaluer qui remonte en 1er ...
- [X] quand on voit un fish, et qu'il n'y a "personne" autour, on peut prédire son mouvement pour un certain temps
- [NO] quand on voit un fish, on sait qu'il n'y en a pas dans les 200 u ??
- [X] conserver qui a eu les coupes en 1er !

## Simulator
- [ ] faire plusieurs tours pour echapper aux UGLYs
- [ ] comment savoir si un drone ennemy va bouger pour ne plus etre dans le rayon d'un UGLY ?? 
		Predict opp moves ?
		->  remove opp from equation ?(pour l'instant)
- [X] faire comme si les drones enemis n'existaient pas pour simuler les next tours (pas pour la simulation from previous)
- [X] faire 2 tours pour echapper aux UGLYs

## Game
- [NO] renumeroter les fishs ?? (dangereux, mais plus simple)
- [X] scanner les intersections sur plusieurs tours
- [X] echapper aux uglies (descente)
- [X] renuméroter les drones pour plus de simplicité (toujours etre 0 & 1)
- [X] echapper aux uglies (remontée)
- [X] gérer 2 drones
- [X] Packer les inputs
- [X] lire les inputs packés

## Maths
- [ ] Cropping in ne fonctionne pas
- [ ] Tout remplacer par le gridMaster ? mais difficile de faire les intersections ???
- [ ] Rectangle -> Polygon ? ou multi rectangle ?
- [ ] intersection cercle rectangle : quel %  du rectangle ça fait (pour les probas de trouver les poissons si on light
- [X] introduce Zone (Rectangle or GridMaster !)
- [X] intersection d'un cercle et d'un rectangle => plus petit rectangle inscrit (qui contient tous les points du cercle)
- [X] intersections de rectangles (triangulation)
- [X] Prendre en compte les lignes de nages 

## Viewer
- [ ] afficher les poissons dans already & current plus distinctivement (petit rond aujdhui)

- [X] Mettre la bonne couleur des drones en fonction P1, P2
- [X] afficher l'etoile sur les poissons
- [X] afficher les 2 types de "coupes"
- [X] copier/coller un "input" dans le viewer
- [X] afficher les poissons dans le bon ordre
- [X] afficher intersection des bads
- [X] afficher le radar quand on survole un poisson
- [X] afficher le range correct des drones