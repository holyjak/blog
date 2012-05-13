Example of Automatic Generic Data Mappers
=========================================

See the description at the blog post [Bad Code: Too Many Object Conversions Between Application Layers And How to Avoid Them](http://theholyjava.wordpress.com/2012/05/12/bad-code-too-many-object-conversions-between-application-layers-and-how-to-avoid-them/).

We are mapping:

* XML to Java Bean via JAXB
* Java Bean to MongoDB's DBObject (BSONObject) via Jackson Mongo Mapper
* MongoDB's DBObject to Java Map via its own method
* Java Map to JSON via Jersey's Pojo Mapping Feature, based on Jackson

Action!
-------

To try this out, run
```
mvn test
```

Note about performance
----------------------

I did some testing for my current project and the results were that using JAXB for automatic XML to Java
mapping was considerably faster than our hand-coded parsing based on XPath and that the
mapping of Java Bean to/from Mongo DB was about 10-20 ms slower than when hand-coded (i.e. totally
negligible compared f.ex. to the time necessary to retrieve the XML).

Notice that the times printed by the test have little value as they include the setup overhead that
normally get distributed among all (presumably many) objects handled by the system.

From performance tests of the real project where we compared times for the manual and automatic processing
for selected use cases, we got the following results:

* XML parsing of 100 entries: 1.8s manually, 0.9s automatically
* Reading from Mongo and converting to a POJO: the automatic solution was slower by 20ms (30%) for 1k records and 50ms (10%) for 10k
* Inserting 1k POJOs into Mongo: 0.6s with the automatic conversion, 7s (quite surprisingly) with the manual one

(No guarantee of any precision, objectivity or correctness of the tests.)
