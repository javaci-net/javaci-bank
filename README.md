# Javaci-Bank

Javaci Banking System

Git: <https://github.com/javaci-net/javaci-bank>

Project: <https://github.com/orgs/javaci-net/projects/2>

You can access live site from following urls :

[Local Url](http://localhost:8080/ "Local")  
[Domain Url](http://api.javacibank.com/ "Javacibank.com")  
[Heroku Url](https://javaci-bank.herokuapp.com/# "Herokuapp")  

## Database

Uses embdedded H2 db by default (jdbc:h2:mem:testdb)
Access /h2-console/ for admin console

### Development Environment

We use h2 data file mode in development environment.

[Local Url](http://localhost:8080/h2-console/ "Local") 

### Production Environment

Production uses Heroku Postgre Add-On: Execute following command:

    heroku pg:psql postgresql-cylindrical-79376 --app javaci-bank-api

## API

You can access Swagger documentation from following urls :

[Local Url](http://localhost:8080/api.html# "Local")  
[Domain Url](http://www.javacibank.com/api.html# "Javacibank.com")  
[Heroku Url](https://javaci-bank.herokuapp.com/api.html# "Herokuapp")  


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
 
 ------------------------------------------------------------
 
![Patrick from Spongebob](https://images-na.ssl-images-amazon.com/images/I/41XyE6W5ofL._AC_SX355_.jpg)


 
                                                                                                   
                                                                                                   


