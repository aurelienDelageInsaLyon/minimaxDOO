import numpy as np


def entropie(x):
    return -(x*np.log(x)/np.log(2) + (1-x) * np.log(1-x)/np.log(2))




X = np.linspace(0.00001,0.9999,1000)
Y = np.linspace(0.00001,0.9999,1000)
xMax = 0
yMin= 0
maximin = -10000
for x in X:
    mini=10000
    yMini = -1
    for y in Y:
        value = x+y-2*x*y-0.5-0.5*(entropie(x)+entropie(y))
        #print("value : " + str(value))
        if (value <mini):
            mini = value
            yMini=y
        #print("mini tmp ; " + str(mini))
    if (mini>maximin):
        maximin = mini
        xMax = x
        yMin=yMini
print("maximin : " + str(maximin) + " x : " + str(xMax) + " y : " + str(yMin))
print("\n val : " +  str(xMax+yMin-2*xMax*yMin-0.5-0.5*(entropie(xMax)+entropie(yMin))))

