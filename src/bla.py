import numpy as np
myfile = open("bla.txt", "r")
myline = myfile.readline()
tab=[]
tab2=[]
while myline:
    myline = myline.split(" ")
    tab.append(np.log(float(myline[0])/0.05)/np.log(2))
    tab2.append(np.log(float(myline[1][0:10])/0.05)/np.log(2))
    myline = myfile.readline()

for i in range(len(tab)):
    print(np.floor(tab[i])," ",np.floor(tab2[i]))

myfile.close()   
