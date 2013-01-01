# Get Jama

Each release is published as OSGi bundle on maven central.

```xml

<dependency>
    <groupId>de.mukis</groupId>
    <artifactId>de.mukis.jama</artifactId>
    <version>2.0.0.M1</version>
</dependency>
```

Or just [download it](http://search.maven.org/#browse%7C2089348230) and put it on the classpath.

# Create a Matrix

```java

double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}}; 
Matrix A = new Matrix(array); 
Matrix b = Matrix.random(3,1); 
Matrix x = A.solve(b); 
Matrix Residual = A.times(x).minus(b); 
double rnorm = Residual.normInf();
```
