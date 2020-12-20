
/*
Proudly built by org.ndx.codingame.simpleclass.Assembler on 2020-12-19T15:05:37.039+01:00[Europe/Paris]
@see https://github.com/Riduidel/codingame/tree/master/tooling/codingame-simpleclass-maven-plugin
*/
import java.util.Random;
import java.util.Scanner;


/**
 * Legend 69 with this code
 * I was in plain rework but found some bugs (minor & major) 
 * 
 * After correcting the bugs, I'm at this good ranking so I won't go longer in the refactoring for now.
 * 
 * @author nmahoude
 *
 */
public class legend_69 {

}



class Collision {

    public double t;

    public int dir;

    public Unit a;

    public Unit b;

    public Collision() {
    }

    public Collision update(double t, Unit unit, int dir) {
        this.t = t;
        this.dir = dir;
        this.a = unit;
        this.b = null;
        return this;
    }

    public Collision update(double t, Unit a, Unit b) {
        this.dir = 0;
        this.t = t;
        this.a = a;
        this.b = b;
        return this;
    }
}

enum EntityType {

    WIZARD, OPPONENT_WIZARD, SNAFFLE, BLUDGER, POLE
}

abstract class Spell {

    public static final int OBLIVIATE = 0;

    public static final int PETRIFICUS = 1;

    public static final int ACCIO = 2;

    public static final int FLIPENDO = 3;

    public static int SPELL_DURATION[] = new int[] { 3, 1, 6, 3 };

    public static int SPELL_COST[] = new int[] { 5, 10, 20, 20 };

    public Wizard caster;

    public int duration;

    public Unit target;

    int type;

    int sduration;

    Unit starget;

    public Spell(int type, Wizard caster) {
        this.type = type;
        this.caster = caster;
        this.duration = 0;
    }

    public void reset() {
        duration = sduration;
        target = starget;
    }

    public abstract void effect();

    public void print() {
        if (duration > 0) {
            String typeAsStr = "";
            switch(type) {
                case OBLIVIATE:
                    typeAsStr = "OBLIVIATE";
                    break;
                case FLIPENDO:
                    typeAsStr = "FLIPENDO";
                    break;
                case ACCIO:
                    typeAsStr = "ACCIO";
                    break;
                case PETRIFICUS:
                    typeAsStr = "PETRIFICUS";
                    break;
            }
            System.err.println("" + type + " " + target.id + " " + duration + " | ");
        }
    }

    public void cast(Unit target) {
        this.target = target;
        duration = SPELL_DURATION[type];
    }

    public void apply() {
        if (duration != 0) {
            duration -= 1;
            if (!target.dead) {
                effect();
            }
        }
    }

    public void save() {
        sduration = duration;
        starget = target;
    }

    public void checkTarget() {
        if (duration != 0 || target == null || target.dead) {
            cancelSpell();
        }
    }

    private void cancelSpell() {
        target = null;
        duration = 0;
    }
}

class Accio extends Spell {

    public Accio(Wizard caster) {
        super(ACCIO, caster);
    }

    @Override
    public void effect() {
        double d = caster.position.distTo(target.position);
        if (d < 10.0) {
            return;
        }
        double dcoef = d * 0.001;
        double power = 3000.0 / (dcoef * dcoef);
        if (power > 1000.0) {
            power = 1000.0;
        }
        dcoef = 1.0 / d;
        power = power / target.mass;
        target.vx -= dcoef * power * (target.position.x - caster.position.x);
        target.vy -= dcoef * power * (target.position.y - caster.position.y);
    }

    @Override
    public void print() {
    // TODO Auto-generated method stub
    }
}

class Flipendo extends Spell {

    public Flipendo(Wizard caster) {
        super(FLIPENDO, caster);
    }

    @Override
    public void effect() {
        double d = caster.position.distTo(target.position);
        if (d < 10.0) {
            return;
        }
        double dcoef = d * 0.001;
        double power = 6000.0 / (dcoef * dcoef);
        if (power > 1000.0) {
            power = 1000.0;
        }
        dcoef = 1.0 / d;
        power = power / target.mass;
        target.vx += dcoef * power * (target.position.x - caster.position.x);
        target.vy += dcoef * power * (target.position.y - caster.position.y);
    }

    @Override
    public void print() {
    // TODO Auto-generated method stub
    }
}

class Bludger extends Unit {

    public Wizard last;

    public Wizard slast;

    public int ignore[] = new int[2];

    public Bludger() {
        super(EntityType.BLUDGER, 200.0, 8, 0.9);
        this.last = this.slast = null;
        ignore[0] = -1;
        ignore[1] = -1;
    }

    @Override
    public void print() {
        System.err.print("Bludger " + id + " " + position + " " + vx + " " + vy + " " + speed() + " " + ignore[0] + " " + ignore[1] + " | ");
        if (last != null) {
            System.err.print("Last " + last.id + " | ");
        }
        System.err.println("");
    }

    @Override
    public void save() {
        super.save();
        slast = last;
    }

    @Override
    public void reset() {
        super.reset();
        last = slast;
        ignore[0] = -1;
        ignore[1] = -1;
    }

    @Override
    public void bounce(Unit u) {
        if (u.type == EntityType.WIZARD) {
            last = (Wizard) u;
        }
        super.bounce(u);
    }

    public void play() {
        // Find our target
        Wizard target = null;
        double d = Double.MAX_VALUE;
        for (int i = 0; i < 4; ++i) {
            Wizard wizard = Player.wizards[i];
            if ((last != null && last.id == wizard.id) || wizard.team == ignore[0] || wizard.team == ignore[1]) {
                continue;
            }
            double d2 = position.squareDistance(wizard.position);
            if (target == null || d2 < d) {
                d = d2;
                target = wizard;
            }
        }
        if (target != null) {
            thrust(1000.0, target.position.x, target.position.y, Math.sqrt(d));
        }
        ignore[0] = -1;
        ignore[1] = -1;
    }
}

class Obliviate extends Spell {

    public Obliviate(Wizard caster) {
        super(OBLIVIATE, caster);
    }

    @Override
    public void effect() {
        ((Bludger) target).ignore[caster.team] = caster.team;
    }
}

class Petrificus extends Spell {

    public Petrificus(Wizard caster) {
        super(PETRIFICUS, caster);
    }

    @Override
    public void effect() {
        target.vx = 0.0;
        target.vy = 0.0;
    }

    @Override
    public void print() {
    }
}

class Wizard extends Unit {

    public Spell[] spells = new Spell[4];

    public int team;

    int sgrab;

    Snaffle ssnaffle;

    int spell;

    Unit spellTarget;

    public Wizard(int team) {
        super(EntityType.WIZARD, 400.0, 1, 0.75);
        this.team = team;
        snaffle = null;
        grab = 0;
        spells[Spell.OBLIVIATE] = new Obliviate(this);
        spells[Spell.PETRIFICUS] = new Petrificus(this);
        spells[Spell.ACCIO] = new Accio(this);
        spells[Spell.FLIPENDO] = new Flipendo(this);
        spellTarget = null;
    }

