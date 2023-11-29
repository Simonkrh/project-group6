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

TCP is used as the transport-layer protocol, using port numbers 1024 and over. TCP is chosen for 
its reliability since it ensures that no data is damaged, lost or delivered out of order. 
This is crucial for accurate transmission of sensor data and control commands between the nodes
in the network. Although the UDP protocol is faster, the speed is not necessary for the communication between the nodes in this network.

## The architecture

Sensor nodes (SN):
1. Generate sensor data such as temperature humidity etc.
2. Establish a tcp connection with the server.
3. Send sensor data to the server regularly.

Actuator nodes (AN):
1. Control different elements either turning on or off, that can be (window , door , fan).
2. Establish a tcp connection with the server.
3. Receive commands through the server and perform them.

Control panel node (CPN)
1. Display received data from sensor nodes
2. Send commands to actuator nodes
3. Establish TCP connection with the server.
4. Request sensor data or send command to AN.

Intermediate server (IS)
1. Facilitate communication between al the different nodes.
2. Manage coming request/data from the nodes.
3. Handle routing between the sensors.

TCP connection
1. AN , SN and CPN initialize TCP connection to the server
2. SN sends different data to the server 
3. AN retrieves commands from the server and performs them
4. CPN request SN data and send command to AN trough the server

## The flow of information and events

The flow of information and events starts with **sensor nodes** collecting environmental data, which 
can include temperature, humidity and light level. This data is transferred to the **control panel nodes** 
where the readings will be displayed in real time. The control panel nodes serves as the central hub and 
can send commands to the **actuator nodes** based on the readings sent by the sensor nodes. The actuator nodes 
can execute actions like opening a window, turning on a fan, turning on a heater, etc...

This process is event driven where the control panel can respond dynamically to changes in sensor readings 
or user commands by sending commands to the actuator nodes. 

## Connection and state

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

### Turning an Actuator on

When turning on an Actuator:

1. The client sends a "Turn ON Actuator" command, which will be encoded as `on:nodeId:actuatorId`
   in the socket, where nodeId and actuatorId are identifiers for the specific actuator.
2. The server sends a "Actuator ON" message to all the connected clients, 
   which is encoded as `ACTUATOR_ON:nodeId:actuatorId`.

### Turning an Actuator off

When turning off an Actuator:

1. The client sends a "Turn OFF Actuator" command, which is encoded as `off:nodeId:actuatorId`
   in the socket, where nodeId and actuatorId are identifiers for the specific actuator.
2. The server sends a "Actuator OFF" message to all the connected clients, 
   which is encoded as `ACTUATOR_OFF:nodeId:actuatorId`.

### Advertising Sensor Data

When advertising sensor data:
1. The node sends a "Sensor Data Advertisement" message, which is encoded as
   `SENSOR_DATA:nodeId;sensorType1=value1 unit1,sensorType2=value2 unit2,...` in the socket.

### Error messages

When a request cannot be processed successfully, the server responds with an error message in the 
format `eM`, where M is an error message - string continues until the newline. For instance, 
if attempting to turn on an actuator when it is already on, the error message will be 
"Actuator already on" and the response will be "e:Actuator already on". 


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
