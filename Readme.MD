This is a transaction service, that stores all the events in the DB as they occur. 
This uses event sourcing technique to store all the events and compute the final states 
by summing the events occured so far.
## Running Application

In order to run the application, you have create a jar file using the following command
> ./gradlew clean build

To run the executable jar file you have to use the following command:

> java -jar build/libs/TransactionService-0.0.1-SNAPSHOT.jar

## Running tests

To run the application tests, you can run using the following command:

> ./gradlew clean build.

## Prerequistes:
> Install Java\
> Install gradle