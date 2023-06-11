# Softeng-gc07 Client

## Introduction

Welcome to the Softeng-gc07 client, a part of an interactive, multiplayer game. This README covers the steps to get you up and running with the client side of the game.

## Prerequisites

To run this project, you need to have a Java Runtime Environment (JRE) installed. This project requires Java version 11 or later. You can check your Java version by typing `java -version` in your command prompt or terminal.

## Getting Started

To start the client, navigate to the project root directory where the `softeng-gc07.jar` file is located. This is done via the command line with the following command:

```sh
java -jar ./softeng-gc07.jar [--cli] [--server-address=<server-address>]
```

## Command-line arguments

The startup command has these optional parameters:

- `--cli`: If set, this flag runs the game in textual mode. If it is not set, the game will run with a graphical interface.
- `--server-address=<server-address>`: This parameter allows you to specify the server address if it's different from the default one.
Note that it is possible also to use a hostname instead of an IP address, as long as there is a valid DNS record for it
in the DNS server used by the client.
Example usage:

```sh
java -jar ./softeng-gc07.jar --cli --server-address=192.168.1.100
```
This command runs the game in textual mode and connects to the server at 192.168.1.100.

Please ensure your server is up and running before starting the client.