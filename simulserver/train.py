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
import simulator


#------------------------------------------------------------------------------#
# SETUP                                                                        #
#------------------------------------------------------------------------------#

dir_fmt = "/home/remi/Battlecode2013/teams/ga_%d/Weights.java"

pop_size = 20
optimize = ['EXPLORE_MINE', 'ENEMY_HQ', 'ALLY_HQ', 'DROPOFF']
#maps = ['spiral', 'maze1', 'choices', 'bloodbath', 'fused']
maps = ['spiral']
oponent = 'godotbot'
workers = 2

template = ""
with open("../weights.tpl", 'r') as f:
    template = f.read()

weights = {}
with open("../genome.txt", 'r') as f:
    for line in f:
        (key, val) = line.split('=')
        weights[key.strip()] = float(val.strip())

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
    return [200.0 * random.random() - 100.0 for i in range(len(optimize))]


def init_pop(seed):
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
    if random.random() > 0.5:
        gene = random.randrange(len(g))
        g[gene] *= random.random() + (random.randrange(1) - 0.5)

    else:
        x = random.randrange(len(g))
        y = random.randrange(len(g))
        g[x], g[y] = g[y], g[x]

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

    # Mutate about 2 genes for each of newly generated genomes.
    for i in range(split * 2):
        x = random.randrange(split) + split
        newPop[x] = mutate(newPop[x])

    return newPop

def write_weights(name, partial, path):
    full = weights
    full['team'] = 'ga_%d' % name
    for i in range(len(partial)):
        full[optimize[i]] = partial[i]

    with open(path, 'w') as f:
        f.write(template % full)


def write_pop(pop):
    for i in range(len(pop)):
        write_weights(i, pop[i], dir_fmt % i)


#------------------------------------------------------------------------------#
# TRAIN                                                                        #
#------------------------------------------------------------------------------#

def gen_battles():
    battles = []

    for i in range(pop_size):
        for j in range(len(maps)):
            battles.append({'id': i * len(maps) + j,
                           'type': 'local',
                           'bc.game.maps': maps[j],
                           'bc.game.team-a': 'gen_%d' % i,
                           'bc.game.team-b': oponent,
                           'bc.server.save-file': "ga_%d.rms" % i})
    return battles

def bot_index(name):
    return int(name[3:]) if name.startswith('ga_') else -1

def rank_pop(pop, results, generation = 0):
    print results

    cumul = {}

    for result in results:
        g = result['id'] / len(maps)
        team = bot_index(result['combatResult']['winnerTeam'])
        if team >= 0:
            cumul[team] += result['combatResult']['maxRound']

    ranks = [(index, count) for (index, count) in cumul]
    ranks.sort(key = lambda (index, count): count)

    mean = ranks[len(ranks) / 2][1]
    print "generation %d: [%d, %d, %d]" % \
        (generation, ranks[0][1], mean, ranks[len(ranks)-1][1])

    return [pop[index] for (index, count) in ranks]


#------------------------------------------------------------------------------#
# RUN                                                                          #
#------------------------------------------------------------------------------#


pop = init_pop(seed)

gen = 0
while True:
    write_pop(pop)
    config = gen_battles()
    print config
    print

    pop = rank_pop(pop, simulator.QueueBattles(workers, config), gen)
    print

    gen += 1
    pop = evolve(pop)
