rem # create project:
rem # mvn archetype:generate -DgroupId=org.bcjj.gitCompare -DartifactId=compareToGit -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

set JAVA_HOME=C:\java\jdk1.8.0_144

mvn clean install dependency:copy-dependencies dependency:sources

rem # mvn dependency:copy-dependencies # copia las dependencias a target/dependencies/

rem # cd target/
rem # java -cp compareToGit-0.1.jar org.bcjj.gitCompare.gui.GitCompareMainWindow

java -jar target/compareToGit-0.1-boot.jar





