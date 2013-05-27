Can test in Java 6 access nested enum in Java 5?
================================================

Setup
-----

All is compiled with Java 7 but we set different source and target for the cdoe (java 5) and test (java 6).

Instructions
------------

    cd code-java5
    mvn install

    cd test-java6
    mvn test

Expected result:

   Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.07 sec
