# matrixserver ver 0.1
It's a master service (to combine with matrixslave) for dispatching to the slaves multiple operations of row * column (of a matrix) 

# Rational
Distribute the computational load of matrix multiplication, across online services.

Matrixserver is a simple CLI application (designed to be a master), able to send HTTP requests of calculation (row * column) to n instances of the slave in parallel.
 - The matrices are splitted in chuncks of 'rows and columns'
 - every chunk is managed (in parallel) from a single thread
 - the threads call the slave HTTP API (one or many potentially) able to manage multiple instances of request and wait for the result
 - every single elaboration is aggregated (in the matrix result) at the moment of the arrive
 - partial results are displayed until the last thread finished the elaboration (or forced to quit after a defined number of seconds)
 - the file final-matrix.edn will contain the elaboration
 
## Technical Requirements

* Java 7+ / Clojure 1.8 / Leiningen 2.x


## Usage  

* Edit the file resources/matrices.edn to define the matrices to elaborate
 
* Edit the file config/config.edn to set where the slave is

* To run the app

    $ java -jar matrixserver.jar

* To run the test

    $ lein midje
