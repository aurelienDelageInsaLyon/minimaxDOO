package solver;

import java.util.Random;

public class main
{

	public static void main(String[] args){
		//doo Doo = new doo();
		//dooNonSimplex Doo = new dooNonSimplex();
		//biDooSimple Doo = new biDooSimple();


		double[][] m = new double[4][4];
		Random rand = new Random();

        for (int i = 0; i < m.length; i++) {
    		for (int j = 0; j < m[0].length; j++) {
            	m[i][j] = rand.nextDouble();
            }
        }

        for (int i = 0; i < m.length; i++) {
        	System.out.println("");
    		for (int j = 0; j < m[0].length; j++) {
            	System.out.print(" "+m[i][j]);
            }
        }

      	long start = System.currentTimeMillis();
		dooHeuristic DooHeuristic = new dooHeuristic(m);
		long end = System.currentTimeMillis();

		System.out.println("time doo dooHeuristic : " + (end-start)/1000);
		System.out.println("\n\n\n\n\n ############################ \n\n\n\n\n");

      	long start2 = System.currentTimeMillis();
		doo Doo = new doo(m);
      	long end2 = System.currentTimeMillis();
      	System.out.println("time doo : " + (end2-start2)/1000);

	}
}