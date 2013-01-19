#!/usr/bin/env python
# -*- coding: utf-8 -*- 

# Include the general libraries
import argparse
import Queue
import multiprocessing

from httplib2 import Http
from urllib import urlencode

import CombatRunner

class BattleWorker(multiprocessing.Process):
    def __init__(self, queueWork, queueResult):
        multiprocessing.Process.__init__(self)
        self.queueWork = queueWork
        self.queueResult = queueResult
        self.killReceived = False

    def run(self):
        while not self.killReceived:
            try:
                # Get a job
                job = self.queueWork.get_nowait()
            except Queue.Empty:
                break

            # Perform the task
            print "[BattleWorker] Running job [%s]" % job
            result = { 'id': job['id'] if 'id' in job else 0,
                       'job': job,
                       'success': None,
                       'combatResult': None }

            # In a try/catch so we can return a success = False on errors
            try:
                if job['type'] == "local":
                    combatRunner = CombatRunner.CombatRunner()
                    combatResult = combatRunner.Run(job)
                    result['combatResult'] = combatResult
                    result['success'] = True
                elif job['type'] == "remote":
                    http = Http()
                    resp, combatResult = http.request(job['remoteUrl'], "POST", urlencode(job))

                    if resp['status'] == "200":
                        result['combatResult'] = combatResult
                        result['success'] = True
                    else:
                        result['success'] = False
            except:
                result['success'] = False

            # Send the result back
            self.queueResult.put(result)


def QueueBattles(workerCount, jobs):
    # Push jobs into the battle queue
    queueWork = multiprocessing.Queue()
    for job in jobs:
        queueWork.put(job)

    # Create a queue for the results
    queueResult = multiprocessing.Queue()

    # Spawn workers
    for i in range(workerCount):
        battleWorker = BattleWorker(queueWork, queueResult)
        battleWorker.start()

    results = []
    for i in range(len(jobs)):
        results.append(queueResult.get())

    return results


if __name__ == "__main__":
    argParser = argparse.ArgumentParser(prog = "...",
                                        description = "...")
    argParser.add_argument("-j",
                           dest = "workerCount",
                           action = "store",
                           help = "# of worker processes to spawn",
                           default = 1,
                           type = int)
    args = vars(argParser.parse_args())

    if not args['workerCount']:
        argParser.print_help()
        exit(1)

    battles = []

    # Additional parameters can be added here.. those will be forwarded to the CombatRunner and
    # can be used to tell the system who goes against who and on what map and stuff...
    battles.append({'id': 1, 'type': "local"})
    battles.append({'id': 2, 'type': "local"})
    battles.append({'id': 3, 'type': "local"})
    battles.append({'id': 4, 'type': "local"})
    battles.append({'id': 5, 'type': "remote", 'remoteUrl': "http://127.0.0.1:2222/run_battle"})
    battles.append({'id': 6, 'type': "remote", 'remoteUrl': "http://127.0.0.1:2222/run_battle"})
    battles.append({'id': 7, 'type': "remote", 'remoteUrl': "http://127.0.0.1:2222/run_battle"})
    battles.append({'id': 8, 'type': "remote", 'remoteUrl': "http://127.0.0.1:2222/run_battle"})
    print QueueBattles(args['workerCount'], battles)