    public void grabSnaffle(Snaffle snaffle) {
        grab = 4;
        snaffle.carrier = this;
        this.snaffle = snaffle;
        // Stop the accio spell if we have one
        Spell accio = spells[Spell.ACCIO];
        if (accio.duration != 0 && accio.target.id == snaffle.id) {
            accio.duration = 0;
            accio.target = null;
        }
    }

    public void apply(int move) {
        if (snaffle != null) {
            // TODO WHY NOT PRECALCULATE ?
            double coef = 500.0 * (1.0 / snaffle.mass);
            snaffle.vx += Player.cosAngles[move] * coef;
            snaffle.vy += Player.sinAngles[move] * coef;
        } else {
            vx += Player.cosAngles[move] * 150.0;
            vy += Player.sinAngles[move] * 150.0;
        }
    }

    public void output(int move, int spellTurn, int spell, Unit target) {
        if (spellTurn == 0 && spells[spell].duration == Spell.SPELL_DURATION[spell]) {
            if (spell == Spell.OBLIVIATE) {
                System.out.print("OBLIVIATE ");
            } else if (spell == Spell.PETRIFICUS) {
                System.out.print("PETRIFICUS ");
            } else if (spell == Spell.ACCIO) {
                System.out.print("ACCIO ");
            } else if (spell == Spell.FLIPENDO) {
                System.out.print("FLIPENDO ");
            }
            System.out.println("" + target.id);
            return;
        }
        // Adjust the targeted point for this angle
        // Find a point with the good angle
        double px = position.x + Player.cosAngles[move] * 10000.0;
        double py = position.y + Player.sinAngles[move] * 10000.0;
        if (snaffle != null) {
            System.out.println("THROW " + Math.round(px) + " " + Math.round(py) + " 500");
        } else {
            System.out.println("MOVE " + Math.round(px) + " " + Math.round(py) + " 150");
        }
    }

    public boolean cast(int spell, Unit target) {
        int cost = Spell.SPELL_COST[spell];
        if (Player.myMana < cost || target.dead) {
            return false;
        }
        Player.myMana -= cost;
        this.spell = spell;
        spellTarget = target;
        return true;
    }

    @Override
    public Collision collision(Unit u, double from) {
        if (u.type == EntityType.SNAFFLE) {
            u.radius = -1.0;
            Collision result = super.collision(u, from);
            u.radius = 150.0;
            return result;
        } else {
            return super.collision(u, from);
        }
    }

    public void save() {
        super.save();
        sgrab = grab;
        ssnaffle = snaffle;
    }

    public void reset() {
        super.reset();
        grab = sgrab;
        snaffle = ssnaffle;
    }

    public void bounce(Unit u) {
        if (u.type == EntityType.SNAFFLE) {
            Snaffle target = (Snaffle) u;
            if (snaffle == null && grab == 0 && !target.dead && target.carrier == null) {
                grabSnaffle(target);
            }
        } else {
            if (u.type == EntityType.BLUDGER) {
                ((Bludger) u).last = this;
            }
            super.bounce(u);
        }
    }

    public void play() {
        // Relacher le snaffle qu'on porte dans tous les cas
        if (snaffle != null) {
            snaffle.carrier = null;
            snaffle = null;
        }
    }

    @Override
    public void end() {
        super.end();
        if (grab != 0) {
            grab -= 1;
            if (grab == 0) {
                // Check if we can grab a snaffle
                for (int i = 0; i < Player.snafflesFE; ++i) {
                    Snaffle snaffle = Player.snaffles[i];
                    if (!snaffle.dead && snaffle.carrier == null && this.position.squareDistance(snaffle.position) < 159201.0) {
                        grabSnaffle(snaffle);
                        break;
                    }
                }
            }
        }
        if (snaffle != null) {
            snaffle.position = this.position;
            snaffle.vx = vx;
            snaffle.vy = vy;
        }
        if (spellTarget != null) {
            spells[spell].cast(spellTarget);
            spellTarget = null;
        }
    }

    @Override
    public void print() {
        System.err.print("Wizard " + id + " " + position + " " + vx + " " + vy + " " + speed() + " " + grab + " | ");
        if (snaffle != null) {
            System.err.print("Snaffle " + snaffle.id + " | ");
        }
        for (int i = 0; i < 4; ++i) {
            spells[i].print();
        }
        System.err.println("");
    }

    public void updateSnaffle() {
        if (state != 0) {
            for (int i = 0; i < Player.snafflesFE; ++i) {
                Snaffle snaffle = Player.snaffles[i];
                if (snaffle.position.equals(this.position) && snaffle.vx == vx && snaffle.vy == vy) {
                    this.snaffle = snaffle;
                    snaffle.carrier = this;
                }
            }
            grab = 3;
        } else {
            if (grab != 0) {
                grab -= 1;
            }
            snaffle = null;
        }
    }

    public void apply(AGSolution solution, int turn, int index) {
        if (index == 1) {
            if (solution.spellTurn1 == turn) {
                if (Player.myWizard1.cast(solution.spell1, solution.spellTarget1)) {
                    Player.myWizard1.apply(solution.moves1[turn]);
                }
            } else {
                Player.myWizard1.apply(solution.moves1[turn]);
            }
        } else {
            if (solution.spellTurn2 == turn) {
                if (Player.myWizard2.cast(solution.spell2, solution.spellTarget2)) {
                    Player.myWizard2.apply(solution.moves2[turn]);
                }
            } else {
                Player.myWizard2.apply(solution.moves2[turn]);
            }
        }
    }
}

class Snaffle extends Unit {

    Wizard scarrier;

    boolean danger = false;

    public Snaffle() {
        super(EntityType.SNAFFLE, 150.0, 0.5, 0.75);
        carrier = null;
    }

    @Override
    public void update(int id, int x, int y, int vx, int vy, int state) {
        super.update(id, x, y, vx, vy, state);
        dead = false;
    }

    @Override
    public Collision collision(double from) {
        if (carrier != null || dead) {
            return null;
        }
        double tx = 2.0;
        double ty = tx;
        if (position.x + vx < 0.0) {
            tx = -position.x / vx;
        } else if (position.x + vx > WIDTH) {
            tx = (WIDTH - position.x) / vx;
        }
        if (position.y + vy < radius) {
            ty = (radius - position.y) / vy;
        } else if (position.y + vy > HEIGHT - radius) {
            ty = (HEIGHT - radius - position.y) / vy;
        }
        int dir;
        double t;
        if (tx < ty) {
            dir = HORIZONTAL;
            t = tx + from;
        } else {
            dir = VERTICAL;
            t = ty + from;
        }
        if (t <= 0.0 || t > 1.0) {
            return null;
        }
        return Simulation.collisionsCache[Simulation.collisionsCacheFE++].update(t, this, dir);
    }

    @Override
    public Collision collision(Unit u, double from) {
        if (u.type == EntityType.WIZARD) {
            radius = -1.0;
            Collision result = super.collision(u, from);
            radius = 150.0;
            return result;
        } else {
            return super.collision(u, from);
        }
    }

