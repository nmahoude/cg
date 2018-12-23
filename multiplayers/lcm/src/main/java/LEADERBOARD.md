
= LEADERBOARD


== LEGEND
23h08 - 10 / 25.32 - le 21h56
21h56 - 11 / 24.79 -  
19h19 - 11 / 25.01 - (1.2, 0.8)
18h39 - 15 / 24.58 - compare avec ref, +5% avec changement de coeff (0.8,1.2)
18h38 oups
17h23 - 14 / 24.64 - malus = 10*depassement, modif manacurve
16h31 - 16 / 24.62 - 1er test avec le nouveau picker -> courbe ok
15h59 - 20 / 24.04 - remise etat ~ 13h13
13h45 - 17 / 24.59 - version de la veille 13h13 (was 12/24.62)
13h12 - 20 / 24.18 - v23h05
12h43 - 36 - eval8 & pickerv2
11h52 - 26 / 23.60 - amelioration eval en local
11h15 - 35 - 8h30 d'hier (avait fini 11/24.44)
10h21 - 22 / 23.74 - malus atk si il y a un guard (1.8 & 2.0) (64%, 42%)
09h52 - 25 / 23.60 - eval9 from scratch (67%, 25%)
09h20 - 35 / 22.71 - back to eval6 :(
08h51 - - code de 19h09
08h25 - 63 à 72% :(- code de 13h13
07h51 - 18 / 24.02 - code de 23h05
02h08 - 19 / 24.33 - code de 19h09
01h38 - 21 / 23.66 - handEval = 0.5
00h58 - 23 / 23.67 - remise coeffs à 19h09 (reste le face au tour 8+)
00h28 - 21 / 23.83 -  
23h05 - 12 / 24.47 - coeff handCardCount à 7
22h07 - 13 / 24.26 - quelques trucs modifiés 
19h41 - 15 / 24.23 - mana<4 +10, charge +5, guard +5
19h09 - 10 / 24.72 - mana<4 -> value+20
15h55 - 20 / - remise de la card values
15h21 - 16 / 23.92 - remise de la vie pow(x, 0.3)
14h15 - - -5 pour mana & nouvelle card values, oups sur la vie
13h13 - 12 / 24.62 - décalage 20 !
12h40 - 12 / 24.65 - décalage de 10 si la mana cost <4
					 41 / 40 (win matchs : p0:104 et p1:59 pour 294 )
12h10 - ~32 - Mes valeurs de cartes -> ko
08h30 - 11 / 24.44 - ajout de la fatigue
02h16 - 20 / 23.96 - 0.8, 1.1 + 1.1, 0.8 sans la decimate
01h49 - 28 / 23.24 - 0.8, 1.1 + 0.8, 1.1
01h18 - 21 / 23.81 - 0.8, 1.1 + 1.1, 0.8 
00h50 - 26 / 23.25 - 1.1, 0.8 + 1.1, 0.8
00h20 - 29 / 23.10 - 1.1, 0.8 + 0.8, 1.1

23h19 - 24 / 23.39 - remise à 0.8,0.6 et 0.6,0.8 (tjs hand < board)
22h44 - 35 - score de hand < score de board count
21h49 - 21 - un bug corrigé sur le comptage des guards
20h42 - - code à jour, cardpickerV2
19h01 - 28 / 22.74- code de 13h43
18h08 - XX - 12h28 d'y il y 2 jours ...
16h30 - 18 / 23.91 - relance code 12h09
16h02 - ~22 - code de 13h43
15h32 - 18 / 24.30 - retest avec 12h38
14h48 - 23 / 23.57 - cl v1 (13h43)
14h13 - 20 / 23.95 - derniere version des cartes de closetAI :(
13h43 - 16 / 24.40 - reevaluate based on closetAI - grosse montée, mais stabilisé à moins :(
13h02 - nogo- cardPickerV4 avec mes cartes calculées
12h38 - ~25 - la meme avec le cardpickerv2 .. Pas bon resultats
12h36 - xx - augmentation des coeff cartes
12h09 - 15 / 24.52 - relance du code de 02h00 pour check
02h00 - 14 / xx - relance cardpickerv2
01h12 - 15 / 24.22 - restore -10 sur mana
00h36 - 27 - optimal now (oups no)
00h31 - nextCardDraw à 10 :( - optim de quelques coeffs
00h20 - (oups)
18h45 - 16 / 24.06 - nouveaux poids issues du offline
13h53 - 18 / 23.79 - test avec plus d'aggro pour moi
12h28 - 16 / 23.80 - test avec code à jour et depth 2
10h40 - 21 / 23.25- code de 18h04
10h07 - 19 / 23.33 - code de 21h39
09h42 - 57 ? - version de 23h02
08h37 - 34 ! - version avec remaximization de l'etat final
08h13 - 20 / 23.37 - version ~ 23h02
02h30 - 52! - sans minimizer (est-ce qu'il est bon ?)
01h56 - 25 / 22.88 - tentative de clock ...
01h29 - 22 / 23.25 - code de 13h30 (qui etait le code de 23h30 du jour d'avant)
01h00 - 19 / 23.37 - modification des coeff avec personalTester (+10 / ref):)
23h47 - 0.85 / 0.8 pour P1
23h14 - 21 / 23.04 - echange p1 & p2 ...
22h39 - 21 / 23.00 - trade à 0.75 vs 8 en P2
21h39 - 17 / 22.95 - echange des coeff pour le P2 et son eval inversée ! (BeamSearch avec le minimizer correct)
  -> le problème etait que je considerais le P2 tjs aggro !
  
20h03 - KO - code normal, mais ne prends plus en compte les handvalue as-is 
19h12 - 15 / 23.20 - le code etrange 
18h04 - 15 / 23.70 - reintegration du 23h02 dans mon code actuel
13h30 - 12 / 23.91 - descendu a 20 / 23.25 ! code de 23.06
xx - 21 / 22.94 - 0.4 0.45
11h39 - - 0.4 / 0.8 (augmentation de la diff de valeur) mode board control
11h11 - 24 / 22.72 - 08 1.0
10h43 - 22 / 22.89 - augmentation des coeff et de la diff (06 08)
10h01 - 23 / 22.72 - simpleEVal mode board control (0.59 0.6)
09h24 - 24 / 22.61 - simpleEval (coeff en dur), aggro (0.6 0.59)
00h00 - 40 - eval board control de silverfish
23h27 - 31 / 21.89 - test avec une eval hyper simpliste (NewEval)

22h59- 18 / 22.97 - ajout gros malus si on laisse le lethal à l'autre
22h52 - nope - tentative d'aggro quand ma clock est meilleur que la sienne ...
22h00 - 18 / 22.91 - fini de réparer le minimizer

10h37 - - reworkEval, ca risque de pas aller bien haut
02h07 - dernier :) - correction du prunning ! je fais de la merde, mais c'est la fonction d'eval qui est pourrie maintenant :)
01h46 - - code de 23h06 
01h24 - bof- suppression des cartes & etrangeté sur le ab cutoff :)
00h50 - 21 / 22.75 - on lui offre des cartes qui ont un cout
00h11 - 22 / 22.78 - bug whand back to 0.6, mais on lui offre des cartes
23h47 - 18 / 22.96 - wHand > wBoard , no good?
23h06 - 15 / 23.49 - ajout du poids sur les cartes hand
22h35 - 15 / 23.35 - augmentation des coeffs wBoard (0.6 / 0.8)
22h00 - 19 / 23.11 - remise à 'niveau' des valeurs de egaetan
21h52 - 22 / 22.56 - apres le push legend

== GOLD
- 
vendredi prochain - - ISBeamSearch :)
18hxx - - reflechir au fait que la valeur des cartes dépend du prochain à jouer
15hxx - - nb carte sur le board en bonus ...
14hxx - 16h27 - essayer les coeff de egaetan ?
16h06 - 18 / 27.88 - mana à -20
15h05 - 17 / 28.07 - coeff mana à -10 + reduction du cas lethal + retournement hisEval
14h36 - 17 / 27.96 - coeff mana à -10
14h06 - 19 / 27.67 - remettre la mana à -1
12h03 - 18 / 27.85 - v6 sans les bigcrea
11h34 - 21 / 27.11 - eval6 (moins bonne d'apres batch16)
10h40 - 20 / 27.19 - give little bonus to mana (low + au lieu de -) 67 / 54%
10h08 - 18 / 27.71 - code à jour avec l'alpha/beta cutoff 69 / 43
09h18 - 16 / 27.93 - code de 22h35 (pour tester) 
08h29 - 18 / 27.53 - uniquement rework, mais 2 instance (lui + moi)
08h04 - 20 !- uniquement rework
07h40 - 24 / 26.69 - essayer dans l'autre sens :)
00h14 - 21 / 27.21 - test avec eval5 en p1 et reworkEval en P2 au vu des %
23h46 - 11 / 28.11 - retest avec eval5 72% / 44%
22h35 - 12 / 28.10 - 256 noeuds 64 % / 50%
22h03 - ~20 - bs à 64 noeuds
21h40 - ~ 20- beamsearch à 1024
21h01 - 11 / 28.10 - code à jour
20h34 - 13 / 27.86 - code de 17h21
19h48 - 16 / 27.31 - 0.4 & 0.45
19h02 - 17 / 27.07 - 0.3 & 0.35
18h35 - 16 / 27.33 - remonte des poids 0.5 et 0.55
18h06 - 11 / 27.98 - meme code avec refactoring de debug
17h21 - 10 / 28.16 - remonte du poids 0.4 et 0.45
16h55 - 17 / 27.50 - baisse du poids board 0.2 & 0.25
16h14 - 13 / 27.82 - nouveaux coeffs 
15h44 - 15 /27.63 - poids des cartes / 3 ( pour prendre en compte le *4*2)
15h17 - 17 / 27.11 - 4*a+2*d
14h45 - 15 / 27.30 - 1 partout
14h15 - 22 / 26.41 - lethal à 3, wh à 1.8 et correction bug zobrist
13h42 - 16 / 27.16 - wHealth 1.8
13h08 - 16 / 27.37 - augmentation wHealth (1.5)
12h40 - 21 / 26.64 - baisse le wHealth
12h01 - 14 / 27.42 - revue de health 
11h35 - no good - ajout early cutoff & rework health
11h06 - 22 / 26.74 - new eval
10h37 - 15 / 27.57 - ajout du endturn dans les actions à évaluer
10h01 - 12 / 27.93 - nothing
09h11 - 28 / 27.20 - bs a 256
08h48 - ouch, timeouts ? - bs à 2048
01h53 - 18 / 27.10 - malus bucket à 0.5
01h29 - 20 / 26.84 - augmentation du malus de bucket (2*)
00h50 - 20 / 26.70 - picker sans malus
23h53 - 16 / 27.17 - un seul picker (v2)
23h18 - dépasse pas 33 :( - augmentation du coeff qui n'etait pas assez grand
23h09 - - correction d'un probleme de pick
22h49 - - draft avec le picker P1/P2
22h02 - 18 / 26.73 - picker gaussian % : 61 / 50
21h58 - KO oublie de faceValue- picker gaussian ...
20h43 - 16 / 27.29 - code à jour, sans pessimistic. 70% / 46%
20h01 - 13 / 27.41 - relance 17h29
19h14 - 27 ? - relance du 17h29
18h35 - 19 / 26.94 - refactoring du beamsearch, sans changement
17h29 - 10 / 28.10 - correction d'un bug dans le cache de BSMOVE
15h03 - 17 / xxx - submit du code refactoré pour les match offline
14h31 - 13 / 27.60 - code de 12h32
14h08 - 16 - suppression du malus sur les items :(
13h44 - 17 / 26.84 - relance pour verifier le 13h02
13h02 - 16 - malus sur le nombre d'item à partir de 5
12h32 - 8 / 28.49 - bonus a * 5
12h07 - 12 / 27.38 - plus gros bonus (20 *) % : 66 / 46
11h41 - 7 / 28.15 - refact eval (as-is) + ajout du bonus pour le nb de carte sur le board (*10) 
10h25 - 25 / 27.31 - repassage en full Eval5
02h27 - 30 / xxx- multi eval inverséé ...
02h21 - ouch - multi eval (1st : aggro, 2nd : board control)
01h48 - 21 / 26.24 - eval Aggro 68 / 46
01h05 - 13 / 26.97 - aggro :  - 10 * his_hp ||  % : 77 /46
00h47 - - Eval aggro (v1 : hp * 100)
23h47 - 15 / 27.19  - card values from egaetan
23h18 - 17 / 26.76 - reevaluate card values ...
21h27 - 11 / 27.41 - apres stabilisation .... 
15h28 - 15 / 27.12 - code à jour
14h55 - 16 / 26.73 - code de 12h22 (j'aimais bien cette 10eme place)
14h27 - 16 / 26.78 - code de 13h06
14h03 - 16 / 26.25 - reput the bug ...
13h40 - 17 / 26.47 - fix bug ou je joue pasle 1er tour
13h06 - 10 / 27.62 - fix a hash bug (def & att sur 16 au lieu de 32)  :nmahoude - P0:82/119 68,91% - P1:47/119 39,50%
12h22 - 9 / 27.73 - BeamSearch refactored
12h09 - 27 / 25.21 - score  courant
01h53 - 26 / 25.40 - correction d'un timeout
01h36 - - 1er pas du beamsearch
22h44 - 30 / 25.13 - le 12h53 ...
22h11 - 29 / 25.19 - same code to be sure
21h48 - 24 / 25.66 - BeamSearch (1024) .... le reste doit etre pareil 
17h06 - 23 / 25.70 - code similaire 12h53 (eval5 au lieu de eval6)
16h26 - 24 / 25.61 - 12h53
16h01 - 28 / 24.884 - code de 01h30
15h40 - 54 / xxx- 6*bigdef + 2*c.d
14h59 - 28 / 24.87 - eval v6 (2 BigDef + 2* c.d)
14h35 - 24 / 25.45 - eval v6 rework
14h10 - 32 / 24.56 - eval v6 rework
13h42 - 27 / 25.02 - eval v6 rework
13h15 - 24 / 25.49 - eval v6
12h53 - 21 / 25.70 - normalement y'a plus le bug du timeout la
12h19 - 50 / xxx - relance avec la modif de simulation uniquement (play p1 & p2)
12hxx - 27 / 25.27 - sans resubmit, stab de la nuit
01h30 - 19 / 25.99 - 40ms
00h43 - 31 / 24.91 - 10ms
00h22 - 29 / 25.27 - full steam ahead - 95ms
23h55 - 25 / 25.50 - code de 11h18 (was 14)
23h29 - 26 / 25.48 - sans le bug ...
23h04 - 29 / 24.96 - attack the same creature to death (+ bug)
22h41 - 29 / 25.04 - only 2*att+2*def dans l'eval du scorecard


- 10/08
18h40 - 23 / 25.48  - remove the bucket in card picker 
14h39 -- attack x4
12h07 - - attack x 6
11h44 - 16 / 26.53 - attack x 8
11h18 - 14 / 26.80 - attack x4
10h55 - 28 / 25.92 - defense *2
10h30 - 17 / 26.31 - score card : 2*attack
01h47 - 21 / 25.76 - random avec une seed 'time'
01h09 - 22 / 25.83 - remonter la vie à 0.5 (changement dans le healthscore) + mana à -1 au lieu de -2
00h43 - 22 / 25.65 - devaluation de la vie à 0.1 (au lieu de .5 au lieu de 1)
00h17 - 25 / 25.40 - uniquement zobrist
23h54 - 29 / 24.96 - zobrist à 4096 + ajout des runes
23h31 - 23 / 25.65 - devaluation de la vie ...
22h55 - 25 / 25.19 - relance du 19h02 pour checker l'autosubmit  
22h24 - 36 / 24.49 - code de 11h37
19h02 - 21 / 25.52 - auto resubmit boss gold (score mana*-2, 30ms, 10,15,5)


== SILVER
-
17h59 - - score : mana * -2
17h36 - 33 / 28.31 - score : mana * -100 
13h52 - 21 / 29.30 - 30ms + (10,15,5) + calcul hash zobrist des cartes quand on les instancie
12h54 - 29 / 28.77 - 30ms + 10,15,5 au lieu de 15,10,5
12h34 - 34 / 28.34 - 30ms (coool :) )
11h37 - 24 / 29.11 - test à 2
09h37 - 34 / 28.19 - handcount à 3
08h43 - 38 / 28.08 - handCount à 4
08h18 - 24 / 28.73 - handCount à <= 3
07h28 - 26 / 28.66 - handcount à 0
02h16 - 29 / 28.50 - handCount basculé à 15 pour test (etait 2, avant 7)
01h56 - 23 / 28.81 - cardpicker different et score > 0 sur le handcount à partir de 2 (au lieu de 7)
01h37 - 25 / 28.55 - code de 23h05
01h13 - 39 / 28 - refact du card picker
00h56 - 51 / 27.20 - bucket à 200 et augmentation de low vs hi
00h30 - 25 / 28.65 - revert à Eval4
00h20 - X - revert sur l'optim du zobrist (precalcul card &  4 [] -> [] semble ko ou c'est autre chose )
23h29 - 34 / 28.10 - new submit (principalement le hash)
23h03 - 20 / 29.09 - resubmit du 15h13 ...
19h28 - 30 ! - 
 - - Lethal a 9 - bof
17h23 - 16 / 29.73 - lethal a 6 
17h16 - XX         - lethal a 1 ca semble pas bon non plus ...
17h04 - XX         - lethal à 12 => bof ~29
16h43 - 19 / 29.60 - amelioration zobrist + eval avec lethal à 3
15h13 - 17 / 29.03 - transposition table en place (/!\ CALCUL DU HASH POURRIE )
14h09 - 22 / 29.08 - sans debug pour voir ....
13h49 - 46 / 27.46 - check back à 511
13h28 - 42 / 27.83 - full à jour (check 1023, 95ms, debug) => hmmm quelques(1, +?) timeouts
13h10 - 29 / 28.55 - code de 9h13
12h46 - 38 / 28.14 - code d 09h33
12h09 - 38 - 95ms
11h27 - 34 / - limitation du node_cache (120_000)
11h06 - 30 / 28.72 - repassage a default pour la hashmap
10h55 - - hashmap des node repasse à defaultsize=32 au lieu de 860 => encore des timeouts
10h52 - ouch - delegation de la construction du zobrist au premier tour de battle
10h27 - 70 - relance pour check
10h07 - 53 ? - check zobrist => beaucoup de timeout au tour 3 ...
09h33 - 18 / 29.62 - ne pas réévaluer les noeuds terminaux déjà vus
09h13 - 21 / 29.19 - 0
08h40 - 46 / 27.39 - boost to 1000
23h08 - 45 / 27.52 - boost to 500
22h46 - 28 / 28.39 - boost du poids du bucket (100 * coeff) pour avoir un impact
22h20 - 30 / 28.xx - relance pour confirmer 
21h56 - 29 / 28.50 - utilisation des face cards precalculées
21h23 - 123 / 24.43 - nouveau code, nouvel eval card (faceValue())

21h05 - - code de 01h15 - on dirait que le niveau a bien monté
12h49 - 106 / 24.95 - 
08h50 - - 2 steps action decision
08h36 - 98 /  25.30 - relance du meme code pour check
01h15 - 63 / 26 - revert du gros refactoring :(
14h05 - 95 / 25.06 - test sur le hasGuard !
13h25 - 99 / 24.93 - suppression de deux types d'actions légales : SummonAndAttack (toutes) + attack(quand attack vaut 0) ET utilisation des valeurs cache plutot que calculée

13h07 - 122 / 24.39 - refacto du nombre de cartes hand/board de chaque joueur + exception si diff

01h15 - - relance 00h21
00h21 - 73 / 25.52 - modification picker CardPickerV2 (value + bucket + items(à 0))
23h18 - 112 / 24.79 - suppression des caches
?     - 107 / 24.78 - sans cache
?     - 144 / 24.10 - cache des summons
?     - 128
06/08 18h03 - 95 / 25.10 - relance du code avec copie du state initiale
06/08 16h55 - 144 / 23.78 - relance du code sans summon mais utilisation du cache avec 0 actions
06/08 16h38 - 139 / xxx - relance du code de 14h10 ...-> pas d'amlioration
06/08 14h59 - 106/24.86 - sans les summon précalculé (le 52/26 doit etre un coup de bol)
06/08 14h10 - 134 / 24.09 - cache des summon précalculé avec State+actions
06/08 13h30 - 52  / 26.13 - modification du picker : only face Value of cards
06/08 00h35 - 157 / 23.45 - same as 00h07 
06/08 00h30 - nogo - simple aggro picker
06/08 00h07 - 144 / 23.71 - refactor abilities to use an int instead of an object

