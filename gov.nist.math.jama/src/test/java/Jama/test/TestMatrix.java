package Jama.test;

import Jama.Matrix;

/**
 * TestMatrix tests the functionality of the Jama Matrix class and associated
 * decompositions.
 * <P>
 * Run the test from the command line using <BLOCKQUOTE>
 * 
 * <PRE>
 * <CODE>
 *  java Jama.test.TestMatrix 
 * </CODE>
 * </PRE>
 * 
 * </BLOCKQUOTE> Detailed output is provided indicating the functionality being
 * tested and whether the functionality is correctly implemented. Exception
 * handling is also tested.
 * <P>
 * The test is designed to run to completion and give a summary of any
 * implementation errors encountered. The final output should be: <BLOCKQUOTE>
 * 
 * <PRE>
 * <CODE>
 *       TestMatrix completed.
 *       Total errors reported: n1
 *       Total warning reported: n2
 * </CODE>
 * </PRE>
 * 
 * </BLOCKQUOTE> If the test does not run to completion, this indicates that
 * there is a substantial problem within the implementation that was not
 * anticipated in the test design. The stopping point should give an indication
 * of where the problem exists.
 **/
public class TestMatrix {
	public static void main(String argv[]) {
		Matrix A, B, C, Z, O, I, R, S, X, SUB, M, T, SQ, DEF, SOL;
		// Uncomment this to test IO in a different locale.
		// Locale.setDefault(Locale.GERMAN);
		double tmp, s;

		int nonconformld = 4; /*
								* leading dimension which is valid, but
								* nonconforming
								*/

		/**
		 * Array-like methods: minus minusEquals plus plusEquals arrayLeftDivide
		 * arrayLeftDivideEquals arrayRightDivide arrayRightDivideEquals
		 * arrayTimes arrayTimesEquals uminus
		 **/

		/*
		print("\nTesting array-like methods...\n");
		S = new Matrix(columnwise, nonconformld);
		R = Matrix.random(A.getRowDimension(), A.getColumnDimension());
		A = R;
		try {
			S = A.minus(S);
			errorCount = try_failure(errorCount, "minus conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("minus conformance check... ", "");
		}
		if (A.minus(R).norm1() != 0.) {
			errorCount = try_failure(errorCount, "minus... ",
					"(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
		} else {
			try_success("minus... ", "");
		}
		A = R.copy();
		A.minusEquals(R);
		Z = new Matrix(A.getRowDimension(), A.getColumnDimension());
		try {
			A.minusEquals(S);
			errorCount = try_failure(errorCount, "minusEquals conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("minusEquals conformance check... ", "");
		}
		if (A.minus(Z).norm1() != 0.) {
			errorCount = try_failure(errorCount, "minusEquals... ",
					"(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
		} else {
			try_success("minusEquals... ", "");
		}

		A = R.copy();
		B = Matrix.random(A.getRowDimension(), A.getColumnDimension());
		C = A.minus(B);
		try {
			S = A.plus(S);
			errorCount = try_failure(errorCount, "plus conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("plus conformance check... ", "");
		}
		try {
			check(C.plus(B), A);
			try_success("plus... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "plus... ", "(C = A - B, but C + B != A)");
		}
		C = A.minus(B);
		C.plusEquals(B);
		try {
			A.plusEquals(S);
			errorCount = try_failure(errorCount, "plusEquals conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("plusEquals conformance check... ", "");
		}
		try {
			check(C, A);
			try_success("plusEquals... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "plusEquals... ", "(C = A - B, but C = C + B != A)");
		}
		A = R.uminus();
		try {
			check(A.plus(R), Z);
			try_success("uminus... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "uminus... ", "(-A + A != zeros)");
		}
		A = R.copy();
		O = new Matrix(A.getRowDimension(), A.getColumnDimension(), 1.0);
		C = A.arrayLeftDivide(R);
		try {
			S = A.arrayLeftDivide(S);
			errorCount = try_failure(errorCount, "arrayLeftDivide conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("arrayLeftDivide conformance check... ", "");
		}
		try {
			check(C, O);
			try_success("arrayLeftDivide... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "arrayLeftDivide... ", "(M.\\M != ones)");
		}
		try {
			A.arrayLeftDivideEquals(S);
			errorCount = try_failure(errorCount, "arrayLeftDivideEquals conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("arrayLeftDivideEquals conformance check... ", "");
		}
		A.arrayLeftDivideEquals(R);
		try {
			check(A, O);
			try_success("arrayLeftDivideEquals... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "arrayLeftDivideEquals... ", "(M.\\M != ones)");
		}
		A = R.copy();
		try {
			A.arrayRightDivide(S);
			errorCount = try_failure(errorCount, "arrayRightDivide conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("arrayRightDivide conformance check... ", "");
		}
		C = A.arrayRightDivide(R);
		try {
			check(C, O);
			try_success("arrayRightDivide... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "arrayRightDivide... ", "(M./M != ones)");
		}
		try {
			A.arrayRightDivideEquals(S);
			errorCount = try_failure(errorCount, "arrayRightDivideEquals conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("arrayRightDivideEquals conformance check... ", "");
		}
		A.arrayRightDivideEquals(R);
		try {
			check(A, O);
			try_success("arrayRightDivideEquals... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "arrayRightDivideEquals... ", "(M./M != ones)");
		}
		A = R.copy();
		B = Matrix.random(A.getRowDimension(), A.getColumnDimension());
		try {
			S = A.arrayTimes(S);
			errorCount = try_failure(errorCount, "arrayTimes conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("arrayTimes conformance check... ", "");
		}
		C = A.arrayTimes(B);
		try {
			check(C.arrayRightDivideEquals(B), A);
			try_success("arrayTimes... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "arrayTimes... ", "(A = R, C = A.*B, but C./B != A)");
		}
		try {
			A.arrayTimesEquals(S);
			errorCount = try_failure(errorCount, "arrayTimesEquals conformance check... ", "nonconformance not raised");
		} catch (IllegalArgumentException e) {
			try_success("arrayTimesEquals conformance check... ", "");
		}
		A.arrayTimesEquals(B);
		try {
			check(A.arrayRightDivideEquals(B), R);
			try_success("arrayTimesEquals... ", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "arrayTimesEquals... ", "(A = R, A = A.*B, but A./B != R)");
		}

		 // I/O methods: read print serializable: writeObject readObject
		print("\nTesting I/O methods...\n");
		try {
			DecimalFormat fmt = new DecimalFormat("0.0000E00");
			fmt.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

			PrintWriter FILE = new PrintWriter(new FileOutputStream("JamaTestMatrix.out"));
			A.print(FILE, fmt, 10);
			FILE.close();
			R = Matrix.read(new BufferedReader(new FileReader("JamaTestMatrix.out")));
			if (A.minus(R).norm1() < .001) {
				try_success("print()/read()...", "");
			} else {
				errorCount = try_failure(errorCount, "print()/read()...", "Matrix read from file does not match Matrix printed to file");
			}
		} catch (java.io.IOException ioe) {
			warningCount = try_warning(warningCount, "print()/read()...",
					"unexpected I/O error, unable to run print/read test;  check write permission in current directory and retry");
		} catch (Exception e) {
			try {
				e.printStackTrace(System.out);
				warningCount = try_warning(warningCount, "print()/read()...", "Formatting error... will try JDK1.1 reformulation...");
				DecimalFormat fmt = new DecimalFormat("0.0000");
				PrintWriter FILE = new PrintWriter(new FileOutputStream("JamaTestMatrix.out"));
				A.print(FILE, fmt, 10);
				FILE.close();
				R = Matrix.read(new BufferedReader(new FileReader("JamaTestMatrix.out")));
				if (A.minus(R).norm1() < .001) {
					try_success("print()/read()...", "");
				} else {
					errorCount = try_failure(errorCount, "print()/read() (2nd attempt) ...",
							"Matrix read from file does not match Matrix printed to file");
				}
			} catch (java.io.IOException ioe) {
				warningCount = try_warning(warningCount, "print()/read()...",
						"unexpected I/O error, unable to run print/read test;  check write permission in current directory and retry");
			}
		}

		R = Matrix.random(A.getRowDimension(), A.getColumnDimension());
		String tmpname = "TMPMATRIX.serial";
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpname));
			out.writeObject(R);
			ObjectInputStream sin = new ObjectInputStream(new FileInputStream(tmpname));
			A = (Matrix) sin.readObject();

			try {
				check(A, R);
				try_success("writeObject(Matrix)/readObject(Matrix)...", "");
			} catch (java.lang.RuntimeException e) {
				errorCount = try_failure(errorCount, "writeObject(Matrix)/readObject(Matrix)...", "Matrix not serialized correctly");
			}
		} catch (java.io.IOException ioe) {
			warningCount = try_warning(warningCount, "writeObject()/readObject()...",
					"unexpected I/O error, unable to run serialization test;  check write permission in current directory and retry");
		} catch (Exception e) {
			errorCount = try_failure(errorCount, "writeObject(Matrix)/readObject(Matrix)...", "unexpected error in serialization test");
		}

		 //LA methods: transpose times cond rank det trace norm1 norm2 normF
		  //normInf solve solveTranspose inverse chol eig lu qr svd

		print("\nTesting linear algebra methods...\n");
		A = new Matrix(columnwise, 3);
		T = new Matrix(tvals);
		T = A.transpose();
		try {
			check(A.transpose(), T);
			try_success("transpose...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "transpose()...", "transpose unsuccessful");
		}
		A.transpose();
		try {
			check(A.norm1(), columnsummax);
			try_success("norm1...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "norm1()...", "incorrect norm calculation");
		}
		try {
			check(A.normInf(), rowsummax);
			try_success("normInf()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "normInf()...", "incorrect norm calculation");
		}
		try {
			check(A.normF(), Math.sqrt(sumofsquares));
			try_success("normF...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "normF()...", "incorrect norm calculation");
		}
		try {
			check(A.trace(), sumofdiagonals);
			try_success("trace()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "trace()...", "incorrect trace calculation");
		}
		try {
			check(A.getMatrix(0, A.getRowDimension() - 1, 0, A.getRowDimension() - 1).det(), 0.);
			try_success("det()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "det()...", "incorrect determinant calculation");
		}
		SQ = new Matrix(square);
		try {
			check(A.times(A.transpose()), SQ);
			try_success("times(Matrix)...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "times(Matrix)...", "incorrect Matrix-Matrix product calculation");
		}
		try {
			check(A.times(0.), Z);
			try_success("times(double)...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "times(double)...", "incorrect Matrix-scalar product calculation");
		}

		A = new Matrix(columnwise, 4);
		QRDecomposition QR = A.qr();
		R = QR.getR();
		try {
			check(A, QR.getQ().times(R));
			try_success("QRDecomposition...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "QRDecomposition...", "incorrect QR decomposition calculation");
		}
		SingularValueDecomposition SVD = A.svd();
		try {
			check(A, SVD.getU().times(SVD.getS().times(SVD.getV().transpose())));
			try_success("SingularValueDecomposition...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "SingularValueDecomposition...", "incorrect singular value decomposition calculation");
		}
		DEF = new Matrix(rankdef);
		try {
			check(DEF.rank(), Math.min(DEF.getRowDimension(), DEF.getColumnDimension()) - 1);
			try_success("rank()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "rank()...", "incorrect rank calculation");
		}
		B = new Matrix(condmat);
		SVD = B.svd();
		double[] singularvalues = SVD.getSingularValues();
		try {
			check(B.cond(), singularvalues[0] / singularvalues[Math.min(B.getRowDimension(), B.getColumnDimension()) - 1]);
			try_success("cond()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "cond()...", "incorrect condition number calculation");
		}
		int n = A.getColumnDimension();
		A = A.getMatrix(0, n - 1, 0, n - 1);
		A.set(0, 0, 0.);
		LUDecomposition LU = A.lu();
		try {
			check(A.getMatrix(LU.getPivot(), 0, n - 1), LU.getL().times(LU.getU()));
			try_success("LUDecomposition...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "LUDecomposition...", "incorrect LU decomposition calculation");
		}
		X = A.inverse();
		try {
			check(A.times(X), Matrix.identity(3, 3));
			try_success("inverse()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "inverse()...", "incorrect inverse calculation");
		}
		O = new Matrix(SUB.getRowDimension(), 1, 1.0);
		SOL = new Matrix(sqSolution);
		SQ = SUB.getMatrix(0, SUB.getRowDimension() - 1, 0, SUB.getRowDimension() - 1);
		try {
			check(SQ.solve(SOL), O);
			try_success("solve()...", "");
		} catch (java.lang.IllegalArgumentException e1) {
			errorCount = try_failure(errorCount, "solve()...", e1.getMessage());
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "solve()...", e.getMessage());
		}
		A = new Matrix(pvals);
		CholeskyDecomposition Chol = A.chol();
		Matrix L = Chol.getL();
		try {
			check(A, L.times(L.transpose()));
			try_success("CholeskyDecomposition...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "CholeskyDecomposition...", "incorrect Cholesky decomposition calculation");
		}
		X = Chol.solve(Matrix.identity(3, 3));
		try {
			check(A.times(X), Matrix.identity(3, 3));
			try_success("CholeskyDecomposition solve()...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "CholeskyDecomposition solve()...", "incorrect Choleskydecomposition solve calculation");
		}
		EigenvalueDecomposition Eig = A.eig();
		Matrix D = Eig.getD();
		Matrix V = Eig.getV();
		try {
			check(A.times(V), V.times(D));
			try_success("EigenvalueDecomposition (symmetric)...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "EigenvalueDecomposition (symmetric)...",
					"incorrect symmetric Eigenvalue decomposition calculation");
		}
		A = new Matrix(evals);
		Eig = A.eig();
		D = Eig.getD();
		V = Eig.getV();
		try {
			check(A.times(V), V.times(D));
			try_success("EigenvalueDecomposition (nonsymmetric)...", "");
		} catch (java.lang.RuntimeException e) {
			errorCount = try_failure(errorCount, "EigenvalueDecomposition (nonsymmetric)...",
					"incorrect nonsymmetric Eigenvalue decomposition calculation");
		}
		*/

	}

}