    @Override
    public void bounce(Unit u) {
        if (u.type == EntityType.WIZARD) {
            Wizard target = (Wizard) u;
            if (target.snaffle == null && target.grab == 0 && !dead && carrier == null) {
                target.grabSnaffle(this);
            }
        } else {
            super.bounce(u);
        }
    }

    public void bounce(int dir) {
        if (dir == HORIZONTAL && position.y >= 2050.0 && position.y <= 5450.0) {
            dead = true;
            if (Player.myTeam == 0) {
                if (position.x > 8000) {
                    Player.myScore += 1;
                } else {
                    Player.hisScore += 1;
                }
            } else {
                if (position.x > 8000) {
                    Player.hisScore += 1;
                } else {
                    Player.myScore += 1;
                }
            }
        } else {
            super.bounce(dir);
        }
    }

    @Override
    public void move(double t) {
        if (!dead && carrier == null) {
            super.move(t);
        }
    }

    @Override
    public void end() {
        if (!dead && carrier == null) {
            super.end();
        }
    }

    @Override
    public void save() {
        super.save();
        scarrier = carrier;
    }

    @Override
    public void reset() {
        super.reset();
        carrier = scarrier;
        dead = false;
    }

    @Override
    public void print() {
        if (dead) {
            System.err.print("Snaffle " + id + " dead");
        } else {
            System.err.print("Snaffle " + id + " " + position + " " + vx + " " + vy + " " + speed() + " " + " | ");
            ;
            if (carrier != null) {
                System.err.print("Carrier " + carrier.id + " | ");
            }
        }
        System.err.println("");
    }
}

class Simulation {

    public static int depth = 0;

    private static final int COLLISION_SIZE = 100000;

    public static int collisionsCacheFE = 0;

    public static Collision[] collisionsCache;

    static int collisionsFE = 0;

    static Collision[] collisions;

    static int tempCollisionsFE = 0;

    static Collision[] tempCollisions;

    public static int smyMana;

    public static int smyScore;

    public static int shisScore;

    static Collision fake = new Collision();

    static {
        fake.t = 1000.0;
        initCollisionsCache();
    }

    private static void initCollisionsCache() {
        collisionsCache = new Collision[COLLISION_SIZE];
        collisions = new Collision[COLLISION_SIZE];
        tempCollisions = new Collision[COLLISION_SIZE];
        for (int i = 0; i < COLLISION_SIZE; ++i) {
            collisionsCache[i] = new Collision();
        }
    }

    public static void dummies() {
        if (Player.hisWizard1.snaffle != null) {
            Player.hisWizard1.snaffle.thrust(500.0, Player.hisGoal.x, Player.hisGoal.y, Player.hisWizard1.position.distTo(Player.hisGoal));
        } else {
            Snaffle target = null;
            double targetD = Double.MAX_VALUE;
            double d;
            for (int i = 0; i < Player.snafflesFE; ++i) {
                Snaffle snaffle = Player.snaffles[i];
                if (!snaffle.dead) {
                    d = Player.hisWizard1.position.squareDistance(snaffle.position);
                    if (d < targetD) {
                        targetD = d;
                        target = snaffle;
                    }
                }
            }
            if (target != null) {
                Player.hisWizard1.thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
            }
        }
        if (Player.hisWizard2.snaffle != null) {
            Player.hisWizard2.snaffle.thrust(500.0, Player.hisGoal.x, Player.hisGoal.y, Player.hisWizard2.position.squareDistance(Player.hisGoal));
        } else {
            Snaffle target = null;
            double targetD = Double.MAX_VALUE;
            double d;
            for (int i = 0; i < Player.snafflesFE; ++i) {
                Snaffle snaffle = Player.snaffles[i];
                if (!snaffle.dead) {
                    d = Player.hisWizard2.position.squareDistance(snaffle.position);
                    if (d < targetD) {
                        targetD = d;
                        target = snaffle;
                    }
                }
            }
            if (target != null) {
                Player.hisWizard2.thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
            }
        }
    }

    private static double eval() {
        double energy = 0;
        energy -= distanceToClosestSnaffle();
        energy -= snafflesNearOppGoal();
        //energy += snaffleAvgPosition();
        //energy += wizardDistanceToSnaffles();
        energy += distanceBetweenMyWizards();
        energy += Player.myMana * 200;
        energy += 500 * (+(Player.myWizard1.snaffle != null ? 1 : 0) + (Player.myWizard2.snaffle != null ? 1 : 0) - (Player.hisWizard1.snaffle != null ? 1 : 0) - (Player.myWizard2.snaffle != null ? 1 : 0));
        // last one !
        if (Player.myScore >= Player.victory) {
            energy = Double.POSITIVE_INFINITY;
        }
        // ------------------------
        return energy;
    }

    private static double snafflesNearOppGoal() {
        double dist = 0;
        for (int i = 0; i < Player.snafflesFE; i++) {
            Snaffle snaffle = Player.snaffles[i];
            if (snaffle.dead)
                continue;
            dist += snaffle.position.distTo(Player.myGoal);
        }
        return dist;
    }

    private static double distanceToClosestSnaffle() {
        Snaffle closest1 = null, closest2 = null;
        double bestDist1 = Double.POSITIVE_INFINITY, bestDist2 = Double.POSITIVE_INFINITY;
        for (int i = 0; i < Player.snafflesFE; i++) {
            Snaffle snaffle = Player.snaffles[i];
            if (snaffle.dead)
                continue;
            double dist1 = Player.myWizard1.position.distTo(snaffle.position);
            double dist2 = Player.myWizard2.position.distTo(snaffle.position);
            if (dist1 < bestDist1) {
                bestDist1 = dist1;
                closest1 = snaffle;
            }
            if (dist2 < bestDist2) {
                bestDist2 = dist2;
                closest2 = snaffle;
            }
        }
        return bestDist1 + bestDist2;
    }

    private static double distanceBetweenMyWizards() {
        double distBetweenWizards = Player.myWizard1.position.distTo(Player.myWizard2.position);
        return 0.1 * distBetweenWizards;
    }

    private static double wizardDistanceToSnaffles(double energy) {
        double wizardAvgDist = 0;
        for (int i = 0; i < Player.snafflesFE; i++) {
            Snaffle snaffle = Player.snaffles[i];
            if (snaffle.dead)
                continue;
            wizardAvgDist += Player.myWizard1.position.distTo(snaffle.position);
            wizardAvgDist += Player.myWizard2.position.distTo(snaffle.position);
        }
        energy -= wizardAvgDist / (2 * 16_000 * Player.snafflesFE);
        return energy;
    }

    private static double snaffleAvgPosition(double energy) {
        double avgPos = 0;
        for (int i = 0; i < Player.snafflesFE; i++) {
            avgPos += Player.snaffles[i].position.distTo(Player.myGoal);
        }
        energy -= avgPos / (16_000 * Player.snafflesFE);
        return energy;
    }

