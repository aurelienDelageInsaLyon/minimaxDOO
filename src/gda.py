import numpy as np
from matplotlib import pyplot as plt
import proj_simplex

h = 0.0001;
alpha = 0.01;
gamma = 1.0;

def f_matrix(x,y,M):
    #return np.dot(x,np.dot(M,y))
    #return x[0]**2+x[1]**2
    return np.dot(x,y)

def projection_simplex_pivot(v, z=1, random_state=None):
    rs = np.random.RandomState(random_state)
    n_features = len(v)
    U = np.arange(n_features)
    s = 0
    rho = 0
    while len(U) > 0:
        G = []
        L = []
        k = U[rs.randint(0, len(U))]
        ds = v[k]
        for j in U:
            if v[j] >= v[k]:
                if j != k:
                    ds += v[j]
                    G.append(j)
            elif v[j] < v[k]:
                L.append(j)
        drho = len(G) + 1
        if s + ds - (rho + drho) * v[k] < z:
            s += ds
            rho += drho
            U = L
        else:
            U = G
    theta = (s - z) / float(rho)
    return np.maximum(v - theta, 0)


def projection_simplex_sort(v, z=1):
    n_features = v.shape[0]
    u = np.sort(v)[::-1]
    is_sorted= 0;
    if (u[0]!=v[0]):
        is_sorted=1;
    cssv = np.cumsum(u) - z
    ind = np.arange(n_features) + 1
    cond = u - cssv / ind > 0
    rho = ind[cond][-1]
    theta = cssv[cond][-1] / float(rho)
    w = np.maximum(v - theta, 0)
    if (is_sorted == 1):
        return [w[1],w[0]]
    return w

def projection_simplex_bisection(v, z=1, tau=0.000001, max_iter=1000):
    func = lambda x: np.sum(np.maximum(v - x, 0)) - z
    lower = np.min(v) - z / len(v)
    upper = np.max(v)

    for it in range(max_iter):
        midpoint = (upper + lower) / 2.0
        value = func(midpoint)

        if abs(value) <= tau:
            break

        if value <= 0:
            upper = midpoint
        else:
            lower = midpoint

    return np.maximum(v - midpoint, 0)


def derivative(x,y,M):
    dx = []
    dy = []

    for i in range(len(x)):
        hi = np.zeros(len(x))
        hi[i] = h;
        print("dx : ",(f_matrix(np.add(x,hi),y,M)-f_matrix(x,y,M)))
        dx.append((f_matrix(np.add(x,hi),y,M)-f_matrix(x,y,M))/h)

    for i in range(len(y)):
        hi = np.zeros(len(y))
        hi[i] = h;
        dy.append((f_matrix(x,np.add(y,hi),M)-f_matrix(x,y,M))/h)
    
    #print("dx,dy : ",dx,dy)
    return dx,dy


def solve(N,x_init,y_init,M):

    Xs = []
    Ys = []
    Zs = []

    step_number = 0;
    x,y = x_init,y_init
    while (step_number<N):

        oldx = [x[0],x[1]]

        dx,_ = derivative(x,y,M)
        #print("x before projection : ", x + np.multiply(dx,alpha))
        #x = projection_simplex_bisection(np.add(x,+np.multiply(alpha,dx)));
        x = proj_simplex.project_simplex(np.add(x,+np.multiply(alpha,dx)));
        print("x after projection : ", x)
        
        dx,dy = derivative(x,y,M)
        #y = projection_simplex_bisection(np.add(y,-np.multiply(alpha,dy)));
        y = proj_simplex.project_simplex(np.add(y,-np.multiply(alpha,dy)));
        print("y after projection : ", y)

        if (step_number%1==0):
            print("current value : ", f_matrix(x,y,M),"x : " ,x, " y : ",y)
            print("dx,dy:", dx,dy)
        Xs.append(x)
        Ys.append(y)
        Zs.append(f_matrix(x,y,M))

        step_number+=1

    return [Xs,Ys,Zs]
 

#M = np.random.rand(2,2)
#0.95798465 0.15038152
#0.40153524 0.44965923
M = [[0.95798465 ,0.15038152],[0.40153524,0.44965923]] #entered line by line
#x_init = [0.056238,0.943762]
#y_init = [0.349735, 0.650265]
x_init = [0.4,0.6]
y_init = [0.2,0.8]

print(f_matrix(x_init,y_init,M))

N = 1000

[Xs,Ys,Zs] = solve(N,x_init,y_init,M);


'''
plotting the points and surface
'''

x_axis = np.linspace(0,1,100)
y_axis =  np.linspace(0,1,100)

X,Y = np.meshgrid(x_axis,y_axis)
Z = np.zeros([100,100])
for i in range(100):
    for j in range(100):
        Z[i][j] = f_matrix([x_axis[j], 1-x_axis[j]],[y_axis[i],1-y_axis[i]],M)


fig, ax = plt.subplots(subplot_kw={"projection": "3d"})

surf = ax.plot_surface(X,Y,Z,alpha=0.3)

for i in range(N):
    ax.scatter(Xs[i][0],Ys[i][0],Zs[i],color="red")


plt.show()
