 # Communication protocol

This document describes the protocol used for communication between the different nodes of the distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer, door opener/closer, heater.
* Sensor/actuator node - a computer which has direct access to a set of sensors, a set of actuators.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with it.

## The underlying transport protocol

TCP is used as the transport-layer protocol, using port numbers 1024 and over. TCP is chosen for its reliability since it ensures that no data is damaged, lost or delivered out of order. This is crucial for accurate transmission of sensor data and control commands between the nodes in the network. Although the UDP protocol is faster, the speed is not necessary for the communication between the nodes in this network.

## The architecture

Sensor/Actuator nodes:

- Generate sensor data such as temperature humidity etc.
- Control different elements by either turning them on or off (for example, a window, a door, a fan).
- Managed by the server.

Greenhouse simulator:

- Acts as the main central server.
- Handles all the sensor/actuator nodes.
- Receives commands and sends responses to the control panel nodes.

Control panel nodes:

- Acts as the clients.
- Display received data from sensor nodes.
- Send commands the server.
   - Fetching information about the sensor/actuator nodes.
   - Changing actuator states.
- Establish TCP connection with the server.

TCP connection:

- Control panel nodes initializes TCP connection to the server.
- Server sends sensor data to the clients.
- Server receives commands from clients, performs them, and sends corresponding responses.
- Control panel nodes requests sensor/actuator node data from the server.

## The flow of information and events

The flow of information and events starts with **sensor nodes** collecting environmental data, which can include temperature, humidity and light level. This data is transferred to the **control panel nodes** where the readings will be displayed periodically. The control panel nodes can send commands to the **server**, for fetching sensor data, or changing actuator states. The actuators can be used for executing actions like opening a window, turning on a fan, turning on a heater, etc...

This process is event driven where the control panel can respond dynamically to changes in sensor readings or other commands by sending commands to the server. 

## Connection and state

We use a TCP communication protocol which is connection-oriented. This means that a logical connection is required throughout the entire time that communication is taking place. The communication protocol we use is also stateful. This ensures reliability when delivering packets from sender to receiver.

## Types, constants

Messages contain data relevant their types. For actuator states, it is enough to provide the id of the node and actuator that has changed, as well as the boolean value that the actuator has changed to. When sending information about the sensors, it becomes necessary to use objects that represent all the data that sensors contain. The messages sent during communication, gets converted to and from strings.

## Message format

Every message starts with a string that describes what type of message that is being sent, followed by the relevant data that is used for the message type.

### Turning an Actuator on

When turning on an Actuator:

1. The client sends a "Turn ON Actuator" command, which will be encoded as `on:nodeId:actuatorId` in the socket, where nodeId and actuatorId are identifiers for the specific actuator.
2. The server sends an "Actuator state" message to all the connected clients, which is encoded as `ACTUATOR_ON:nodeId:actuatorId`.

### Turning an Actuator off

When turning off an Actuator:

1. The client sends a "Turn OFF Actuator" command, which is encoded as `off:nodeId:actuatorId` in the socket, where nodeId and actuatorId are identifiers for the specific actuator.
2. The server sends a "Actuator state" message to all the connected clients, which is encoded as `ACTUATOR_OFF:nodeId:actuatorId`.

### Advertising Sensor Data

When advertising sensor data:

1. The node sends a "Sensor Data Advertisement" message, which is encoded as `SENSOR_DATA:nodeId;sensorType1=value1 unit1,sensorType2=value2 unit2,...` in the socket.

### Requesting Node Information

When requesting current information about all sensor/actuator nodes:

1. The client sends a "Request node info" command, which is encoded `REQUEST_NODE_INFO` in the socket.
2. The server compiles information about each node, including sensor readings and actuator state.
3. The server responds with a "Node Information" message, which is encoded as `NODE_INFO|nodeId1:sensorData:actuatorData|nodeId2:sensorData:actuatorData|...` in the socket. "sensorData" is encoded as `sensorType1=value1 unit1,sensorType2=value2 unit2,...`, and "actuatorData" is encoded as `actuatorId1_actuatorType1=state1,actuatorId2_actuatorType2=state2,...`.

### Error messages

When a request cannot be processed successfully, the server responds with an error message in the format `eM`, where M is an error message - string continues until the newline.

## An example scenario

A typical scenario goes as follows:

1. The greenhouse simulator starts by setting up all the sensor/actuator nodes, and the TCP server.
2. The server awaits new clients that will be handled, as well as periodically sending sensor data from the sensor/actuator nodes.
3. A control panel starts and connects to the server.
4. The control panel requests information about all the sensor/actuator nodes stored in the server.
5. The server receives the request and sends a response containing all the data.
6. The data is received by the control panel, and the nodes and data are displayed in the gui.
7. The server periodically sends sensor data.
8. The sensor data is received by the control panel and the sensor information is updated.
9. An actuator is toggled in the control panel, which makes the control panel send an actuator state change to the server.
10. The server receives the command and executes it, resulting in the corresponding actuator to change its state.
11. The server broadcasts a response to all the clients, alerting them that the specified actuator has changed its state.

## Reliability and security

If the server closes, all the control panels that are connected, will also automatically close. Error message responses are handled when they are received.
