Can test in Java 6 access nested enum in Java 5?
================================================

Setup
-----

All is compiled with Java 7 but we set different source and target for the cdoe (java 5) and test (java 6).

*UPDATE*: Ok even if code-java5 compiled by JDK 5.

Instructions
------------

    # On a machine with JDK 5:
    cd code-java5
    mvn install
    # In the host machine, with JDK 6+:
    mvn install:install-file -Dfile=target/code-java5-1.0-SNAPSHOT.jar -DpomFile=pom.xml

    # On the host machine, running JDK 6+
    cd test-java6
    mvn test

Expected result:

   Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.07 sec
