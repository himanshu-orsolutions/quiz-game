#### QUIZ SERVER
It serves requests from the players. Multiple players can play the quiz simultaneously.

##### Specifications
- Its currently configured to run on port 9090 which can be changed in Server.java
- The server sends the quiz questions immediately after the connection is established with the player
- All requests from the player trigger the mechanism to calculate result from the answers sent in request

##### Implementation
- Implemented in Java. Source level: 1.8
- To start the server, run Server.java
