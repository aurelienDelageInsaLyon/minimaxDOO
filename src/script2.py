from matplotlib import pyplot as plt
import numpy as np
import matplotlib.legend_handler

lines = []

path = "dooBis3d.txt"
'''
with open(path) as f:
    lines = f.readlines()

nbIt = 	[]
majorant = []
minorant =[]
for line in lines :
	line = line.split(" ")
	nbIt.append(line[0])
	majorant.append(float(line[1]))
	minorant.append(float(line[3]))

'''
path = "dooNM1Bis3d.txt"

with open(path) as f:
    lines = f.readlines()

time = []
for line in lines :
	line = line.split(" ")
	print(line)
	time.append(float(line[1][1:]))

timeMedium = np.sum(np.asarray(time))/len(time)

print(time)
print(timeMedium)
print(np.sqrt(np.var(time)))
print(np.mean(time))
'''
plt.plot(majorant)
plt.plot(minorant)
plt.savefig("NM1vsN.pdf")'''


print("######################")
path = "logExp.txt"

with open(path) as f:
    lines = f.readlines()

time = []

tableOfDiff = []
tableOfDiffN = []
current = 0
tmp = []
for line in lines :
	if ("time" in line):
		if (current==0):
			tableOfDiff.append(np.array(tmp,copy=True))
		else : 
			tableOfDiffN.append(np.array(tmp,copy=True))
		tmp = []
		current = (current+1) %2

	if ("Nmax" in line):
		line = line.split(" ")
		if (len(tmp)<200 ):
			tmp.append(float(line[3]))
#print(tableOfDiff)
#plt.plot(tableOfDiff)

for k in range(len(tableOfDiff)):
    if (len(tableOfDiff[k])<200):
        tableOfDiff[k] = np.pad(tableOfDiff[k],(0,200-len(tableOfDiff[k])),'constant',constant_values=(0,np.min(tableOfDiff[k])))
    if (len(tableOfDiffN[k])<200):
        tableOfDiffN[k] = np.pad(tableOfDiffN[k],(0,200-len(tableOfDiffN[k])),'constant',constant_values=(0,np.min(tableOfDiffN[k])))
    #plt.plot(tableOfDiff[k],color='blue',alpha=0.05)
    #plt.plot(tableOfDiffN[k],color='green',alpha=0.05)

#plt.plot(tableOfDiff[0],color='blue',label="Smarter")
#plt.plot(tableOfDiffN[0],color='green',label="Naive")


minCurve = []
maxCurve = []
mediumCurve = []
fig,ax = plt.subplots(1)
for i in range(200):
    mini = 1000;
    maxi = -10000;
    medium= [];
    #for i in range(len(tableOfDiff[k])):
    for k in range(len(tableOfDiff)):
        if (k != 10):
            if (sum(np.isinf(tableOfDiff[k]))>0):
                print("k : ",k,"tableOfDiff[k][i] : ", tableOfDiff[k])
            
            if (tableOfDiff[k][i]<mini):
                mini = tableOfDiff[k][i]
            if (tableOfDiff[k][i]>maxi):
                maxi = tableOfDiff[k][i]
            medium.append(tableOfDiff[k][i])
    med = np.mean(medium)
    minCurve.append(mini)
    maxCurve.append(maxi)
    mediumCurve.append(med)

ax.plot(mediumCurve,color='blue',label='smarter')
ax.fill_between(range(200),maxCurve,minCurve,facecolor='blue',alpha=0.2)
#ax.plot(minCurve)
#ax.plot(maxCurve)



minCurve = []
maxCurve = []
mediumCurve = []
for i in range(200):
    mini = 1000;
    maxi = -10000;
    medium= [];
    #for i in range(len(tableOfDiffN[k])):
    for k in range(len(tableOfDiffN)):
        if (k != 10):
            if (sum(np.isinf(tableOfDiffN[k]))>0):
                print("k : ",k,"tableOfDiffN[k][i] : ", tableOfDiffN[k])
            
            if (tableOfDiffN[k][i]<mini):
                mini = tableOfDiffN[k][i]
            if (tableOfDiffN[k][i]>maxi):
                maxi = tableOfDiffN[k][i]
            medium.append(tableOfDiffN[k][i])
    med = np.mean(medium)
    minCurve.append(mini)
    maxCurve.append(maxi)
    mediumCurve.append(med)

ax.plot(mediumCurve,color='green',label='naive')
ax.fill_between(range(200),maxCurve,minCurve,facecolor='green',alpha=0.2)
#ax.plot(minCurve)
#ax.plot(maxCurve)
'''
X = np.zeros(200)
for i in range(2,200):
  X[i] = 2*1/(2**round(np.log(i)/np.log(2)))

print(X)
plt.plot(X,label='unif')
#plt.show()
'''


path = "logUnif.txt"

with open(path) as f:
    lines = f.readlines()

time = []

tmp = []
'''
for line in lines :
  line = line.split(" ")
  tmp.append(1.82*float(line[3]))
#1.82 is the \alpha-Hölderian constant for the game considered
print(tmp)
plt.plot(tmp,label="Unif")
'''
plt.legend()
plt.xlabel("iterations")
plt.ylabel("distance between the upper- and lower-bound")
#plt.savefig("comparisonNandNM1.pdf")
plt.show()

'''
fig, ax = plt.subplots()
plotN = []
plotNM1 = []
#for k in range(len(tableOfDiff)):
plotN.append(ax.plot(tableOfDiff[0],color='blue',label="sub NM1"))
plotNM1.append(ax.plot(tableOfDiffN[0],color='green',label="sub N"))
ax.legend([plotN[0],plotNM1[0]], ["sub NM1","sub N"], loc='upper left', 
          handler_map = {tuple: matplotlib.legend_handler.HandlerTuple(None)})
fig.show()
'''


#print(np.reshape(tableOfDiff,(