    public static void reset() {
        for (int i = 0; i < Player.unitsFE; ++i) {
            Player.units[i].reset();
        }
        for (int i = 0; i < 16; ++i) {
            Player.spells[i].reset();
        }
        Player.myMana = smyMana;
        Player.myScore = smyScore;
        Player.hisScore = shisScore;
    }

    public static void simulate(AGSolution solution) {
        Player.energy = 0;
        depth = 0;
        int myInitScore = Player.myScore;
        int hisInitScore = Player.hisScore;
        Player.myWizard1.apply(solution, 0, 1);
        Player.myWizard2.apply(solution, 0, 2);
        dummies();
        play();
        final int coef = 100_000;
        solution.energy = AG.patiences[0] * coef * ((Player.myScore - myInitScore) - (Player.hisScore - hisInitScore));
        ;
        //solution.energy = eval() * 0.1;
        depth = 1;
        for (int i = 1; i < AG.DEPTH; ++i) {
            Player.myWizard1.apply(solution, i, 1);
            Player.myWizard2.apply(solution, i, 2);
            dummies();
            play();
            solution.energy += AG.patiences[depth] * coef * ((Player.myScore - myInitScore) - (Player.hisScore - hisInitScore));
            depth += 1;
        }
        solution.energy += eval();
        reset();
    }

    public static void play() {
        for (int i = 0; i < 4; ++i) {
            Player.spells[i].apply();
        }
        Player.bludgers[0].play();
        Player.bludgers[1].play();
        Player.wizards[0].play();
        Player.wizards[1].play();
        Player.wizards[2].play();
        Player.wizards[3].play();
        for (int i = 5; i < 16; ++i) {
            Player.spells[i].apply();
        }
        move();
        for (int i = 0; i < Player.unitsFE; ++i) {
            Player.units[i].end();
        }
        if (Player.myMana != 100) {
            Player.myMana += 1;
        }
    }

