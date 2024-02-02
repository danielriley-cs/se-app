se-app
=====

A sample web application in Java using the Spring Boot Framework.

Requirement 
============

* JDK 1.8 or later
* Gradle 4

Run
===

`./gradlew bootRun` or the "Run" task in IntelliJ IDEA

Layout
=====
Create new post (if logged in)
* `https://localhost:8443`

View post history
* `https://localhost:8443/history`

Health check (for running in e.g. Docker) 
* `https://localhost:8443/actuator/health`

Credits
=====
Original Code - Ilia Zlatkin

Improvements - Darin Debrestian
