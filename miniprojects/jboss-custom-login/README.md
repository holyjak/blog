Examples of custom login modules for JBoss AS
==============================================

Tried with JBoss AS 7 but the code I use hasn't changed in years so
it should work equally well with JBoss 5 - 7 - future.

Configuration
-------------

Make sure that your `<JBoss AS 7.1.0.Final>/standalone/configuration/standalone.xml`
contains this fragment:

           <security-domains>
                <security-domain name="form-auth" cache-type="default">
                    <authentication>
                        <login-module code="custom.MySimpleUsernamePasswordLoginModule" flag="required">
                            <!--module-option name="exampleProperty" value="exampleValue"/-->
                        </login-module>
                    </authentication>
                </security-domain>

Notice that the domain to use for this webapp is specified in its jboss-web.xml.

Login Module Deployment
------------------------

At JBoss AS 7 you can deploy the login module as a part of this webapp, just by having its .class in
WEB-INF/classes/ (and make sure you have it configured in standalone.xml).

For details read [JBossAS7SecurityDomainModel#Using_custom_login_module](https://community.jboss.org/wiki/JBossAS7SecurityDomainModel#Using_custom_login_module)
(notice that "write the FQCN in the code attribute" means you should write the fully qualified name of your
login module implementation into the code attribute of a login-module element in standalone.xml).

Webapp Deployment
-----------------

Deploy as usual, f.ex. run `package` and copy target/jboss-custom-login-*.war
to `<JBoss AS 7.1.0.Final>/standalone/deployments/`

Webapp should be at [http://localhost:8080/jboss-custom-login](http://localhost:8080/jboss-custom-login).

Resources
---------

This part of JBoss AS hasn't changed in years. There is no good current documentation
(i.e. for v7) but the documentation for AS 5 is pretty good and is still valid. The
two main articles are:

* JBoss EAP 5 [Security Guide Ch. 12.2. Custom Modules](http://docs.redhat.com/docs/en-US/JBoss_Enterprise_Application_Platform/5/html/Security_Guide/sect-Custom_Modules.html)
* JBoss EAP 5 [Security Guide Ch. 12.2.2. Custom LoginModule Example](http://docs.redhat.com/docs/en-US/JBoss_Enterprise_Application_Platform/5/html/Security_Guide/sect-Custom_LoginModule_Example.html)

If you f.ex. can reuse JBoss' DatabaseServerLoginModule but have your passwords encrypted in a way
not supported out of the box than you can subclass it and override `convertRawPassword` to encrypt
the user-provided password accordingly, as described in [CreatingACustomLoginModule](https://community.jboss.org/wiki/CreatingACustomLoginModule).

JBoss AS 7 specific (but rather too brief):

* [https://community.jboss.org/wiki/JBossAS7SecurityCustomLoginModules](https://community.jboss.org/wiki/JBossAS7SecurityCustomLoginModules)
* [https://community.jboss.org/wiki/JBossAS7SecurityDomainModel](https://community.jboss.org/wiki/JBossAS7SecurityDomainModel)

Other:

* [Source code of JBoss' DatabaseServerLoginModule](http://www.docjar.com/html/api/org/jboss/security/auth/spi/DatabaseServerLoginModule.java.html)

 Credits
 -------

 The webapp with the exception of the login module class comes from the article
 https://community.jboss.org/wiki/JBossAS7SecurityCustomLoginModules.