    private static void move() {
        double t = 0.0;
        double delta;
        Collision next = fake;
        collisionsCacheFE = 0;
        collisionsFE = 0;
        tempCollisionsFE = 0;
        Collision col;
        Unit a;
        Unit b;
        Unit u;
        int i, j;
        // Get first collisions
        for (i = 0; i < Player.unitsFE; ++i) {
            a = Player.units[i];
            // collision contre les murs
            col = a.collision(t);
            if (col != null) {
                collisions[collisionsFE++] = col;
                if (col.t < next.t) {
                    next = col;
                }
            }
            // collision contre les autres units
            for (j = i + 1; j < Player.unitsFE; ++j) {
                b = Player.units[j];
                if (a.can(b)) {
                    col = a.collision(b, t);
                    if (col != null) {
                        collisions[collisionsFE++] = col;
                        if (col.t < next.t) {
                            next = col;
                        }
                    }
                }
            }
        }
        while (t < 1.0) {
            if (next == fake) {
                // no collision found
                for (i = 0; i < Player.unitsFE; ++i) {
                    Player.units[i].move(1.0 - t);
                }
                break;
            } else {
                // Move to the collision time
                delta = next.t - t;
                for (i = 0; i < Player.unitsFE; ++i) {
                    Player.units[i].move(delta);
                }
                t = next.t;
                if (next.dir != 0) {
                    next.a.bounce(next.dir);
                } else {
                    next.a.bounce(next.b);
                }
                a = next.a;
                b = next.b;
                // Invalid previous collisions for the concerned units and get new ones
                next = fake;
                for (i = 0; i < collisionsFE; ++i) {
                    col = collisions[i];
                    if (!mustErase(col, a, b)) {
                        if (col.t < next.t) {
                            next = col;
                        }
                        tempCollisions[tempCollisionsFE++] = col;
                    }
                }
                Collision[] temp = tempCollisions;
                tempCollisions = collisions;
                collisions = temp;
                collisionsFE = tempCollisionsFE;
                tempCollisionsFE = 0;
                // Find new collisions for a
                col = a.collision(t);
                if (col != null) {
                    //System.err.println("Found a new collision with walls at "+col.t);
                    collisions[collisionsFE++] = col;
                    if (col.t < next.t) {
                        next = col;
                    }
                }
                for (i = 0; i < Player.unitsFE; ++i) {
                    u = Player.units[i];
                    if (a.id != u.id && a.can(u)) {
                        col = a.collision(u, t);
                        if (col != null) {
                            //System.err.println("Found a new collision w "+a.id+" with unit "+col.b.id+" at "+col.t);
                            if (u.type == EntityType.SNAFFLE) {
                                Snaffle snaffle = (Snaffle) u;
                            //System.err.println("collision with snaffle. ==> "+snaffle.carrier);
                            }
                            collisions[collisionsFE++] = col;
                            if (col.t < next.t) {
                                next = col;
                            }
                        }
                    }
                }
                // Find new collisions for b
                if (b != null) {
                    col = b.collision(t);
                    if (col != null) {
                        collisions[collisionsFE++] = col;
                        if (col.t < next.t) {
                            next = col;
                        }
                    }
                    for (i = 0; i < Player.unitsFE; ++i) {
                        u = Player.units[i];
                        if (b.id != u.id && b.can(u)) {
                            col = b.collision(u, t);
                            if (col != null) {
                                collisions[collisionsFE++] = col;
                                if (col.t < next.t) {
                                    next = col;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean mustErase(Collision col, Unit a, Unit b) {
        if (a.id == col.a.id) {
            return true;
        }
        if (b != null && col.b != null) {
            if (a.id == col.b.id || b.id == col.a.id || b.id == col.b.id) {
                return true;
            }
        } else if (b != null) {
            if (b.id == col.a.id) {
                return true;
            }
        } else if (col.b != null) {
            if (a.id == col.b.id) {
                return true;
            }
        }
        return false;
    }

    public static void save() {
        smyMana = Player.myMana;
        smyScore = Player.myScore;
        shisScore = Player.hisScore;
    }
}

class MathUtil {

    public static double[] resolve2ndDegree(double a, double b, double c) {
        double root = b * b - 4 * a * c;
        if (root < 0) {
            return null;
        }
        double sqRoot = Math.sqrt(root);
        if (root == 0) {
            return new double[] { -b / (2 * a) };
        } else {
            return new double[] { (-b + sqRoot) / (2 * a), (-b - sqRoot) / (2 * a) };
        }
    }
}

class Vector {

    public static final Vector ZERO = new Vector(0, 0);

    public final double vx, vy;

    public Vector(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public String toString() {
        return "V(" + vx + "," + vy + ")";
    }

    public Vector normalize() {
        return new Vector(vx / length(), vy / length());
    }

    /**
   * @param angle in radians
   * @return
   */
    /**
   * @param angle in radians
   * @return
   */
    public Vector rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector(vx * cos - vy * sin, vx * sin + vy * cos);
    }

    public Vector add(Vector v) {
        return new Vector(vx + v.vx, vy + v.vy);
    }

    public Vector dot(double d) {
        return new Vector(d * vx, d * vy);
    }

    public double dot(Vector v) {
        return vx * v.vx + vy * v.vy;
    }

    public double squareLength() {
        return vx * vx + vy * vy;
    }

    public double length() {
        return Math.sqrt(vx * vx + vy * vy);
    }

    public double angle(Vector v) {
        return Math.acos(this.dot(v) / (this.length() * v.length()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(vx);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(vy);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector other = (Vector) obj;
        if (Double.doubleToLongBits(vx) != Double.doubleToLongBits(other.vx))
            return false;
        if (Double.doubleToLongBits(vy) != Double.doubleToLongBits(other.vy))
            return false;
        return true;
    }

    public Vector sub(Vector v) {
        return new Vector(vx - v.vx, vy - v.vy);
    }

    static Point[] getInertialPointsIntersection(Vector currentSpeed, Vector desiredDirection, double maxForce) {
        double sx = currentSpeed.vx;
        double sy = currentSpeed.vy;
        double dx = desiredDirection.vx;
        double dy = desiredDirection.vy;
        double constant = (sx * dy - sy * dx) / dx;
        double a = (dy / dx) * (dy / dx);
        double b = 2 * constant * dy / dx + 1;
        double c = constant * constant - maxForce * maxForce;
        double results[] = MathUtil.resolve2ndDegree(a, b, c);
        if (results == null) {
            return null;
        } else if (results.length == 1) {
            double vx = results[0];
            double vy = Math.sqrt(maxForce * maxForce - vx * vx);
            return new Point[] { new Point(vx, vy) };
        } else {
            double vx1 = results[0];
            double vy1 = Math.sqrt(maxForce * maxForce - vx1 * vx1);
            double vx2 = results[1];
            double vy2 = Math.sqrt(maxForce * maxForce - vx2 * vx2);
            return new Point[] { new Point(vx1, vy1), new Point(vx2, vy2) };
        }
    }

    public Vector ortho() {
        return new Vector(-vy, vx);
    }
}

class Point {

    public static final Point ZERO = new Point(0, 0);

    public final double x, y;

    public Point(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Point(Point position) {
        this(position.x, position.y);
    }

    @Override
    public String toString() {
        return "P(" + x + "," + y + ")";
    }

    public Point add(Point addedPoint) {
        return new Point(x + addedPoint.x, y + addedPoint.y);
    }

    public Point add(Vector vec) {
        return new Point(x + vec.vx, y + vec.vy);
    }

    public Point sub(Vector vec) {
        return new Point(x - vec.vx, y - vec.vy);
    }

    public double distTo(Point p) {
        return Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
    }

    public double distTo(Point p, Vector v) {
        Point p2 = p.add(v);
        return distTo(p, p2);
    }

    public double distTo(Point p1, Point p2) {
        return Math.abs((p2.y - p1.y) * x - (p2.x - p1.x) * y + p2.x * p1.y - p2.y * p1.x) / Math.sqrt((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x));
    }

    public Vector sub(Point p2) {
        return new Vector(x - p2.x, y - p2.y);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }

    public double squareDistance(Point p) {
        return (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y);
    }

    public boolean isAbove(Point p0, Point p1) {
        if (y < p0.y && y < p1.y) {
            return false;
        }
        Vector n = this.sub(p0);
        Vector v = p1.sub(p0);
        Vector result = n.sub(v.dot(v.dot(n)));
        return result.vy > 0;
    }

    Point closest(Point origin, Vector v) {
        Point p2 = origin.add(v);
        return closest(origin, p2);
    }

    Point closest(Point a, Point b) {
        double da = b.y - a.y;
        double db = a.x - b.x;
        double c1 = da * a.x + db * a.y;
        double c2 = -db * this.x + da * this.y;
        double det = da * da + db * db;
        double cx = 0;
        double cy = 0;
        if (det != 0) {
            cx = (da * c1 - db * c2) / det;
            cy = (da * c2 + db * c1) / det;
        } else {
            // The point is already on the line
            cx = this.x;
            cy = this.y;
        }
        return new Point(cx, cy);
    }
}

abstract class Unit {

    public static final double WIDTH = 16_000;

    public static final double HEIGHT = 7_500;

    public static final int VERTICAL = 1;

    public static final int HORIZONTAL = 2;

    public Wizard carrier;

    int grab;

    public Snaffle snaffle;

    public int id;

    public EntityType type;

    int state;

    public boolean dead = false;

    public Point position = new Point(0, 0);

    private Point sposition;

    public double vx;

    public double vy;

    double svx;

    double svy;

    public double radius;

    public double mass;

    public double friction;

    public Unit(EntityType type, double radius, double mass, double friction) {
        this.type = type;
        this.radius = radius;
        this.mass = mass;
        this.friction = friction;
    }

    public void update(int id, int x, int y, int vx, int vy, int state) {
        this.id = id;
        this.position = new Point(x, y);
        this.vx = vx;
        this.vy = vy;
        this.state = state;
    }

    public void move(double t) {
        position = position.add(new Vector(vx * t, vy * t));
    }

    public void thrust(double thrust, double x, double y, double distance) {
        double coef = (thrust / mass) / distance;
        vx += (x - this.position.x) * coef;
        vy += (y - this.position.y) * coef;
    }

    public double speed() {
        return Math.sqrt(vx * vx + vy * vy);
    }

    public double speedTo(Point p) {
        double d = 1.0 / position.distTo(p);
        // vitesse dans la direction du checkpoint - (vitesse orthogonale)^2/dist au cheeckpoint
        double dx = (p.x - this.position.x) * d;
        double dy = (p.y - this.position.y) * d;
        double nspeed = vx * dx + vy * dy;
        double ospeed = dy * vx - dx * vy;
        // TODO c'est quoi ce 5 ???
        return nspeed - (5 * ospeed * ospeed * d);
    }

    public Collision collision(double from) {
        double tx = 2.0;
        double ty = tx;
        if (position.x + vx < radius) {
            tx = (radius - position.x) / vx;
        } else if (position.x + vx > WIDTH - radius) {
            tx = (WIDTH - radius - position.x) / vx;
        }
        if (position.y + vy < radius) {
            ty = (radius - position.y) / vy;
        } else if (position.y + vy > HEIGHT - radius) {
            ty = (HEIGHT - radius - position.y) / vy;
        }
        int dir;
        double t;
        if (tx < ty) {
            dir = HORIZONTAL;
            t = tx + from;
        } else {
            dir = VERTICAL;
            t = ty + from;
        }
        if (t <= 0.0 || t > 1.0) {
            return null;
        }
        return Simulation.collisionsCache[Simulation.collisionsCacheFE++].update(t, this, dir);
    }

    public Collision collision(Unit u, double from) {
        double x2 = position.x - u.position.x;
        double y2 = position.y - u.position.y;
        double r2 = radius + u.radius;
        double vx2 = vx - u.vx;
        double vy2 = vy - u.vy;
        double a = vx2 * vx2 + vy2 * vy2;
        if (a < Player.E) {
            return null;
        }
        double b = -2.0 * (x2 * vx2 + y2 * vy2);
        double delta = b * b - 4.0 * a * (x2 * x2 + y2 * y2 - r2 * r2);
        if (delta < 0.0) {
            return null;
        }
        double t = (b - Math.sqrt(delta)) * (1.0 / (2.0 * a));
        if (t <= 0.0) {
            return null;
        }
        t += from;
        if (t > 1.0) {
            return null;
        }
        return Simulation.collisionsCache[Simulation.collisionsCacheFE++].update(t, this, u);
    }

    public void bounce(Unit u) {
        double mcoeff = (mass + u.mass) / (mass * u.mass);
        double nx = position.x - u.position.x;
        // TODO vector
        double ny = position.y - u.position.y;
        double nxnydeux = nx * nx + ny * ny;
        double dvx = vx - u.vx;
        double dvy = vy - u.vy;
        double product = (nx * dvx + ny * dvy) / (nxnydeux * mcoeff);
        double fx = nx * product;
        double fy = ny * product;
        double m1c = 1.0 / mass;
        double m2c = 1.0 / u.mass;
        vx -= fx * m1c;
        vy -= fy * m1c;
        u.vx += fx * m2c;
        u.vy += fy * m2c;
        // Normalize vector at 100
        double impulse = Math.sqrt(fx * fx + fy * fy);
        if (impulse < 100.0) {
            double min = 100.0 / impulse;
            fx = fx * min;
            fy = fy * min;
        }
        vx -= fx * m1c;
        vy -= fy * m1c;
        u.vx += fx * m2c;
        u.vy += fy * m2c;
    }

    public void bounce(int dir) {
        if (dir == HORIZONTAL) {
            vx = -vx;
        } else {
            vy = -vy;
        }
    }

    public void end() {
        position = new Point(Math.round(position.x), Math.round(position.y));
        vx = Math.round(vx * friction);
        vy = Math.round(vy * friction);
    }

    public boolean can(Unit u) {
        // TODO check the conversion to java
        if (type == EntityType.SNAFFLE) {
            return carrier == null && !dead && u.snaffle == null && u.grab == 0;
        } else if (u.type == EntityType.SNAFFLE) {
            return u.carrier == null && u.dead == false && snaffle == null && grab == 0;
        }
        return true;
    }

    public void save() {
        sposition = position;
        svx = vx;
        svy = vy;
    }

    public void reset() {
        position = sposition;
        vx = svx;
        vy = svy;
    }

    public void move() {
    // TODO Auto-generated method stub
    }

    public void print() {
    // TODO Auto-generated method stub
    }
}

class FastRand {

    Random random;

    int g_seed;

    public FastRand(int seed) {
        g_seed = seed;
        random = new Random(g_seed);
    }

    public void fast_srand(int seed) {
        //Seed the generator
        g_seed = seed;
    }

    public int fastrand() {
        //fastrand routine returns one integer, similar output value range as C lib.
        g_seed = (214013 * g_seed + 2531011);
        return (g_seed >> 16) & 0x7FFF;
    }

    public int fastRandInt(int maxSize) {
        //return fastrand() % maxSize;
        return random.nextInt(maxSize);
    }

    public int fastRandInt(int a, int b) {
        return (a + fastRandInt(b - a));
    }

    public double fastRandDouble() {
        return (fastrand()) / 0x7FFF;
    }

    public double fastRandDouble(double a, double b) {
        return a + ((fastrand()) / 0x7FFF) * (b - a);
    }
}

class AGSolution {

    private static FastRand rand = new FastRand(42);

    public double energy;

    public int moves1[] = new int[AG.DEPTH];

    public int moves2[] = new int[AG.DEPTH];

    public int spellTurn1;

    public Unit spellTarget1;

    public int spell1;

    public int spellTurn2;

    public Unit spellTarget2;

    public int spell2;

    public void randomize() {
        for (int i = 0; i < AG.DEPTH; ++i) {
            moves1[i] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
            moves2[i] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
        }
        spellTurn1 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
        spell1 = Player.rand.fastRandInt(4);
        spellTarget1 = Player.spellTargets[spell1][Player.rand.fastRandInt(Player.spellTargetsFE[spell1])];
        spellTurn2 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
        spell2 = Player.rand.fastRandInt(4);
        spellTarget2 = Player.spellTargets[spell2][Player.rand.fastRandInt(Player.spellTargetsFE[spell2])];
        spell2 += 0;
    }

    public void copy(AGSolution solution) {
        for (int i = 0; i < AG.DEPTH; ++i) {
            moves1[i] = solution.moves1[i];
            moves2[i] = solution.moves2[i];
        }
        spellTurn1 = solution.spellTurn1;
        spell1 = solution.spell1;
        spellTarget1 = solution.spellTarget1;
        spellTurn2 = solution.spellTurn2;
        spell2 = solution.spell2;
        spellTarget2 = solution.spellTarget2;
        this.energy = solution.energy;
    }

    public void mutate() {
        int r = rand.fastRandInt(4);
        if (r == 0) {
            // Change a moves1
            moves1[Player.rand.fastRandInt(AG.DEPTH)] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
        } else if (r == 1) {
            // Change a moves2
            moves2[Player.rand.fastRandInt(AG.DEPTH)] = Player.rand.fastRandInt(Player.ANGLES_LENGTH);
        } else if (r == 2) {
            // Change spell1
            spellTurn1 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
            spell1 = Player.rand.fastRandInt(4);
            spellTarget1 = Player.spellTargets[spell1][Player.rand.fastRandInt(Player.spellTargetsFE[spell1])];
        } else {
            // Change spell2
            spellTurn2 = Player.rand.fastRandInt(AG.SPELL_DEPTH);
            spell2 = Player.rand.fastRandInt(4);
            spellTarget2 = Player.spellTargets[spell2][Player.rand.fastRandInt(Player.spellTargetsFE[spell2])];
            spellTarget2.speed();
        }
    }

    public AGSolution mergeInto(AGSolution child, AGSolution solution) {
        for (int i = 0; i < AG.DEPTH; ++i) {
            if (Player.rand.fastRandInt(2) != 0) {
                child.moves1[i] = solution.moves1[i];
            } else {
                child.moves1[i] = moves1[i];
            }
            if (Player.rand.fastRandInt(2) != 0) {
                child.moves2[i] = solution.moves2[i];
            } else {
                child.moves2[i] = moves2[i];
            }
        }
        if (Player.rand.fastRandInt(2) != 0) {
            child.spellTurn1 = solution.spellTurn1;
            child.spellTarget1 = solution.spellTarget1;
            child.spell1 = solution.spell1;
        } else {
            child.spellTurn1 = spellTurn1;
            child.spellTarget1 = spellTarget1;
            child.spell1 = spell1;
        }
        if (Player.rand.fastRandInt(2) != 0) {
            child.spellTurn2 = solution.spellTurn2;
            child.spellTarget2 = solution.spellTarget2;
            child.spell2 = solution.spell2;
        } else {
            child.spellTurn2 = spellTurn2;
            child.spellTarget2 = spellTarget2;
            child.spell2 = spell2;
        }
        return child;
    }

    void makeNewSolutionFromLastBest(AGSolution best) {
        for (int j = 1; j < AG.DEPTH; ++j) {
            moves1[j - 1] = best.moves1[j];
            moves2[j - 1] = best.moves2[j];
            spellTurn1 = best.spellTurn1;
            spell1 = best.spell1;
            spellTarget1 = best.spellTarget1;
            spellTurn2 = best.spellTurn2;
            spell2 = best.spell2;
            spellTarget2 = best.spellTarget2;
            if (spellTurn1 == 0) {
                spellTurn1 = AG.SPELL_DEPTH - 1;
            } else {
                spellTurn1 -= 1;
            }
            if (spellTurn2 == 0) {
                spellTurn2 = AG.SPELL_DEPTH - 1;
            } else {
                spellTurn2 -= 1;
            }
            if (spellTarget1.dead) {
                spellTurn1 = AG.SPELL_DEPTH - 1;
                spellTarget1 = Player.spellTargets[spell1][rand.fastRandInt(Player.spellTargetsFE[spell1])];
            }
            if (spellTarget2.dead) {
                spellTurn2 = AG.SPELL_DEPTH - 1;
                spellTarget2 = Player.spellTargets[spell2][rand.fastRandInt(Player.spellTargetsFE[spell2])];
            }
        }
    }

    public void randomizeLastMove() {
        moves1[AG.DEPTH - 1] = rand.fastRandInt(Player.ANGLES_LENGTH);
        moves2[AG.DEPTH - 1] = rand.fastRandInt(Player.ANGLES_LENGTH);
    }
}

class AG {

    public static final int POOL = 50;

    public static final int MUTATION = 2;

    public static final int DEPTH = 6;

    static final double COEF_PATIENCE = 0.9;

    public static final int SPELL_DEPTH = 8;

    private static AGSolution best = new AGSolution();

    private static FastRand rand = new FastRand(42);

    public static double patiences[] = new double[DEPTH];

    static {
        for (int i = 0; i < DEPTH; ++i) {
            patiences[i] = Math.pow(COEF_PATIENCE, i);
        }
    }

    static AGSolution[] pool = new AGSolution[POOL];

    static AGSolution[] newPool = new AGSolution[POOL];

    static {
        for (int i = 0; i < POOL; i++) {
            pool[i] = new AGSolution();
            newPool[i] = new AGSolution();
        }
    }

    public static AGSolution evolution() {
        AGSolution base = null;
        if (Player.turn != 0) {
            base = new AGSolution();
            base.makeNewSolutionFromLastBest(best);
        }
        AGSolution[] temp;
        best = new AGSolution();
        AGSolution sol = pool[0];
        sol.randomize();
        Simulation.simulate(sol);
        best.copy(sol);
        AGSolution tempBest = buildFirstGeneration(base, pool, sol);
        double limit = Player.turn != 0 ? 85_000_000 : 800_000_000;
        int generation = 1;
        int bestGeneration = 1;
        int poolFE;
        while (System.nanoTime() - Player.start < limit) {
            // New generation
            // Force the actual best with a mutation to be in the pool
            AGSolution solution = newPool[0];
            solution.copy(tempBest);
            solution.mutate();
            Simulation.simulate(solution);
            if (solution.energy > tempBest.energy) {
                tempBest = solution;
            }
            poolFE = 1;
            while (poolFE < POOL && System.nanoTime() - Player.start < limit) {
                AGSolution child = merge2Solutions(newPool[poolFE++], pool);
                Simulation.simulate(child);
                if (child.energy > tempBest.energy) {
                    tempBest = child;
                }
            }
            temp = pool;
            pool = newPool;
            newPool = temp;
            if (tempBest.energy > best.energy) {
                best.copy(tempBest);
                bestGeneration = generation;
            }
            tempBest = best;
            generation += 1;
        }
        // Play a last time to check some infos
        Player.myWizard1.apply(best, 0, 1);
        Player.myWizard2.apply(best, 0, 2);
        Simulation.dummies();
        Simulation.play();
        Simulation.smyMana = Player.myMana;
        Player.bludgers[0].slast = Player.bludgers[0].last;
        Player.bludgers[1].slast = Player.bludgers[1].last;
        for (int i = 0; i < 16; ++i) {
            Player.spells[i].save();
        }
        Simulation.reset();
        // Burn last generation !!
        //TODO revoir les deletes ...
        //    for (int i = 0; i < poolFE; ++i) {
        //      delete pool[i];
        //    }
        //
        //    delete [] pool;
        //    delete [] newPool;
        System.err.println("Generations : " + generation);
        return best;
    }

    private static AGSolution merge2Solutions(AGSolution futureChild, AGSolution[] pool) {
        int firstIndex = findIndex(pool, -1);
        int secondIndex = findIndex(pool, firstIndex);
        AGSolution child = pool[firstIndex].mergeInto(futureChild, pool[secondIndex]);
        if (rand.fastRandInt(MUTATION) == 0) {
            child.mutate();
        }
        return child;
    }

    private static int findIndex(AGSolution[] pool, int otherThanIndex) {
        int aIndex, bIndex;
        do {
            aIndex = rand.fastRandInt(POOL);
        } while (aIndex == otherThanIndex);
        do {
            bIndex = rand.fastRandInt(POOL);
        } while (bIndex == aIndex && bIndex != otherThanIndex);
        return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
    }

    private static AGSolution buildFirstGeneration(AGSolution base, AGSolution[] pool, AGSolution sol) {
        AGSolution tempBest = sol;
        // First generation
        int startI = 1;
        if (Player.turn != 0) {
            // Populate the POOL with some copy of the previous best one
            for (int i = startI; i < POOL / 5; ++i) {
                AGSolution solution = pool[i];
                solution.copy(base);
                solution.randomizeLastMove();
                Simulation.simulate(solution);
                if (solution.energy > tempBest.energy) {
                    tempBest = solution;
                }
            }
            startI = POOL / 5;
        }
        for (int i = startI; i < POOL; ++i) {
            AGSolution solution = pool[i];
            solution.randomize();
            Simulation.simulate(solution);
            if (solution.energy > tempBest.energy) {
                tempBest = solution;
            }
        }
        if (tempBest.energy > best.energy) {
            best.copy(tempBest);
        }
        tempBest = best;
        return tempBest;
    }
}

class Pole extends Unit {

    public Pole(int id, int x, int y) {
        super(EntityType.POLE, 300, Integer.MAX_VALUE, 0.0);
        position = new Point(x, y);
        this.id = id;
        vx = 0;
        vy = 0;
        dead = false;
    }

    @Override
    public void move() {
    }

    @Override
    public void save() {
    }

    public void reset() {
    }

    ;

    @Override
    public Collision collision(double from) {
        return null;
    }
}

class Player {

    static final double TO_RAD = Math.PI / 180.0;

    public static final int ANGLES_LENGTH = 36;

    static final double ANGLES[] = new double[] { 0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0, 210.0, 220.0, 230.0, 240.0, 250.0, 260.0, 270.0, 280.0, 290.0, 300.0, 310.0, 320.0, 330.0, 340.0, 350.0 };

    public static final double E = 0.001;

    public static double cosAngles[] = new double[ANGLES_LENGTH];

    public static double sinAngles[] = new double[ANGLES_LENGTH];

    public static FastRand rand;

    public static int myTeam;

    public static Wizard[] wizards = new Wizard[4];

    static Unit unitsById[] = new Unit[24];

    public static int unitsFE = 0;

    public static Unit[] units = new Unit[20];

    public static Wizard myWizard1;

    public static Wizard myWizard2;

    public static Wizard hisWizard1;

    public static Wizard hisWizard2;

    public static Point myGoal;

    public static Point hisGoal;

    private static Point mid;

    public static Bludger[] bludgers = new Bludger[2];

    private static Pole[] poles = new Pole[4];

    public static Spell spells[] = new Spell[16];

    public static Unit spellTargets[][] = new Unit[4][20];

    public static int spellTargetsFE[] = new int[4];

    public static int myMana;

    public static int myScore;

    public static int hisScore;

    public static Snaffle[] snaffles = new Snaffle[10];

    public static int snafflesFE;

    public static long start;

    public static int turn = 0;

    public static int victory;

    public static int oldSnafflesFE;

    public static double energy = 0;

    static {
        initConstants();
        rand = new FastRand(73);
    }

    public static void init(int myTeam) {
        Player.myTeam = myTeam;
        createWizards();
        createBludgers();
        createPoles();
        unitsFE = 10;
        int spellsFE = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                spells[spellsFE++] = wizards[j].spells[i];
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        init(in.nextInt());
        while (true) {
            myScore = in.nextInt();
            myMana = in.nextInt();
            hisScore = in.nextInt();
            int hisMana = in.nextInt();
            int entities = in.nextInt();
            start = System.nanoTime();
            int bludgersFE = 0;
            if (turn != 0) {
                resetSnaffles();
            }
            for (int i = 0; i < entities; i++) {
                int id = in.nextInt();
                String entity = in.next();
                EntityType entityType = EntityType.valueOf(entity);
                int x = in.nextInt();
                int y = in.nextInt();
                int vx = in.nextInt();
                int vy = in.nextInt();
                int state = in.nextInt();
                System.err.println("createUnit(" + id + ", \"" + entity + "\", " + x + ", " + y + ", " + vx + ", " + vy + ", " + state + ");");
                Unit unit = null;
                if (entityType == EntityType.WIZARD || entityType == EntityType.OPPONENT_WIZARD) {
                    unit = wizards[id];
                } else if (entityType == EntityType.SNAFFLE) {
                    if (turn == 0) {
                        unit = new Snaffle();
                    } else {
                        unit = unitsById[id];
                    }
                    units[unitsFE++] = unit;
                    snaffles[snafflesFE++] = (Snaffle) unit;
                } else if (entityType == EntityType.BLUDGER) {
                    unit = bludgers[bludgersFE++];
                }
                unit.update(id, x, y, vx, vy, state);
            }
            if (turn == 0) {
                victory = (snafflesFE / 2) + 1;
                affectUnitsToUnitsById();
            }
            // Mise à jour des carriers et des snaffles
            updateWizardsAndSnaffles();
            updateBludgersSpells();
            updatePetrificus();
            updateSnaffleSpells();
            for (int i = 0; i < unitsFE; ++i) {
                units[i].save();
            }
            Simulation.save();
            for (int i = 0; i < 16; ++i) {
                spells[i].checkTarget();
                spells[i].save();
            }
            AGSolution solution = AG.evolution();
            myWizard1.output(solution.moves1[0], solution.spellTurn1, solution.spell1, solution.spellTarget1);
            myWizard2.output(solution.moves2[0], solution.spellTurn2, solution.spell2, solution.spellTarget2);
            Player.turn += 1;
            Player.unitsFE = 10;
            Player.oldSnafflesFE = snafflesFE;
            Player.snafflesFE = 0;
        }
    }

    public static void updateWizardsAndSnaffles() {
        for (int i = 0; i < 4; ++i) {
            wizards[i].updateSnaffle();
        }
    }

    public static void affectUnitsToUnitsById() {
        for (int i = 0; i < unitsFE; ++i) {
            unitsById[units[i].id] = units[i];
        }
    }

    private static void resetSnaffles() {
        for (int i = 0; i < 24; ++i) {
            Unit u = unitsById[i];
            if (u != null && u.type == EntityType.SNAFFLE) {
                u.dead = true;
                u.carrier = null;
            }
        }
    }

    public static void updateSnaffleSpells() {
        // Snaffles pour tous les sorts sauf obliviate
        for (int i = 1; i < 4; ++i) {
            for (int j = 0; j < snafflesFE; ++j) {
                spellTargets[i][spellTargetsFE[i]++] = snaffles[j];
            }
        }
    }

    public static void updatePetrificus() {
        // Wizards ennemis pour petrificus et flipendo
        if (myTeam == 0) {
            spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[2];
            spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[3];
            spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[2];
            spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[3];
        } else {
            spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[0];
            spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[1];
            spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[0];
            spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[1];
        }
    }

    public static void updateBludgersSpells() {
        // Bludgers pour tous les sorts
        for (int i = 0; i < 4; ++i) {
            spellTargets[i][0] = bludgers[0];
            spellTargets[i][1] = bludgers[1];
            spellTargetsFE[i] = 2;
        }
    }

    private static void createWizards() {
        wizards[0] = new Wizard(0);
        wizards[1] = new Wizard(0);
        wizards[2] = new Wizard(1);
        wizards[3] = new Wizard(1);
        units[0] = wizards[0];
        units[1] = wizards[1];
        units[2] = wizards[2];
        units[3] = wizards[3];
        initTeams();
        mid = new Point(8000, 3750);
    }

    private static void createBludgers() {
        bludgers[0] = new Bludger();
        bludgers[1] = new Bludger();
        units[4] = bludgers[0];
        units[5] = bludgers[1];
    }

    private static void createPoles() {
        poles[0] = new Pole(20, 0, 1750);
        poles[1] = new Pole(21, 0, 5750);
        poles[2] = new Pole(22, 16000, 1750);
        poles[3] = new Pole(23, 16000, 5750);
        units[6] = poles[0];
        units[7] = poles[1];
        units[8] = poles[2];
        units[9] = poles[3];
    }

    private static void initTeams() {
        if (myTeam == 0) {
            myWizard1 = wizards[0];
            myWizard2 = wizards[1];
            hisWizard1 = wizards[2];
            hisWizard2 = wizards[3];
            myGoal = new Point(16000, 3750);
            hisGoal = new Point(0, 3750);
        } else {
            myWizard1 = wizards[2];
            myWizard2 = wizards[3];
            hisWizard1 = wizards[0];
            hisWizard2 = wizards[1];
            myGoal = new Point(0, 3750);
            hisGoal = new Point(16000, 3750);
        }
    }

    private static void initConstants() {
        for (int i = 0; i < ANGLES_LENGTH; ++i) {
            cosAngles[i] = Math.cos(ANGLES[i] * TO_RAD);
            sinAngles[i] = Math.sin(ANGLES[i] * TO_RAD);
        }
    }
}
