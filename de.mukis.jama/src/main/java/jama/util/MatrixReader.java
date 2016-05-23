package jama.util;

import jama.Matrix;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Vector;


/**
 * Reads a Matrix from a Reader
 * 
 * @author muki
 * 
 */
public class MatrixReader extends LineNumberReader {

    public MatrixReader(Reader in) {
        super(in);
    }

    // TODO Get rid of java.util.Vector
    /**
     * 
     * @return Matrix read from the input stream
     * @throws IOException
     */
    public Matrix readMatrix() throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(this);

        // Although StreamTokenizer will parse numbers, it doesn't recognize
        // scientific notation (E or D); however, Double.valueOf does.
        // The strategy here is to disable StreamTokenizer's number parsing.
        // We'll only get whitespace delimited words, EOL's and EOF's.
        // These words should all be numbers, for Double.valueOf to parse.

        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 255);
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.eolIsSignificant(true);

        while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
            // Ignore initial empty lines
        }
        
        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
            throw new IOException("Unexpected EOF on matrix read.");
            
        final Vector<Double> vD = new Vector<Double>();
        do {
            // Read & store 1st row.
            vD.addElement(Double.valueOf(tokenizer.sval)); 
        } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

        // Now we've got the number of columns!
        final int n = vD.size();
        double row[] = new double[n];
        for (int j = 0; j < n; j++) {
            // extract the elements of the 1st row.
            row[j] = vD.elementAt(j).doubleValue();
        }
        
        // Start storing rows instead of columns.
        final Vector<double[]> v = new Vector<double[]>();
        v.addElement(row);
        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
            // While non-empty lines
            v.addElement(row = new double[n]);
            int j = 0;
            do {
                if (j >= n)
                    throw new IOException("Row " + v.size() + " is too long.");
                row[j++] = Double.valueOf(tokenizer.sval).doubleValue();
            } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
            if (j < n)
                throw new IOException("Row " + v.size() + " is too short.");
        }
        final int m = v.size(); // Now we've got the number of rows.
        final double[][] A = new double[m][];
        v.copyInto(A); // copy the rows out of the vector
        return new Matrix(A);
    }

}
