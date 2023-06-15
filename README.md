# Software Engineering I - final project

This is the final project for the Software Engineering I course at *Politecnico di Milano*.
The project consists of a client-server application for a multiplayer game.

Authors:
- [Gabriele Puglisi]()
- [Matteo Santelmo]()
- [Giuseppe Steduto](https://giuseppesteduto.me)
- [Mattia Vicenzotto]()

Read the documentation at [https://softeng-gc07.github.io/](https://softeng-gc07.github.io/).

## Introduction

Welcome to the Softeng-gc07 client, a part of an interactive, multiplayer game. This README covers the steps to get you up and running with the client side of the game.

## Prerequisites

To run this project, you need to have a Java Runtime Environment (JRE) installed. This project requires Java version 11 or later. You can check your Java version by typing `java -version` in your command prompt or terminal.

## Getting Started

To start the client, navigate to the project root directory where the `softeng-gc07.jar` file is located. This is done via the command line with the following command:

```sh
java -jar ./softeng-gc07.jar [--cli|--gui] [--server-address=<server-address>] [--socket|--rmi]
```

## Command-line arguments

The startup command has these optional parameters:

- `--cli`: If set, this flag runs the game in textual mode. If it is not set, the game will run with a graphical interface.
- `--server-address=<server-address>`: This parameter allows you to specify the server address if it's different from the default one.
- `--socket`: If set, this flag forces the client to use the socket connection. If the `--rmi` flag is set instead, the client will use the RMI connection.

Note that it is possible also to use a hostname instead of an IP address, as long as there is a valid DNS record for it
in the DNS server used by the client.
Example usage:

```sh
java -jar ./softeng-gc07.jar --cli --socket --server-address=192.168.1.100
```
This command runs the game in textual mode and connects to the server at 192.168.1.100.

It is also possible to pass a valid domain name, like myshelfie.polimi.it, as the server address (as long as the DNS server used by the client can resolve it to an IP address).

Please ensure your server is up and running before starting the client.

## Running the server

To run the server, navigate to the `deliverables` directory where the `server.jar` file is located and run it. This is done via the command line with the following command:

```{.sh .copy-to-clipboard}
java -jar ./server.jar [--logging=<debug|info|error>] [--server-address=<server-address>]
```

The `--server-address` option should take as a parameter the IP address of the interface you want the server to listen on as seen from the client. If you don't specify anything, the server will listen on `localhost`

### Logging options

The server has extensive logging capabilities. Every log message is both printed to the console and saved in a log file. 
The log file name is `server.log` and it is appended to every time the server is run.

You can specify the logging level by setting the `--logging` parameter
when running the JAR from the command line. The possible values are:
- `info`: Logs all the important events that happen in the server. Matches the `Loglevel.INFO` enum value.
- `debug`: Logs all the events that happen in the server. Matches the `Loglevel.FINE` enum value.
- `error`: Logs only the errors that happen in the server. Matches the `Loglevel.SEVERE` enum value.

If you don't specify anything, the default logging level is `info`.

Example usage:

```{.sh .copy-to-clipboard}
java -jar ./server.jar --logging=debug
```