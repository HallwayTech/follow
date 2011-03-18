**Follow**
==========

Follow is a simple Java application which allows a user to 
monitor several log files concurrently.  The name and behavior 
of this application are inspired by the "f" (follow) flag of
the UNIX command "tail".  For more information about Follow,
please visit http://hallwaytech.github.com/follow/

Copyright (C) 2000-2011
 Greg Merrill (greghmerrill@yahoo.com),
 Murali Krishnan (murali_sourceforge@hotmail.com),
 Carl Hall (carl@hallwaytech.com)

----------
*Building*
----------
The project has been designed with no dependencies. That isn't to say we hand
rolled everything but we have done our best to make sure we don't carry extra
baggage when not needed.

Building the project is done using Maven:

	mvn clean install

--------------
*Installation*
--------------
None really. Just run the file.

---------
*Running*
---------
If you build the project locally, the resulting artifact can be found in
target/ as follow-${version}.jar

To run this, just use this Java command:

	java -jar follow-${version}.jar
