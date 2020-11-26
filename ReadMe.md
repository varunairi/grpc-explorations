Steps: 
1) Use the POM.XML and run mvn -DskipTests package.
this will create the stub and server and pput it into a jar file in target folder after reading from the  .proto file under proto folder
2) Then see GrpcServerStarter.java that calls Finderservice (extending stub service) to implement server logic
3) Then Finder client 