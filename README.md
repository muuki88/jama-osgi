## Tycho build for the Jama matrix library

This project is a fork of [Jama](http://math.nist.gov/javanumerics/jama/) and provides
an OSGi bundle, eclipse feature and an p2 updatesite. You can

1. Deploy an eclipse plugin (OSGi bundle) with tycho
2. Deploy an eclipse feature containing Jama
3. Deploy an eclipse repository (update site) to install Jama into eclipse, etc.

Checkout the files and run 

```bash

mvn compile package` 
```

## Usage

```java

double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}}; 
Matrix A = new Matrix(array); 
Matrix b = Matrix.random(3,1); 
Matrix x = A.solve(b); 
Matrix Residual = A.times(x).minus(b); 
double rnorm = Residual.normInf();
```

## Acknowledgement

JAMA's initial design, as well as this reference implementation, was developed by 

* Joe Hicklin 
* [Cleve Moler ](http://www.nist.gov/cgi-bin/exit_nist.cgi?timeout=5&url=http://www.mathworks.com/company/cleve_bio.shtml)
* Peter Webb

... from [The MathWorks](http://www.mathworks.com/)

* [Ronald F. Boisvert](http://math.nist.gov/~RBoisvert/)
* [Bruce Miller](http://math.nist.gov/~BMiller/)
* [Roldan Pozo](http://math.nist.gov/~RPozo/)
* [Karin Remington](http://math.nist.gov/~KRemington/)

... from [NIST](http://www.nist.gov/)

**Copyright Notice**

_This software is a cooperative product of The MathWorks and the National Institute of Standards and Technology (NIST) which has been released to the public domain. Neither The MathWorks nor NIST assumes any responsibility whatsoever for its use by other parties, and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic._

## Links

* [Jama Homepage](http://math.nist.gov/javanumerics/jama/)
* [Jama-1.0.2.jar](http://math.nist.gov/javanumerics/jama/Jama-1.0.2.jar)
