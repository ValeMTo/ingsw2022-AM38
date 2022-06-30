# Eriantys Java game

<!DOCTYPE html>

<img src="https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_scatolaFrontombra-600x600.png" width=200px height=200 px align="right" />

## Team members
Team:  ingsw2022-AM38 <br />
Valeria Amato valeria.amato@mail.polimi.it <br />
Francesco Barisani francesco.barisani@mail.polimi.it <br />
Nicolò Caruso nicolo1.caruso@mail.polimi.it <br />

## Project overview
This group project was developed as the final test for the Software Engineering 2022 Course at Politecnico di Milano, held by the teacher Alessandro Margara. <br />  
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
All tests in model and controller has a classes' coverage at 100%.

*The following coverages refer to coverage on code lines.*

|[Package](index_SORT_BY_NAME_DESC.html)|[Class, %](index_SORT_BY_CLASS.html)|[Method, %](index_SORT_BY_METHOD.html)|[Line, %](index_SORT_BY_LINE.html)|
|:----|:----|:----|:----|
|[it.polimi.ingsw](ns-1/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/1)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/2)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/20)  </span>|
|[it.polimi.ingsw.client](ns-2/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/3)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/42)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/167)  </span>|
|[it.polimi.ingsw.client.CLI](ns-3/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/4)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/65)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/651)  </span>|
|[it.polimi.ingsw.client.gui](ns-4/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/1)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/18)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/79)  </span>|
|[it.polimi.ingsw.client.gui.controllers](ns-5/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/13)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/102)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/917)  </span>|
|[it.polimi.ingsw.client.view](ns-6/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/4)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/108)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/394)  </span>|
|[it.polimi.ingsw.connectionManagement](ns-7/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/2)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/5)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/21)  </span>|
|[it.polimi.ingsw.controller](ns-8/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (7/7)  </span>|<span class="percent">    92.1%  </span>  <span class="absValue">    (35/38)  </span>|<span class="percent">    81.1%  </span>  <span class="absValue">    (425/524)  </span>|
|[it.polimi.ingsw.exceptions](ns-9/index.html)|<span class="percent">    63.6%  </span>  <span class="absValue">    (7/11)  </span>|<span class="percent">    64.7%  </span>  <span class="absValue">    (11/17)  </span>|<span class="percent">    72.7%  </span>  <span class="absValue">    (16/22)  </span>|
|[it.polimi.ingsw.messages](ns-a/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (9/9)  </span>|<span class="percent">    86.7%  </span>  <span class="absValue">    (65/75)  </span>|<span class="percent">    86.5%  </span>  <span class="absValue">    (347/401)  </span>|
|[it.polimi.ingsw.model.board](ns-b/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (12/12)  </span>|<span class="percent">    93.3%  </span>  <span class="absValue">    (112/120)  </span>|<span class="percent">    93%  </span>  <span class="absValue">    (675/726)  </span>|
|[it.polimi.ingsw.model.player](ns-c/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (6/6)  </span>|<span class="percent">    100%  </span>  <span class="absValue">    (52/52)  </span>|<span class="percent">    96.5%  </span>  <span class="absValue">    (192/199)  </span>|
|[it.polimi.ingsw.model.specialCards](ns-d/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (4/4)  </span>|<span class="percent">    91.7%  </span>  <span class="absValue">    (33/36)  </span>|<span class="percent">    84.8%  </span>  <span class="absValue">    (106/125)  </span>|
|[it.polimi.ingsw.mvc](ns-e/index.html)|<span class="percent">    100%  </span>  <span class="absValue">    (2/2)  </span>|<span class="percent">    71.4%  </span>  <span class="absValue">    (5/7)  </span>|<span class="percent">    55.6%  </span>  <span class="absValue">    (10/18)  </span>|
|[it.polimi.ingsw.server](ns-f/index.html)|<span class="percent">    0%  </span>  <span class="absValue">    (0/5)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/59)  </span>|<span class="percent">    0%  </span>  <span class="absValue">    (0/554)  </span>|



### How to launch the JAR file

