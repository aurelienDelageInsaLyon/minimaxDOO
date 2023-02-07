#sed -i 's/Nmax.*//g' logProbaEps.txt
#sed -i '/^$/d' logProbaEps.txt
#sed -i 's/repatition : (.*)/ /g' logProbaEps.txt


import subprocess

import numpy as np
from matplotlib import pyplot as plt


file="tmplog.txt"

subprocess.call("sed -i 's/Nmax.*//g' " +file ,shell=True)
subprocess.call("sed -i '/^$/d' "+ file ,shell=True)
subprocess.call("sed -i 's/)0/) 0/g' "+ file,shell=True)


time = {}
error = {}

listEpsilon=["0.15","0.175","0.2c","0.225","0.25"]
listLambda=["0.8","0.9","1.0","1.1","1.2","1.3"]

eps = "-1";
lipschitzConstant = "-1";
valCplex = -100;

fig,ax = plt.subplots()
fig2,ax2 = plt.subplots()

counterList = -1;
markers = ['o','x','v','^','s','*']
colors = ['r','b','m','g','c','y']

for l in listLambda :
    counterList+=1;
    errorEpsLambda = [[],[],[],[],[]]
    timeEpsLambda = [[],[],[],[],[]]
    varTime = [[],[],[],[],[]]
    varError = [[],[],[],[],[]]
    
    counterEps = 0;
    
    for eps in listEpsilon :
        file = open('tmplog.txt', "r")
        for line in file:
            #if ("new execution" in line):
                #reinitialiser
            
            if ("value Cplex" in line):
                line = line.split(" ")
                valCplex = float(line[4][0:len(line[4])-1])
            if (("epsilon : "+eps) in line and ("constMult : " + l) in line):
                line = line.split(" ")
                print(line) 
                val = line[11]
                
                timeEpsLambda[counterEps].append(float(line[14][0:len(line[14])-1])/1000)
                errorEpsLambda[counterEps].append(np.abs(valCplex - float(val))) 

                
        counterEps+=1;
        #plt.plot(timeEpsLambda/100);
        #plt.plot(errorEpsLambda/100)
        

    for i in range(5):
       
        varTime[i] = np.std(timeEpsLambda[i])/10
        varError[i] = np.std(errorEpsLambda[i])/10
        timeEpsLambda[i] = np.mean(timeEpsLambda[i])
        errorEpsLambda[i] = np.mean(errorEpsLambda[i])
    
        #print("variance : " + str(varError[i]) + "mean : " + str(errorEpsLambda[i]))
    #print( " l : " + l + " time : " + str(timeEpsLambda))
    print("color : " + colors[counterList])
    ax.plot([0.15,0.175,0.20,0.225,0.25],timeEpsLambda,label="lamda :"+l, marker=markers[counterList],color=colors[counterList])
    ax.fill_between([0.15,0.175,0.20,0.225,0.25],timeEpsLambda,np.add(timeEpsLambda,varTime),alpha=0.2,color=colors[counterList])
    ax.fill_between([0.15,0.175,0.20,0.225,0.25],timeEpsLambda,np.add(timeEpsLambda,np.dot(varTime,-1)),alpha=0.2,color=colors[counterList])
    
    
    ax2.plot([0.15,0.175,0.20,0.225,0.25],errorEpsLambda,label="lamda :"+l,marker=markers[counterList],color=colors[counterList])
    ax2.fill_between([0.15,0.175,0.20,0.225,0.25],errorEpsLambda,np.add(errorEpsLambda,varError),alpha=0.2,color=colors[counterList])
    ax2.fill_between([0.15,0.175,0.20,0.225,0.25],errorEpsLambda,np.add(errorEpsLambda,np.dot(varError,-1)),alpha=0.2,color=colors[counterList])

ax.legend()
ax.set_xlabel("epsilon")
ax.set_ylabel("itime (s)")
ax2.legend()
ax2.set_ylabel("real error")
ax2.set_xlabel("epsilon")

fig.savefig("timeDOOFixBug.pdf")
fig2.savefig("errorDOOFixBug.pdf")

plt.show()


file.close()
