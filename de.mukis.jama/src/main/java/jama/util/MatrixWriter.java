package jama.util;

import jama.FloatMatrix;
import jama.Matrix;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Writes a Matrix to a specified stream
 * 
 * @author muki
 * 
 */
public class MatrixWriter extends PrintWriter {

    private NumberFormat format;
    private int width;

    public MatrixWriter(OutputStream out) {
        super(out);
        init();
    }

    public MatrixWriter(Writer out) {
        super(out);
        init();
    }

    private void init() {
        setWidth(10);
        setDigits(10);
    }

    /* ============================================= */
    /* =========== Configuration =================== */
    /* ============================================= */

    public MatrixWriter setFormatter(NumberFormat format) {
        this.format = format;
        return this;
    }

    public MatrixWriter setWidth(int width) {
        this.width = width;
        return this;
    }

    public MatrixWriter setDigits(int digits) {
        format = new DecimalFormat();
        ((DecimalFormat) format).setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(digits);
        format.setMinimumFractionDigits(digits);
        format.setGroupingUsed(false);
        return this;
    }

    /* ============================================= */
    /* =========== MatrixWriter API ================ */
    /* ============================================= */

    /**
     * Writes the given matrix.
     * 
     * @param matrix
     */
    public void writeMatrix(Matrix matrix) {
        println(); // start on new line.
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int col = 0; col < matrix.getColumnDimension(); col++) {
                String s = format.format(matrix.get(row, col));

                // At least 1 space
                int padding = Math.max(1, width - s.length());
                for (int k = 0; k < padding; k++) {
                    print(' ');
                }
                print(s);
            }
            println();
        }
        println(); // end with blank line.
        flush();
    }

    /**
     * Writes the given matrix.
     * 
     * @param matrix
     */
    public void writeMatrix(FloatMatrix matrix) {
        println(); // start on new line.
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int col = 0; col < matrix.getColumnDimension(); col++) {
                String s = format.format(matrix.get(row, col));

                // At least 1 space
                int padding = Math.max(1, width - s.length());
                for (int k = 0; k < padding; k++) {
                    print(' ');
                }
                print(s);
            }
            println();
        }
        println(); // end with blank line.
        flush();
    }

    /* ============================================= */
    /* =========== Static API ====================== */
    /* ============================================= */

    /**
     * Generates a string representation of the Matrix. Locale is US.
     * 
     * @param matrix
     * @param width
     * @param digits
     * @return
     */
    public static String toString(Matrix matrix, int width, int digits) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(digits);
        format.setMinimumFractionDigits(digits);
        format.setGroupingUsed(false);

        StringBuilder sb = new StringBuilder();
        sb.append("\n"); // start on new line.
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int col = 0; col < matrix.getColumnDimension(); col++) {
                String s = format.format(matrix.get(row, col));

                // At least 1 space
                int padding = Math.max(1, width - s.length());
                for (int k = 0; k < padding; k++) {
                    sb.append(' ');
                }
                sb.append(s);
            }
            sb.append("\n");
        }
        sb.append("\n"); // end with blank line.
        return sb.toString();
    }
}
