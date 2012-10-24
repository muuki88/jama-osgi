package Jama.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

public class MatrixTest {

	private int n = 20;
	private int nm = 40;
	private int m = 30;
	private Matrix A, B;

	@Before
	public void setUp() {
		A = Matrix.random(n, nm);
		B = Matrix.random(nm, m);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGetArray() {
		double[][] internal = A.getArray();
		assertEquals("Array has not the correct size", n, internal.length);
		assertEquals("Array has not the correct size", nm, internal[0].length);
	}

	@Test
	public void testGetRowDimension() {
		assertEquals("Row dimension is not correct", n, A.getRowDimension());
	}

	@Test
	public void testGetColumnDimension() {
		assertEquals("Column dimension is not correct", nm, A.getColumnDimension());
	}

	@Test
	public void testTimesDouble() {
		assertEquals("Scalarmultiplication with one must be identical matrix", A, A.times(1.0));
	}

	@Test
	public void testTimesEquals() {
		assertEquals("Scalarmultiplication with one must be identical matrix", A, A.timesEquals(1.0));
	}

	@Test
	public void testTimesMatrix() {
		A.times(B);
	}

}
