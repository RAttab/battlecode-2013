#-------------------------------------------------------------- -*- python -*- #
# train.py
# Remi Attab (remi.attab@gmail.com), 19 Jan 2013
# FreeBSD-style copyright and disclaimer apply
#
# Genetic algorithm to train weights in the Weight.iava file.
#------------------------------------------------------------------------------#

import os
import re
import sys
import random
import shutil
import simulator


#------------------------------------------------------------------------------#
# SETUP                                                                        #
#------------------------------------------------------------------------------#

install_dir = os.path.expanduser("~/Battlecode2013")
bin_dir = install_dir + "/bin"
team_dir = install_dir + "/teams"
ga_path = team_dir + "/ga_%d/Weights.java"

populations = 3
pop_size = 20

# optimize = ['EXPLORE_MINE', 'ENEMY_HQ', 'CAPTURE']
maps = ['maze1', 'lilforts', 'jacket', 'smiles', 'Cairo', 'Chicago', 'caves',\
    'questionable', 'spiral', 'zigzag', 'zugzwang', 'simple', 'backdooor', \
    'choices', 'smiley', 'boxes', 'lol', 'five', 'map-20-20-empty', 'map-20-20-mountain'\
    'map-25-23-encamp_or_direct', 'map-20-20-square-o-doom', 'map-35-35-sneaky', 'SpecialMarc']

control_oponents = ['team216', 'nuclear', 'rusher']
control_maps = ['Chicago', 'Wellington', 'map-20-20-empty']

workers = 15

template = ""
with open("../weights.tpl", 'r') as f:
    template = f.read()

weights = {}
with open("../genome.txt", 'r') as f:
    for line in f:
        (key, val) = line.split('=')
        weights[key.strip()] = float(val.strip())


optimize = weights.keys()
seed = [weights[w] for w in optimize]


#------------------------------------------------------------------------------#
# DEBUG                                                                        #
#------------------------------------------------------------------------------#

def print_pop(pop, generation = 0):
    print "Population %d (len=%d):" % (generation, len(pop))
    for i in range(len(pop)):
        print "  %d: %s" % (i, pop[i])


#------------------------------------------------------------------------------#
# GENETIC ALGO                                                                 #
#------------------------------------------------------------------------------#

def random_genome():
    return [float(random.randrange(-100, 100)) for i in range(len(optimize))]


def init_pop(seed):
    # split = pop_size / 2
    # pop = [tweak(seed) for i in range(split - 1) ]
    # pop.extend([random_genome() for i in range(split)])

    pop = [random_genome() for i in range(pop_size - 1)]
    pop.append(seed)
    return pop


def crossover(x, y):
    split = random.randrange(len(x))

    g = [x[i] for i in range(split)]
    g.extend([y[i] for i in range(split, len(y))])

    assert len(g) == len(x)
    assert len(g) == len(y)

    return g

def mutate(g):
    g = list(g)

    if random.random() > 0.5:
        gene = random.randrange(len(g))
        g[gene] *= random.random() + (random.randrange(4) - 2)

    else:
        x = random.randrange(len(g))
        y = random.randrange(len(g))
        g[x], g[y] = g[y], g[x]

    return g

def tweak(g):
    g = list(g)
    gene = random.randrange(len(g))
    g[gene] *= random.random() + 0.5
    return g


def evolve(pop):
    split = len(pop) / 2

    # Promote half the population
    newPop = [pop[i] for i in range(split)]

    # Generate a new half using the promoted genes
    for i in range(split):
        x = random.randrange(split)
        y = random.randrange(split)
        newPop.append(crossover(pop[x], pop[y]))

    # Apply tweaks to the top 50%
    for i in range(1, split):
        newPop[i] = tweak(newPop[i])

    # Mutate about 2 genes for each of newly generated genomes.
    for i in range(split * 2):
        x = random.randrange(split) + split
        newPop[x] = mutate(newPop[x])

    return newPop

def write_weights(name, partial, path):
    full = weights
    full['team'] = name
    for i in range(len(partial)):
        full[optimize[i]] = partial[i]

    with open(path, 'w') as f:
        f.write(template % full)


def write_pop(pop):
    for i in range(len(pop)):
        write_weights('ga_%d' % i, pop[i], ga_path % i)


#------------------------------------------------------------------------------#
# TRAIN                                                                        #
#------------------------------------------------------------------------------#

def gen_battles():
    battles = []

    for i in range(populations):
        for j in range(pop_size-1):
            for k in range(j+1, pop_size):
                x = i*pop_size+j
                y = i*pop_size+k
                m = random.randrange(len(maps))
                battles.append({'id': (x+1)*1000 + y,
                                'type': 'local',
                                'bc.game.maps': maps[m],
                                'bc.game.team-a': 'ga_{}'.format(x),
                                'bc.game.team-b': 'ga_{}'.format(y),
                                'bc.server.save-file': "ga_{}_vs_ga{}.rms".format(x,y)})
    for i in range(populations*pop_size):
        for j in range(control_oponents):
                battles.append({'id': (i+1)*1000 + j + populations*pop_size,
                                'type': 'local',
                                'bc.game.maps': control_maps[j],
                                'bc.game.team-a': 'ga_{}'.format(i),
                                'bc.game.team-b': '{}'.format(control_oponents[j]),
                                'bc.server.save-file': "ga_{}_vs_{}.rms".format(i, control_oponents[j])})

    return battles


def bot_index(name):
    return int(name[3:]) if name.startswith('ga_') else -1

def parse_result(i):
    b = i % 1000
    a = (i-b) / 1000
    a = "ga_{}".format(a)
    if b > populations * pop_size:
        b = control_oponents[b - populations*pop_size]
    else:
        b = "ga_{}".format(b)

def rank_pop(pop, results, generation = 0):
    cumul = {}

    for result in results:
        a, b = parse_result(result['id'])

        winner = result['combatResult']['winnerTeam']
        rounds = result['combatResult']['maxRound']
        s = 100 + (2500. - rounds)/rounds # round number just used for tiebreaks
        if winner == 'A':
            if a[:2] == 'ga':
                a in cumul ? cumul[a] = s : cumul[a] += s
        else
            if b[:2] == 'ga':
                b in cumul ? cumul[b] = s : cumul[b] += s

        g = result['id'] / len(maps)

        assert winner == 'B' or winner == 'A'


    ranks = [(index, cumul[index]) for index in cumul.keys()]
    ranks.sort(key = lambda (index, count): count)

    print "\nRANKS:"
    for i in range(len(ranks)):
        print "  %d: %s" % (i, ranks[i])
    print ""

    mean = ranks[len(ranks) / 2][1]
    print "generation %d: [%d, %d, %d]" % \
        (generation, ranks[0][1], mean, ranks[len(ranks)-1][1])

    return [pop[index] for (index, count) in ranks]


#------------------------------------------------------------------------------#
# RUN                                                                          #
#------------------------------------------------------------------------------#

print "Optimizing: %s" % optimize
print "Maps: %s" % maps

pop = init_pop(seed)

gen = 0
while True:
#    print_pop(pop, gen)

    write_pop(pop)
    config = gen_battles()
    shutil.rmtree(bin_dir)

    pop = rank_pop(pop, simulator.QueueBattles(workers, config), gen)
    write_weights("team216", pop[0], "../weights/Weights_%d.java" % gen)

    gen += 1
    pop = evolve(pop)
