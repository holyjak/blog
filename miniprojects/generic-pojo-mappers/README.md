Example of Automatic Generic Data Mappers
=========================================

See the description at the blog post [XXXXX]().

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
