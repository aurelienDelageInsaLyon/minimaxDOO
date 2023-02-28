from matplotlib import pyplot as plt
import numpy as np
lines = []

path = "log.txt"

with open(path) as f:
    lines = f.readlines()

nbIt = 	[]
majorant = []
minorant =[]
for line in lines :
	line = line.split(" ")
	nbIt.append(line[0])
	majorant.append(-float(line[3]))
	minorant.append(-float(line[1]))

plt.plot(majorant,label="pessimistic bound")
plt.plot(minorant,label="optimistic bound")
plt.xlabel("iterations")
plt.ylabel("value")
plt.legend()
#plt.show()
plt.savefig("nonSimplexHolderianBis.pdf")
