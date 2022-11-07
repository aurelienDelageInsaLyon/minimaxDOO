package util;

import Jama.Matrix;

public class MatrixX extends Matrix {

    /**
     * Needed to "cast" a Matrix in a MatrixX
     *
     * @param that : Matrix one wants to cast to a MatrixX
     */
    public MatrixX(Matrix that) {
	super(that.getArray());
    }
    
    public MatrixX(double[][] A) {
	super(A);
    }

    public MatrixX(double[][] A, int m, int n) {
	super(A,m,n);
    }

    public MatrixX(double[] vals, int m) {
	super(vals, m);
    }
    
    public MatrixX(int m, int n) {
	super(m,n);
    }
    
    public MatrixX(int m, int n, double s) {
	super(m,n,s);
    }
    
    /**
     * Test if two matrices are equal.
     * 
     * @param o  The other matrix you want to compare "this" with.
     * @return true if this and o are the same or equal.
     */
    public boolean equals(Object o) {
	/*System.out.println("MatrixX.equals()");
	System.out.format("%s =? %s%n",
			  System.identityHashCode(this),
			  System.identityHashCode(o));*/
        if (this == o) return true;
        if (!(o instanceof MatrixX)) return false;
	
        MatrixX that = (MatrixX) o;
	
        //return (Arrays.equals(this.getArray(), that.getArray()));
	
	Matrix diff = this.minus(that);
	return diff.normInf() < 0.000001; // ??? Awful handcoded constant
    }

    /**
     * Test if a matrix is GreaterThanOrEqualTo (GE) another one (value by value).
     *
     * @param that The other matrix you want to compare "this" with.
     * @return true if each value of this is GE each value of that.
     */
    public boolean isGE(MatrixX that) {
	if (this == that) return true;
	checkMatrixDimensions(that);
	
	double[][] A = this.getArray();
	double[][] B = that.getArray();
	
	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		if ( A[i][j] < B[i][j] )
		    return false;
	    }
	}

        return true;
    }

    /**
     * Test if a matrix is GreaterThanOrEqualTo (GE) another one (value by value).
     *
     * @param that The other matrix you want to compare "this" with.
     * @return true if each value of this is GE each value of that.
     */
    public boolean isLE(MatrixX that) {
	if (this == that) return true;
	checkMatrixDimensions(that);
	
	double[][] A = this.getArray();
	double[][] B = that.getArray();
	
	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		if ( A[i][j] > B[i][j] )
		    return false;
	    }
	}

        return true;
    }

    /**
     * Returns the absolute value of a matrix
     *
     * @return the same matrix with modified values in its array.
     */
    public MatrixX arrayAbsEquals() {
	double[][] A = this.getArray();

	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		A[i][j] = Math.abs(A[i][j]);
	    }
	}

	return this;
    }

    /**
     * Returns a copy of the matrix, but with its absolute value.
     *
     * @return [cf. description above]
     */
    public MatrixX arrayAbs() {
	MatrixX M = (MatrixX)(this.copy());

	return M.arrayAbsEquals();
    }
    
    /**
     * Returns the maximum (componentwise) of two matrices
     *
     * @param that : matrix to be compared with 'this' matrix
     * @return [...]
     */
    public MatrixX arrayMaxEquals(MatrixX that) {
	double[][] A = this.getArray();
	double[][] B = that.getArray();

	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		A[i][j] = Math.max(A[i][j], B[i][j]);
	    }
	}

	return this;
    }

    /**
     * Returns a copy of the maximum (componentwise) of two matrices
     *
     * @param that : matrix to be compared with 'this' matrix
     * @return [cf. description above]
     */
    public MatrixX arrayMax(MatrixX that) {
	MatrixX M = (MatrixX)(this.copy());

	return M.arrayMaxEquals(that);
    }

    /**
     * Returns the minimum (componentwise) of two matrices
     *
     * @param that : matrix to be compared with 'this' matrix
     * @return [...]
     */
    public MatrixX arrayMinEquals(MatrixX that) {
	double[][] A = this.getArray();
	double[][] B = that.getArray();

	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		A[i][j] = Math.min(A[i][j], B[i][j]);
	    }
	}

	return this;
    }

    /**
     * Returns a copy of the minimum (componentwise) of two matrices
     *
     * @param that : matrix to be compared with 'this' matrix
     * @return [cf. description above]
     */
    public MatrixX arrayMin(MatrixX that) {
	MatrixX M = (MatrixX)(this.copy());

	return M.arrayMinEquals(that);
    }

    /**
     * Compute normInf distance between this and that.
     *
     * @param that : matrix to be compared with 'this' matrix
     */
    public double normInf(MatrixX that) {
	double max = 0.0;
	
	double[][] A = this.getArray();
	double[][] B = that.getArray();

	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		double tmp = Math.abs(A[i][j]- B[i][j]);
		if (tmp > max)
		    max = tmp;
	    }
	}

	return max;
    }
    
    /**
     * Compute normInf distance between this and that.
     *
     * @param that : matrix to be compared with 'this' matrix
     */
    public double norm1(MatrixX that) {
	double[][] A = this.getArray();
	double[][] B = that.getArray();

	double tmp=0.0;
	for (int i=0; i<this.getRowDimension(); i++) {
	    for (int j=0; j<this.getColumnDimension(); j++) {
		tmp += Math.abs(A[i][j]- B[i][j]);
	    }
	}

	return tmp;
    }
    
    /**
     * Print out content of a matrix in a string
     *
     * @return [cf. description above]
     */
    public String toString() {
	//return Arrays.deepToString(this.getArray());
	
	String str = new String();
	double[][] A = this.getArray();

	str += "[";
	for(int i=0; i<A.length; i++) {
	    str += "[";
	    for(int j=0; j<A[0].length; j++) {
		if (j!=0) str += " ";
		str += String.format("%6.2e",A[i][j]);
	    }
	    str += "]";
	}
	str += "]";
	
	return str;
    }
    

/* ------------------------
   Private Methods
 * ------------------------ */

   /**
    * Check if size(A) == size(B) 
    *
    * @param B : matrix to be compared with 'this' matrix
    * @return True if dimensions match 
    */
   private void checkMatrixDimensions (MatrixX B) {
       if (B.getRowDimension() != this.getRowDimension()
	   ||
	   B.getColumnDimension() != this.getColumnDimension()) {
         throw new IllegalArgumentException("MatrixX dimensions must agree.");
      }
   }
}

