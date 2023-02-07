package solver;
import ilog.concert.*;
import ilog.cplex.*;


public class CplexSolver<State,Action,Observation> {
    
    double mamax=0.0;
    private static final long serialVersionUID = 1L;
    public CplexSolver() {
    }
    public void printSolution(double[] solution, int nActions) {
        System.out.println("--------CplexSolver::Printing solutions");
        if (solution != null) {

            double max = solution[nActions];
            System.out.println("Opt: " + (-max+mamax));


            for (int j = 0; j < nActions+1; j++) {
                System.out.print(solution[j] + " ");
            }
            System.out.println("");
        }
        System.out.println("--------CplexSolver::End printing solutions");
    }

    public double getActionMax(double[][] m) {

    /* solver inspired by 
     * https://optimization.mccormick.northwestern.edu/index.php/Matrix_game_(LP_for_game_theory)
     * and
     * https://michaelmadhukalya.wordpress.com/2013/11/06/solving-linear-programming-lp-problems-using-java/
     */
    
    // System.err.printf(" c=%d",counter);

    /**
     * get number of actions for Max and Min player
     */
    int nActionsMax = m.length;
    int nActionsMin = m[0].length;
        
    // Subtract maximum value to the matrix to make it negative.
    // 1- find maximum value
    double mamax = Double.NEGATIVE_INFINITY;
    for(int j=0; j<nActionsMin; j++)
        for(int i=0; i<nActionsMax; i++)
        mamax = (m[i][j]>mamax) ? m[i][j] : mamax;
    // 2- make matrix negative
    for(int j=0; j<nActionsMin; j++)
        for(int i=0; i<nActionsMax; i++)
        m[i][j] -= mamax;
    
    try {   
        /*
         * describe the optimization problem
         */
        
        // Create the modeler/solver object
        IloCplex cplexModel = new IloCplex();
        IloLPMatrix lp = cplexModel.addLPMatrix();

        // Create  variables
        // double[]    lb      = {0.0, 0.0, 0.0};
        // double[]    ub      = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
        // String[]    varname = {"x1", "x2", "x3"};
        // IloNumVar[] x       = model.numVarArray(3, lb, ub, varname);

        // Define columns (variables)

        // x_0, ..., x_{nActionsMax-1} \in [0,1]
        double[] lb = new double[nActionsMax+1];
        double[] ub = new double[nActionsMax+1];
        String[] varname = new String[nActionsMax+1];
        for(int i=0; i<nActionsMax; i++) {
        varname[i] = "x"+i;
        lb[i] = 0.0;
        ub[i] = 1.0;
        }

        // u \in \mathbb{R}+
        // CPLEX.glp_set_col_bnds(lp, nActionsMax+1, CPLEXConstants.GLP_LO, 0,0);
        varname[nActionsMax] = "u";
        lb[nActionsMax] = 0.0; // -Double.MAX_VALUE;
        ub[nActionsMax] = +Double.MAX_VALUE;
        
        IloNumVar[] x = cplexModel.numVarArray(cplexModel.columnArray(lp, nActionsMax+1), lb, ub, varname);

        /* I- objective function
         */
        
        // mINimize u
        double[] objvals = new double[nActionsMax+1];
        objvals[nActionsMax] = 1.0;
        //cplexModel.addMaximize(cplexModel.scalProd(x, objvals));
        cplexModel.addMinimize(cplexModel.scalProd(x, objvals));
        //System.out.println("objective= min "); printVector(objvals); System.out.println("");
        
        /* II- constraints
         */
        //      Create constraints
        
        //System.out.println("// 1- game matrix constraints (1 constraint per opponent action)");
        
        // [from LPex3.java]
        // // add rows to lp
        // double[]   lhs = {-Double.MAX_VALUE, -Double.MAX_VALUE};
        // double[]   rhs = {20.0, 30.0};
        // double[][] val = { {-1.0,  1.0,  1.0},
        //             { 1.0, -3.0,  1.0} };
        // int[][]    ind = { {0, 1, 2},
        //             {0, 1, 2} };
        // lp.addRows(lhs, rhs, ind, val);
        
        //System.out.println("nActionsMin="+nActionsMin);
        
        double[]   lhs = new double[nActionsMin];
        double[]   rhs = new double[nActionsMin];
        double[][] val = new double[nActionsMin][nActionsMax+1];
        int[][] ind = new int[nActionsMin][nActionsMax+1];
        
        for(int j=0; j<nActionsMin; j++) {
        lhs[j] = 0.0;
        rhs[j] = Double.MAX_VALUE;
        for(int i=0; i<nActionsMax; i++) {
            val[j][i] = m[i][j];
            ind[j][i] = i;
        }
        val[j][nActionsMax] = 1.0;
        ind[j][nActionsMax] = nActionsMax;
        }
        lp.addRows(lhs, rhs, ind, val);
        

        //System.out.println("// 2- positive solution constraints");

        lhs = new double[nActionsMax];
        rhs = new double[nActionsMax];
        val = new double[nActionsMax][1];
        ind = new int[nActionsMax][1];
     
        for(int i=0; i<nActionsMax; i++) {
        lhs[i] = 0.0;
        rhs[i] = Double.MAX_VALUE;
        val[i][0] = 1;
        ind[i][0] = i;
        }
        lp.addRows(lhs, rhs, ind, val);

        // // replaced by (i) non-negative-solutions flag in call to optimizer and (ii) non-negative game matrix
        
        // System.out.println("// 3- normalized solution constraint");
            
        lhs = new double[1]; lhs[0] = 1.0;
        rhs = new double[1]; rhs[0] = 1.0;
        val = new double[1][nActionsMax];
        ind = new int[1][nActionsMax];
     
        for(int i=0; i<nActionsMax; i++) {
        val[0][i] = 1;
        ind[0][i] = i;
        }
        lp.addRows(lhs, rhs, ind, val);

        //System.out.println("dLC n="); printVector(dLC); System.out.println("");

        // lp.setMinProblem(true);
            
        /**
         * create and run solver
         */
        // set solver parameters

        // turn off logging
        cplexModel.setOut(null);
        
        //cplexModel.setParam(IloCplex.Param.Threads, 1); // set number of threads to use
        
        //cplexModel.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Sifting);
        //cplexModel.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Concurrent);
        
        if ( cplexModel.solve() ) {
        
        
        double[] solution = cplexModel.getValues(lp);
        //printSolution(solution, nActionsMax);
        /*
        for(Action aMax : problem.getActionsMax(s))  {
            double tmp = solution[aMax.getId()];
            
            if (tmp <0) {
            //System.out.println("[BUGmax]("+counter+") solution= "+aMax+" "+solution[aMax.getId()]);
            tmp=0;
            
            // System.out.println("matrix="); printMatrix(m);
            // System.exit(0);
            // printSolution(solution, nActionsMax);
            
            }
            
            stocActionMax.setWeight((ActionMax) aMax, tmp);
        }
        */
        cplexModel.end();

        //stocActionMax.normalize();
        //return new Pair(stocActionMax, -solution[nActionsMax]+mamax);
        //System.out.println("value : "+ (-solution[nActionsMax]+mamax));
        return (-solution[nActionsMax]+mamax);
        } else {
        System.exit(2);
        }
    }  catch (IloException e) {
        System.err.println("Concert exception '" + e + "' caught");
    }

    //return null;  
    return -Double.POSITIVE_INFINITY;      
    }



