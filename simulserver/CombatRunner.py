#!/usr/bin/env python
# -*- coding: utf-8 -*- 

import json
import shutil
import tempfile
import subprocess


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
    
    def Run(self, config):
        print "[CombatRunner][Run] With config [%s]" % config

    def ParseAntRunResult(self, antResult):
        result = { "maxRound": 0,
                   "winnerName": "",
                   "winnerTeam": "",
                   "raw": antResult }

        for line in antResult:
            line = line.strip()

            if line.startswith("[java] [server]") and line.endswith("wins"):
                winnerItems = line.split(" ")
                result['winnerName'] = winnerItems[-3]
                result['winnerTeam'] = winnerItems[-2][1]
            elif line.startswith("[java] Round"):
                # This will technically be run multiple times... but wtv.. the
                # last entry is always the max one anyway
                result['maxRound'] = int(line.split(" ")[2])

        return result

    def AntRunHeadless(self, path, configFile):
        command = "cd %s && ant file -Dconfig=%s" % (path, configFile)
        print "Running command [%s]" % command
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
