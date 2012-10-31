package Jama.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StreamTokenizer;

import Jama.Matrix;

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
        java.util.Vector v = new java.util.Vector();

        // Ignore initial empty lines
        while (tokenizer.nextToken() == StreamTokenizer.TT_EOL)
            ;
        if (tokenizer.ttype == StreamTokenizer.TT_EOF)
            throw new java.io.IOException("Unexpected EOF on matrix read.");
        do {
            v.addElement(Double.valueOf(tokenizer.sval)); // Read & store 1st
                                                          // row.
        } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);

        int n = v.size(); // Now we've got the number of columns!
        double row[] = new double[n];
        for (int j = 0; j < n; j++)
            // extract the elements of the 1st row.
            row[j] = ((Double) v.elementAt(j)).doubleValue();
        v.removeAllElements();
        v.addElement(row); // Start storing rows instead of columns.
        while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
            // While non-empty lines
            v.addElement(row = new double[n]);
            int j = 0;
            do {
                if (j >= n)
                    throw new java.io.IOException("Row " + v.size() + " is too long.");
                row[j++] = Double.valueOf(tokenizer.sval).doubleValue();
            } while (tokenizer.nextToken() == StreamTokenizer.TT_WORD);
            if (j < n)
                throw new java.io.IOException("Row " + v.size() + " is too short.");
        }
        int m = v.size(); // Now we've got the number of rows.
        double[][] A = new double[m][];
        v.copyInto(A); // copy the rows out of the vector
        return new Matrix(A);
    }

}
