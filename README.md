# Javaci-Bank

Javaci Banking System

Git: <https://github.com/javaci-net/javaci-bank>
Project: <https://github.com/orgs/javaci-net/projects/2>

## Database

Uses embdedded H2 db by default (jdbc:h2:mem:testdb)
Access /h2-console/ for admin console

[Local Url](http://localhost:8080/h2-console/ "Local")  
[Domain Url](http://www.javacibank.com/h2-console/ "Javacibank.com")  
[Heroku Url](https://javaci-bank.herokuapp.com/h2-console/ "Herokuapp")  

## API

You can access Swagger documentation from folowing urls :

[Local Url](http://localhost:8080/swagger-ui.html# "Local")  
[Domain Url](http://www.javacibank.com/swagger-ui.html# "Javacibank.com")  
[Heroku Url](https://javaci-bank.herokuapp.com/swagger-ui.html# "Herokuapp")  


------------------------------------------------------------

## Project Configuration

Project Install, Run, Test information

------------------------------------------------------------

#### Running The Application

You can use following command line to run the program. 

    java -jar javaci-bank-api/target/javaci-bank-api-0.0.1-SNAPSHOT.jar

------------------------------------------------------------
   
### Building The Application

Run Maven Goal Install

Or Run Maven Wrapper given inside the project. For more information about Maven Wrapper check: https://github.com/takari/maven-wrapper

    ./mvnw clean install

------------------------------------------------------------

### Testing The Application

#### Unit Tests

Code contains unit test that can be executed using maven test goal.

    ./mvnw test

#### Integration Tests

    ./mvnw install


------------------------------------------------------------

### Heroku Configuration

*   [Deploy](https://devcenter.heroku.com/articles/deploying-java-applications-with-the-heroku-maven-plugin "Heroku Deploy")  
*   [Subfolder Config Option](https://devcenter.heroku.com/articles/deploying-java-applications-with-the-heroku-maven-plugin "Heroku Subfolder Config Option")  : Not a good option, use Proc file instead


------------------------------------------------------------

javaci.net&trade; 2020

        _                       _        _                 _    
       (_)                     (_)      | |               | |   
        _  __ ___   ____ _  ___ _ ______| |__   __ _ _ __ | | __
       | |/ _` \ \ / / _` |/ __| |______| '_ \ / _` | '_ \| |/ /
       | | (_| |\ V / (_| | (__| |      | |_) | (_| | | | |   < 
       | |\__,_| \_/ \__,_|\___|_|      |_.__/ \__,_|_| |_|_|\_\
      _/ |                                                      
     |__/                                                       
 
 
                                                                                                   
                                                                                                   


