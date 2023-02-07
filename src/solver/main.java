package solver;

import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class main {

    public static void main(String[] args) {

        int N = 0;

        double[][] m = new double[3][3];double[][] m2 = new double[3][3];double[][] m3 = new double[3][3];double[][] m4 = new double[3][3];double[][] m5 = new double[3][3];
        double[][] mForCplex = new double[3][3];
        Random rand = new Random();
	rand.setSeed(Double.valueOf(args[4]).longValue());
	System.out.println("seed : " + Double.valueOf(args[4]).longValue());
	for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                m[i][j] = rand.nextDouble();
                mForCplex[i][j] = m[i][j];
                m2[i][j] = m[i][j];
                m3[i][j] = m[i][j];
                m3[i][j] = m[i][j];
                m5[i][j] = m[i][j];
                System.out.print(" " + m[i][j]);
            }
        }

        CplexSolver cplexSolver = new CplexSolver();

        double valueCplex = cplexSolver.getActionMax(mForCplex);


        double normInfX = 0.0;
        double normInfY = 0.0;
        //double normeInf = -1;
        for (int i = 0; i < m.length; i++) {
            double maxi=0.0;
	    for (int j = 0; j < m[0].length; j++) {
		if (maxi<m[i][j])
		    maxi = m[i][j];
            }
	    normInfY+=maxi;
        }

        for (int i = 0; i < m[0].length; i++) {
            double maxj=0.0;
	    for (int j = 0; j < m.length; j++) {
		if (maxj<m[j][i])
		    maxj = m[j][i];
            }
	    normInfX+=maxj;
        }
        //System.out.println("normeInf : " + normeInf);


        double epsilonChoosen = 0.05;
        double constMult = 1.0;
	double repartitionExt=0.5;
	double repartitionInt=0.5;
        System.out.println("args[0] : " + args[0]);	
        if (args.length > 0) {
            constMult = Double.valueOf(args[0]);
            epsilonChoosen = Double.valueOf(args[1]);
	    repartitionExt = Double.valueOf(args[2]);	
	    repartitionInt = Double.valueOf(args[3]);	
	}

        System.out.println("Hölderian constant used : " + normInfX + "," + normInfY);
        System.out.println("epsilon : " + epsilonChoosen);
	System.out.println("choosen repartition : " + repartitionExt + "," + repartitionInt);
	System.out.println("constMult : " + constMult);
        
	System.out.println("\n value Cplex : "+ valueCplex);
	//System.exit(1);        
	for (int i = 1; i<=1;i++){
	    
	    long start = System.currentTimeMillis();
            dooHeuristic4 Doo = new dooHeuristic4(m, normInfX*constMult, normInfY*constMult, epsilonChoosen*repartitionExt, epsilonChoosen*repartitionInt);
            long end = System.currentTimeMillis();
            System.out.println(" value  for epsilon : " + epsilonChoosen + "constMult : " + constMult + "repatition : (" + repartitionExt + "," + repartitionInt + ")" + Doo.finalValue + " time : " + (end-start));
	    if (Math.abs(Doo.finalValue - valueCplex)>epsilonChoosen)
		System.out.println("bug value");
	    
	    
	    long start2 = System.currentTimeMillis();
            //dooHeuristic4 DooHeuristic4 = new dooHeuristic4(m, normInfX*constMult, normInfY*constMult, epsilonChoosen*repartitionExt, epsilonChoosen*repartitionInt);
            uniform_search Unif = new uniform_search(m, normInfX*constMult, normInfY*constMult, epsilonChoosen*repartitionExt, epsilonChoosen*repartitionInt);
	    long end2 = System.currentTimeMillis();
            System.out.println(" valueDooHeuristic  for epsilon : " + epsilonChoosen + "constMult : " + constMult + "repatition : (" + repartitionExt + "," + repartitionInt + ")" + " time : " + (end2-start2));
	    /*
	    if (Math.abs(DooHeuristic4.finalValue - valueCplex)>epsilonChoosen)
		System.out.println("bug value");
	    */
	    /*
            long start2 = System.currentTimeMillis();
	    dooUniform DooUniform = new dooUniform(m, normInfX*constMult, normInfY*constMult);    
            long end2 = System.currentTimeMillis();
    	    System.out.println("time taken by uniform search : " + (end2-start2));
	    */
	    /*	    
	    long start2 = System.currentTimeMillis();
            doo Doo2 = new doo(m2, normeInf*constMult, normeInf*constMult, epsilon*0.75, epsilon*0.25);
            long end2 = System.currentTimeMillis();
            System.out.println(" value  for epsilon (0.75,0.25) : " + Doo2.finalValue + " time : " + (end2-start2));

            long start3 = System.currentTimeMillis();
            doo Doo3 = new doo(m3, normeInf*constMult, normeInf*constMult, epsilon*0.9, epsilon*0.1);
            long end3 = System.currentTimeMillis();
            System.out.println(" value  for epsilon (0.9,0.1) : " + Doo3.finalValue + " time : " + (end3-start3));


            long start3 = System.currentTimeMillis();
            doo Doo3 = new doo(m3, normeInf*constMult, normeInf*constMult, epsilon*0.25, epsilon*0.75);
            long end3 = System.currentTimeMillis();
            System.out.println(" value  for epsilon (0.25,0.75) : " + Doo3.finalValue + " time : " + (end3-start3));


            long start5 = System.currentTimeMillis();
            doo Doo5 = new doo(m5, normeInf*constMult, normeInf*constMult, epsilon*0.1, epsilon*0.9);
            long end5 = System.currentTimeMillis();
            System.out.println(" value  for epsilon (0.1,0.9) : " + Doo5.finalValue + " time : " + (end5-start5));
    */
      	}
    }
}
