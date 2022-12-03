# MapBuilder
Java 8 classes -> target/classes

Java 17 classes -> target/java 17 target/classes

### Jar Location
- look in `Releases` for the latest release. You will see two different jars: MapBuilder.jar is Java 8 while SNAPSHOT is Java 17. (Jar with the smaller size is Java 8)

### JavaFX requirements
- if you are using the Java 17 version, make sure you are using the correct JavaFX version as well which is version 20-ea+7

( [visit the repo](https://mvnrepository.com/artifact/org.openjfx/javafx-controls) for more details )

### Features
- put down beepers
- place horizontals and verticals
- remove all objects with remove
- add or remove objects using free click
### File Managment
One primary goal of this projecct was to use JavaFX's calabilities to provide a new and improve file choosing system.

- choose a file to open with the classical interface
- save a file by choosing a directory
- .map extension filter to make finding files easier
- saves directory location for easy loading and saving
