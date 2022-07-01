# Eriantys Java game

<!DOCTYPE html>

<img src="https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_scatolaFrontombra-600x600.png" width=200px height=200 px align="right" />

## Team members
Team: &nbsp;&nbsp; ingsw2022-AM38 <br />
Valeria Amato &nbsp;&nbsp; valeria.amato@mail.polimi.it <br />
Francesco Barisani &nbsp;&nbsp; francesco.barisani@mail.polimi.it <br />
Nicolò Caruso &nbsp;&nbsp; nicolo1.caruso@mail.polimi.it <br />

## Project overview
This group project was developed as the final test for the Software Engineering 2022 Course at Politecnico di Milano, held by teacher Alessandro Margara. <br />  
The Eriantys game is the Java-based version of the board game of the same name.
Additional information on the original board game can be found at https://craniointernational.com/products/eriantys/ <br />

The final version of the game contains the following:
* Initial UML class diagram
* Final UML class diagram
* Communication protocol specification
* game implementation (jar file)
* source code of the game implementation
* source code of tests

## Implemented Functionalities

| Functionality                | Status |
|:-----------------------------|:------:|
| Basic rules                  |   🟢   |
| Complete rules               |   🟢   |
| Socket                       |   🟢   |
| GUI                          |   🟢   |
| CLI                          |   🟢   |
| All Characters cards         |   🟢   |
| Four-player game             |   🔴   |
| Multiple games               |   🟢   |
| Persistence                  |   🔴   |
| resilience to disconnections |  🔴    |

#### Legend

🔴 Not Implemented &nbsp;&nbsp;&nbsp;&nbsp;🟡 Implementing&nbsp;&nbsp;&nbsp;&nbsp;🟢 Implemented

## Tests coverage
All tests in model and controller show a classes' coverage of 100%.

*The table below shows the detailed coverage rates on Model and Controller*

|[Package](index_SORT_BY_NAME_DESC.html)|[Class, %](index_SORT_BY_CLASS.html)|[Method, %](index_SORT_BY_METHOD.html)|[Line, %](index_SORT_BY_LINE.html)|
|:----|:----|:----|:----|
|[it.polimi.ingsw.controller](ns-8/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (7/7)  </span>|<span class="percent">    92.1%  </span>  <span class="absValue">    (35/38)  </span>|<span class="percent">    81.1%  </span>  <span class="absValue">    (425/524)  </span>|
|[it.polimi.ingsw.model.board](ns-b/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (12/12)  </span>|<span class="percent">    93.3%  </span>  <span class="absValue">    (112/120)  </span>|<span class="percent">    93%  </span>  <span class="absValue">    (675/726)  </span>|
|[it.polimi.ingsw.model.player](ns-c/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (6/6)  </span>|<span class="percent">    100%  </span>  <span class="absValue">    (52/52)  </span>|<span class="percent">    96.5%  </span>  <span class="absValue">    (192/199)  </span>|
|[it.polimi.ingsw.model.specialCards](ns-d/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (4/4)  </span>|<span class="percent">    91.7%  </span>  <span class="absValue">    (33/36)  </span>|<span class="percent">    84.8%  </span>  <span class="absValue">    (106/125)  </span>|




## How to launch the JAR file and setup a game
In order to launch the game you have to run the jar file. <br/>
After opening a command terminal, navigate to the directory in which the jar file is located and type the following command:<br/>
` java -jar AM38-1.0-SNAPSHOT-jar-with-dependencies.jar `

Once you have launched the "Eriantys" game by command line, you will be asked to choose between 3 options:<br/>
* run a Server
* run a client CLI
* run a client GUI <br/>

Each option is identified by a corresponding integer value which you will have to type in order to choose what to run. <br/>
The minimum configuration for playing a functioning game requires one running server and at least two clients, which can be any combination of CLI and GUI (e.g. one CLI and one GUI, two CLIs, etc.). <br/>
3-player games are also fully supported. <br/>
It is also possible to run multiple complete games on the same running Server. <br/>

To start a game, each client has to connect to the server by reaching it on its IP address and port. Then, the client application will ask the player to choose a unique nickname for the game and - if no other game is being created at that moment - the player will have to choose the game settings (number of players and expert / easy game mode).<br/><br/>

---
*Grateful for your time and attention, we wish you many exciting games with Eriantys.<br/> Have fun!* <br/><br/>
*- The developers team*



