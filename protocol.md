 # Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Sensor and actuator node - a computer which has direct access to a set of sensors, a set of
  actuators and is connected to the Internet.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and
  actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.

## The underlying transport protocol

TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you 
choose this transport layer protocol?

TCP is used as the transport-layer protocol, using port numbers 1024 and over. TCP is chosen for 
its reliability since it ensures that no data is damaged, lost or delivered out of order. 
This is crucial for accurate transmission of sensor data and control commands between the nodes
in the network. Although the UDP protocol is faster, the speed is not necessary for the communication between the nodes in this network.

## The architecture

TODO - show the general architecture of your network. Which part is a server? Who are clients? 
Do you have one or several servers? Perhaps include a picture here. 


## The flow of information and events

TODO - describe what each network node does and when. Some periodic events? Some reaction on 
incoming packets? Perhaps split into several subsections, where each subsection describes one 
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

The flow of information and events starts with **sensor nodes** collecting environmental data, which 
can include temperature, humidity and light level. This data is transferred to the **control panel nodes** 
where the readings will be displayed in real time. The control panel nodes serves as the central hub and 
can send commands to the **actuator nodes** based on the readings sent by the sensor nodes. The actuator nodes 
can execute actions like opening a window, turning on a fan, turning on a heater, etc...

This process is event driven where the control panel can respond dynamically to changes in sensor readings 
or user commands by sending commands to the actuator nodes. 

## Connection and state

TODO - is your communication protocol connection-oriented or connection-less? Is it stateful or 
stateless? 

We use a TCP communication protocol which is connection-oriented. This means that a logical connection
is required throughout the entire time that communication is taking place.
The communication protocol we use is also stateful. This ensures reliability when delivering packets
from sender to receiver.

## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe 
them here.

## Message format

TODO - describe the general format of all messages. Then describe specific format for each 
message type in your protocol.

### Error messages

TODO - describe the possible error messages that nodes can send in your system.

## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When 
are connections established? Which packets are sent? How do nodes react on the packets? An 
example scenario could be as follows:
1. A sensor node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can
   also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans
   and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
6. After 5 seconds all three sensor/actuator nodes broadcast their sensor data.
7. The user of the first-control panel presses on the button "ON" for the first fan of
   sensor/actuator node with ID=2.
8. The user of the second control-panel node presses on the button "turn off all actuators".

## Reliability and security

TODO - describe the reliability and security mechanisms your solution supports.
