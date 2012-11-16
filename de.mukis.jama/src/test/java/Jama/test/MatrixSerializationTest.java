package Jama.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;

public class MatrixSerializationTest {

    private final double[] columnwise = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12. };

    // leading dimension of intended test Matrices
    private final int validld = 3;

    private Matrix A;

    private DecimalFormat fmt;
    private ByteArrayOutputStream out;

    @Before
    public void setUp() {
        A = new Matrix(columnwise, validld);
        fmt = new DecimalFormat("0.0000E00");
        fmt.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        out = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws IOException {
        out.close();
    }

    @Test
    public void testPrintReadRoundtrip() throws IOException {
        PrintWriter writer = new PrintWriter(out);
        A.print(writer, fmt, 10);
        writer.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
        Matrix R = Matrix.read(reader);
        assertEquals("Norm must be identical", A.minus(R).norm1(), 0.0, 0.0);
        assertEquals("Matrices must be identical", A, R);
    }

    @Test
    public void testSerializeDeserializeRoundtrip() throws IOException, ClassNotFoundException {
        ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(A);
        oout.close();
        ObjectInputStream sin = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        Matrix R = (Matrix) sin.readObject();
        assertEquals("Norm must be identical", A.minus(R).norm1(), 0.0, 0.0);
        assertEquals("Matrices must be identical", A, R);
    }

}
