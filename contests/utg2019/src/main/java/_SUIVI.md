## SUIVI DES SUBMITS ...

#TODO
"the purificateur(red) , seed=5886469136755935200 => on peut eviter de se faire sauter au tour 9 si on evite les mines ...


# GOLD | 11/10
* 02h34 / - : reupdate du positionnement radar à chaque tour
* 02h04 / 49 - : hard code du tour 3 aussi
* 01h40 / bug du follow radar trop tot: ajout d'un follow radar (sans ore obvious)
* 01h20 / 41 : poids moins fort pour un dig random (ca craint quand je fais des dig random)
* 00h56 / 33 - : idem, mais sans les ore obvious
* 00h33 / 43 - 26.29 : correction bug de utility des dig (toujours vers le haut de l'ore :()
* 00H22 / XX- : 2er tours précalculés
* 00h07 / XX - : plusieurs solutions pour le move to dig on target (pas utilisé normalement)
* 23h47 / - 43 : correction du bug en utilisant un world backup pour le think
* 23h33 / - bug : on essaye de retirer les ORE qui seront DIG au prochain tour (sauf celles qu'on va dig ...)
* 23h06 / 12 - 27.92:  correction d'un bug de trapAdvisor
* 22h32 / 72 - : on trade even si x < 3
* 20h34 / 33 - 27.31 : relance
* 18h53 / 55 - 25.99 : current avec la detection des trades de trap, mais pas d'actions
* 18h40 / - : relance 14h26 ... monte pas assez vite ...

	-- LEGEND 80 / 25.37
* 17h06 / ~110 - : relance 14h26
* 16h28 / 79 - 25.65 : on approche du but : c'est l'IA qui classe les microActions
* 16h05 / ~100 -     : trier les move to base pour prendre le plus proche du robot (algo ~v1 ?)
* 15h39 / 191 -      : moveToBase v2
* 14h26 / 56 - 26.72 : repush
* 14h02 / 91 - 25.20 : re-ajout du dig vers les past known, et random en dernier recours
* 13h37 / 80 - 25.57 : revert
* 13h34 / -MARCHE PAS: on prend le pari qu'apres 21, il n'y a plus de trap
* 12h46 / 85 - 25.60 : 1ere iter vers retour 23h57
* 09h03 / 76 - 25.65 : à nouveau 23H57
* 07h33 / 167 -      : relance
* 01h53 / 162 - xxxx : nouveau fix dans les goals
* 01h35 / - ~84      : petit fix dans les goals
* 01h28 / -          : changement dans les goals 
* 00h58 / 92 - 25.11 : suppression du forcage d'arret en 0 mais on laisse le code qui dig plus tard les ore safe
* 00h42 / ~160       : refine de la simulation (pas trop loin, et on fini par prendre les cachées)
* 00h28 / ~120       : on pose/simule des pieges
* 23h57 / 70 -       : again
* 23h34 / 77 - 25.26 : amelioration radar + nouvelle strat dans trapAdvisor 
* 23h08 / 155- 23.36 : nouveau systeme de pose des radars
* 21h44 / 73 - 25.52 : revert d'une partie pour aller vers code de 13h14
* 19h00 / 71 - 25.71 : code de 13h14 ..
* 18h34 / 152- xxxxx : code de 13h14 (70)
* 17h45 / ~140       : ajout des mouvements pour le prochain DIG quand on retourne à la base (PAS VERIFIE !)
* 17h36 / xxx        : plus de retour à 10 tours de la fin
* 16h55 / 129 -23.33 : pleins de changements, pas sûr. NE PAS JETER L EAU DU BAIN
* 13h14 / 70 - 25.34 : correction sur le trapAdvisor
* 12h17 / 97 - 24.14 : wallDetector (en wip) + evol 'if digged then safe' sur les traps
* 07h35 / 77 - 25.17 : relance avec debug off ...

# GOLD | 09/10
* 01h45 / 130: 
* 01h25 / 130 22.66 : resuppression de l'opti ETA pour check
* 01h05 / 92 - 24.32 : remise en place du ETA pour le dig (il faut revenir à a base aussi)
* 00h36 / 68 - 24.91 : suppression evol du move avant d'arriver à la base (pour se mettre en bonne position pour suivant)
* 00h11 / 162 : correction d'un bug sur radar+trap simultanésé
* 23h49 / ~160(70%): idem, mais avec correction du TRIGGER
* 23h02 / 209 : modification eta du DIG (pour prendre en compte le retour ...)
* 22h32 / 174 : remise du bug sur les digs (des fois que ...)
* 21h54 / 132 : essayer de se rapprocher du next point dans les move
* 21h25 / 165 : correction bug "new hole" + resubmit
* 18h00 / 62 - repush apres passage en gold

# Silver | 09/10
* 13h51 / : revert sur la utility des radars
* 13h29 / 400 : correction bug random & remise radar static
* 13h13 / 200 : coeff plus faible pour la proportion (radarOptimize, 0.25)
* 12h55 / 200 : radar avec score & repartition
* 12h18 / 121 : rework radar (pas encore actif)
* 09h10 / 104 (puis 80) : avec ambush cell radius 1
* 08h02 / 127 : ne plus se faire exploser sur des traps improbables (seulement une cellule), sans bug
* 08h00 / bug teams : ne plus se faire exploser sur des traps improbables (seulement une cellule)
* 07h21 / 80 : remise en place des radars statiques (mais mieux disposé)
* 06h55 / 110 : relance sans changement
* 01h29 / 112 : bug réparé,il ne faut pas mettre l'utility de wait à -infini .... (bug?)
* 01h23 / BUG : Ajout des msgs debug & state FINISH
* 00h44 / 106 : toutes le colonnes pour les TRADE & anti SKRIL : ne pas se laisser berner par les trap qui retournent en col 0 (y'a pas de score++ :) )
* 00h13 / 100 : ajout de la detection des traps + explosion si col 1 & interessant

# Silver | 08/10
* 22h34 / 138 : poids sur les ore potentiels à 25 
* 21h41 / 138 : plus de poids (49) sur les ore potentiels 
* 18h28 / 106 : raffinage trapAdvisor
* 17h56 / 118 : relance coeff 0.5
* 17h41 / ~200 : relance (coef 2)...
* 17h39 /  : relance (coef 2)... + WIP on TrapAdvisor :(
* 17h18 / 135 : relance 16h38 (coef 1)...
* 16h57 / 172 : rework encore sur la pos des radars (coef 4)
* 16h38 / 139 : le dig known ore ne prenais pas en compte qu'on ne connaissait pas les cells ...
* 16h30 / : revue des radars again (ne pas laisser de trous en debut de board)
* 16h00 / 166 : correction du need radar (je ne comptais pas les cellules dangereuses)
* 15h33 / 200 : commit + correction utility des dig lors du choix
* 14h49 : comit : amelioration calcul ETA
* 14h34 : submit (radar entierement placés sur l'algo du best spot)