# Client documentation

This documentation page aims to provide you with detailed instructions on how to set up and run the client-side software
of the game.

!!! note
    Before we begin, ensure that you have the server component of the game up and running.
    Head over to the [server documentation](../server/index.md) for instructions on how to do that.

## Prerequisites

The Softeng-gc07 client is built on Java, therefore it requires a Java Runtime Environment (JRE) to function. Please make sure that you have JRE installed on your system and it is of version 11 or later.

To check your Java version, use the following command in your terminal or command prompt:

```sh
java -version
```

## Getting Started

Kickstarting the client is a straightforward process. First, navigate to the `deliverables` directory. The file `client.jar` is what you're looking for, this is the compiled JAR file for the client.

The format of the command to run the client is the following:

```sh
java -jar ./softeng-gc07.jar [--cli|--gui] [--server-address=<server-address>] [--socket|--rmi]
```

### Command-line arguments

To customize your gaming experience, there are several optional command-line parameters you can utilize:

- `--cli` or `--gui`: These flags allow you to choose between the textual and graphical interfaces of the game. If you don't specify anything, the game is launched with the graphical interface by default.
- `--server-address=<server-address>`: This parameter allows you to specify the server address if it's different from the default one (localhost).
- `--socket` or `--rmi`: These flags allow you to choose between the socket and RMI connection types. If you don't specify anything, the game is launched with the socket connection by default.

!!! tip
    The `--server-address` parameter accepts both IP addresses and hostnames. This provides flexibility in case you have a valid DNS record for your server in the DNS server used by your client.

### Example usage

Here's an example of using these parameters:

```sh
java -jar ./softeng-gc07.jar --cli --socket --server-address=192.168.1.51
```