    /*
    public Pair<Distribution<Action>,Double> getActionMin(double[][] m) {
        int nActionsMax = m.length;
        int nActionsMin = m[0].length;
        double mimin = Double.POSITIVE_INFINITY;
        for(int j=0; j<nActionsMin; j++)
            for(int i=0; i<nActionsMax; i++)
                mimin = (m[i][j]<mimin) ? m[i][j] : mimin;

        for(int j=0; j<nActionsMin; j++)
            for(int i=0; i<nActionsMax; i++)
                m[i][j] -= mimin;

        try {


            IloCplex cplexModel = new IloCplex();
            IloLPMatrix lp = cplexModel.addLPMatrix();

            double[] lb = new double[nActionsMin+1];
            double[] ub = new double[nActionsMin+1];
            String[] varname = new String[nActionsMin+1];
            for(int i=0; i<nActionsMin; i++) {
                varname[i] = "x"+i;
                lb[i] = 0.0;
                ub[i] = 1.0;
            }


            varname[nActionsMin] = "u";
            lb[nActionsMin] = 0.0;
            ub[nActionsMin] = +Double.MAX_VALUE;

            IloNumVar[] x = cplexModel.numVarArray(cplexModel.columnArray(lp, nActionsMin+1),
                    lb, ub,
                    varname);


            double[] objvals = new double[nActionsMin+1];
            objvals[nActionsMin] = 1.0;

            cplexModel.addMinimize(cplexModel.scalProd(x, objvals));


            double[]   lhs = new double[nActionsMax];
            double[]   rhs = new double[nActionsMax];
            double[][] val = new double[nActionsMax][nActionsMin+1];
            int[][] ind = new int[nActionsMax][nActionsMin+1];

            for(int i=0; i<nActionsMax; i++) {
                lhs[i] = 0.0;
                rhs[i] = Double.MAX_VALUE;
                for(int j=0; j<nActionsMin; j++) {
                    val[i][j] = -m[i][j];
                    ind[i][j] = j;
                }
                val[i][nActionsMin] = 1.0;
                ind[i][nActionsMin] = nActionsMin;
            }
            lp.addRows(lhs, rhs, ind, val);

            lhs = new double[nActionsMin];
            rhs = new double[nActionsMin];
            val = new double[nActionsMin][1];
            ind = new int[nActionsMin][1];

            for(int j=0; j<nActionsMin; j++) {
                lhs[j] = 0.0;
                rhs[j] = Double.MAX_VALUE;
                val[j][0] = 1;
                ind[j][0] = j;
            }
            lp.addRows(lhs, rhs, ind, val);


            lhs = new double[1]; lhs[0] = 1.0;
            rhs = new double[1]; rhs[0] = 1.0;
            val = new double[1][nActionsMin];
            ind = new int[1][nActionsMin];

            for(int j=0; j<nActionsMin; j++) {
                val[0][j] = 1;
                ind[0][j] = j;
            }
            lp.addRows(lhs, rhs, ind, val);


            cplexModel.setOut(null);

            if ( cplexModel.solve() ) {

                Distribution<Action> stocActionMin = new Distribution();
                double[] solution = cplexModel.getValues(lp);

                for (int i = 0; i < this.posg.getActionsJ1().size();i++){
                    Action aMin = (Action) this.posg.getActionsJ1().toArray()[i];
                    double tmp = solution[i];

                    if (tmp <0) {

                        tmp=0;
                         printSolution(solution, nActionsMin);

                    }

                    stocActionMin.setWeight((Action) aMin, tmp);
                }
                cplexModel.end();

                stocActionMin.normalize();
                return new Pair(stocActionMin, solution[nActionsMin]+mimin);

            } else {
                throw new Exception("idk");
            }

        }
        catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
*/

}

