import numpy as np

path = "tmpTest.txt"

with open(path) as f:
    lines = f.readlines()

timeDoo = []
timeDooHeuristic = []
for line in lines :
        if ("time doo dooHeuristic :" in line) :
            line = line.split(" ")
            timeDooHeuristic.append(float(line[4]))

        if ("time doo :" in line) :
            line = line.split(" ")
            timeDoo.append(float(line[3]))
#print(timeDoo)
#print(timeDooHeuristic)


timeMediumDoo = np.sum(np.asarray(timeDoo))/len(timeDoo)
#print("time medium doo : " + timeMediumDoo)
print("time medium doo :" + str(np.mean(timeDoo)))
print("variance doo : " + str(np.sqrt(np.var(timeDoo))))
#print(np.mean(timeDoo))


timeMedium = np.sum(np.asarray(timeDooHeuristic))/len(timeDooHeuristic)

#print("time medium doo heuristic : " + timeMedium)
print("time medium doo heuristic :" + str(np.mean(timeDooHeuristic)))
print("variance doo heuristic : " + str(np.sqrt(np.var(timeDooHeuristic))))
