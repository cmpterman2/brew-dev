docker run -it --rm --name my-maven-project -v c:\dev\brew:/usr/src/mymaven -v maven-repo:/root/.m2 -w /usr/src/mymaven maven:3.3-jdk-8 mvn clean install