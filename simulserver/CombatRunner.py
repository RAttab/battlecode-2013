#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import json
import tempfile
import subprocess
import tempfile

# can be tested using:
# import CombatRunner
# cRunner = CombatRunner.CombatRunner()
# print cRunner.ParseAntRunResult(cRunner.AntRunHeadless("/home/myuser/Desktop/Battlecode2013/","bc.conf"))
# will return something like:
# {'maxRound': 500,
# 'raw': [ ... wtv was returned by the ant file command ... ],
# 'winnerName': 'fieldbot',
# 'winnerTeam': 'A'}

class CombatRunner:
    bcTemplateFile = "bc.conf.tmpl"
    bcPath = os.path.expanduser("~/Battlecode2013/")

    def Run(self, config):
        # print "[CombatRunner][Run] With config [%s]" % config

        # Load the bc template config file
        # That template file should have placeholders
        # like: %(mapName)s  (note that the "s" after the parenthesis is required)
        # that mapName value would be replaced by the value of config['mapName']
        with open(self.bcTemplateFile, "r") as fh:
            templateStr = fh.read()

        # Generate a temp config file
        fh, filename = tempfile.mkstemp()

        # Populate the config file based on template and the config param
        with open(filename, "w") as fh:
            fh.write(templateStr % config)

        # **
        # Generate weights here? or those get generated in the simulator.py file
        # before calling QueueBattles ?
        # **

        # Run the combat and parse the results
        result = self.ParseAntRunResult(self.AntRunHeadless(self.bcPath, filename))

        print "%s vs %s on %s -> %s wins in %d rounds" % \
            (config['bc.game.team-a'], config['bc.game.team-b'],
            config['bc.game.maps'], result['winnerName'], result['maxRound'])

        # Delete the temp config file
        fh.close()
        os.remove(filename)

        return result

    def ParseAntRunResult(self, antResult):
        result = { "maxRound": 0,
                   "winnerName": "",
                   "winnerTeam": "",
                   "raw": antResult }

        for line in antResult:
            line = line.strip()
            roundStr = "[TRAIN] Round "

            if line.startswith("[java] [server]") and line.endswith("wins"):
                winnerItems = line.split(" ")
                result['winnerName'] = winnerItems[-3]
                result['winnerTeam'] = winnerItems[-2][1]
            elif line.count(roundStr) > 0:
                split = line.split(" ")

                # This will technically be run multiple times... but wtv.. the
                # last entry is always the max one anyway
                result['maxRound'] = int(split[len(split) - 1])

        return result

    def AntRunHeadless(self, path, configFile):
        command = "cd %s && ant file -Dconfig=%s" % (path, configFile)
        # print "Running command [%s]" % command
        return subprocess.check_output(command, shell=True).split("\n")

    def RunBattle(self):
        print "Starting RunBattle"

        print "Configuring battle..."

        tempFolder = tempfile.mkdtemp()
        if not tempFolder or len(tempFolder) < 10:
            self.ReturnErr("[RunBattle] Could not create a temp folder")
            return # *** #
        print "Created temp folder [%s]" % tempFolder

        config = json.loads(self.args['client']['contentRaw'])
        """ %(BLAH)   """


        print "Copying battlecode installation"
        print "Generating bc.conf"
        print "Creating a weight file"
        print "Running battle"
        print "Massaging results"


        print "Removing temp folder..."
        if tempFolder and len(tempFolder) >= 10: # Being paranoid about removing system files...
            shutil.rmtree(tempFolder)

        print "RunBattle done"
        self.ReturnJson({})
