import numpy as np
from matplotlib import pyplot as plt


file = open('logEpsClean.txt', "r")

time = {}
error = {}

listEpsilon=["0.1c","0.125","0.15","0.175","0.2"]
listLambda=["(0.25,0.75)","(0.5,0.5)","(0.75,0.25)"]

eps = "-1";
lipschitzConstant = "-1";
valCplex = -100;

fig,ax = plt.subplots()
fig2,ax2 = plt.subplots()
for l in listLambda :
    
    errorEpsLambda = [[],[],[],[],[]]
    timeEpsLambda = [[],[],[],[],[]]
    varTime = [[],[],[],[],[]]
    varError = [[],[],[],[],[]]
    
    counterEps = 0;
    
    for eps in listEpsilon :
        file = open('logEpsClean.txt', "r")
        for line in file:
            #if ("new execution" in line):
                #reinitialiser
            if ("value Cplex" in line):
                line = line.split(" ")
                valCplex = float(line[3][0:len(line[3])-1])
            if ("value " in line and ("epsilon : "+eps) in line and ("repatition : " + l) in line):
                line = line.split(" ")
                val = line[9]
                print("val : ",val) 
                timeEpsLambda[counterEps].append(float(line[12][0:len(line[12])-1])/1000)
                errorEpsLambda[counterEps].append(np.abs(valCplex - float(val))) 

                print("epsilon : " + str(eps) + " lambda : " + l + " error : " + str(errorEpsLambda))
                
        counterEps+=1;
        #plt.plot(timeEpsLambda/100);
        #plt.plot(errorEpsLambda/100)
        

    for i in range(5):
       
        varTime[i] = np.std(timeEpsLambda[i])
        varError[i] = np.std(errorEpsLambda[i])
        timeEpsLambda[i] = np.mean(timeEpsLambda[i])
        errorEpsLambda[i] = np.mean(errorEpsLambda[i])
    
        #print("variance : " + str(varError[i]) + "mean : " + str(errorEpsLambda[i]))
    #print( " l : " + l + " time : " + str(timeEpsLambda))
    ax.plot([0.1,0.125,0.15,0.175,0.2],timeEpsLambda,label="repartition : "+l)
    #ax.fill_between([0.1,0.125,0.15,0.175,0.2],timeEpsLambda,np.add(timeEpsLambda,varTime))
    #ax.fill_between([0.1,0.125,0.15,0.175,0.2],timeEpsLambda,np.add(timeEpsLambda,np.dot(varTime,-1)))
    
    
    ax2.plot([0.1,0.125,0.15,0.175,0.2],errorEpsLambda,label="repartition : "+l)
    #ax2.fill_between([0.1,0.125,0.15,0.175,0.2],errorEpsLambda,errorEpsLambda,np.add(errorEpsLambda,varError),alpha=0.2)
    #ax2.fill_between([0.1,0.125,0.15,0.175,0.2],errorEpsLambda,np.add(errorEpsLambda,np.dot(varError,-1)),alpha=0.2)

ax.legend()
ax.set_xlabel("epsilon")
ax.set_ylabel("itime (s)")
ax2.legend()
ax2.set_ylabel("real error")
ax2.set_xlabel("epsilon")

#plt.plot()

fig.savefig("time_distribution.pdf")
fig2.savefig("error_distribution.pdf")

plt.show()


file.close()
