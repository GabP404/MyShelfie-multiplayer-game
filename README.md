# Software Engineering I - final project

This is the final project for the Software Engineering I course at *Politecnico di Milano*, taught by Professor *Gianpaolo Cugola* during the academic year 2022/2023.
The project consists of a client-server application for a multiplayer game.

Authors:
- [Gabriele Puglisi](https://github.com/GabP404)
- [Matteo Santelmo](https://github.com/matsant01)
- [Giuseppe Steduto](https://github.com/giuseppe-steduto)
- [Mattia Vicenzotto](https://github.com/Vice41)

> Note that this is just a README file with the basic instructions on how to run the game.
For the full documentation, please refer to the project documentation, that you can find under 
[`deliverables/documentation/site`](/deliverables/documentation/site/index.html).

## What was implemented

| Feature                  | Implemented?       |
|--------------------------|--------------------|
| CLI                      | ✔️ |
| Base rules               | ✔️ |
| Complete rules           | ✔️ |
| CLI                      | ✔️ |
| GUI                      | ✔️ |
| RMI                      | ✔️ |
| Socket                   | ✔️ |
| Disconnection  (1FA)     | ✔️ |
| Server Persistence (2FA) | ✔️ |
| Multiple games (3FA)     | ✔️ |
| Chat (4FA)               | ❌                 |

## Instructions

To run this project, you need to have a Java Runtime Environment (JRE) installed. This project requires Java version 19
or later. You can check your Java version by typing `java -version` in your command prompt or terminal.

### Running the client

To run the client, navigate to the `deliverables` directory where the `client.jar` file is located and run it.
This is done via the command line with the following command:

```sh
java -jar ./client.jar [--cli|--gui] [--server-address=<server-address>] [--socket|--rmi]
```

The startup command has these optional parameters:

- `[--cli|--gui]`: This parameter allows you to specify the client mode. If you don't specify anything or specify `--gui`, the client will run in GUI mode. If you specify `--cli`, the client will run in textual mode.
- `--server-address=<server-address>`: This parameter allows you to specify the server address if it's different from the default one (`127.0.0.1`).
- `[--socket|--rmi]`: This parameter allows you to specify the communication method. If you don't specify anything or specify `--socket`, the client will use the socket communication method. If you specify `--rmi`, the client will use the RMI communication method.

Note that it is possible also to use a hostname instead of an IP address, as long as there is a valid DNS record for it
in the DNS server used by the client.
Example usage:

```sh
java -jar ./softeng-gc07.jar --cli --socket --server-address=192.168.1.100
```

Also note that you can start the client in GUI mode even by just double-clicking on the `client.jar` file!
This will start the client in GUI mode with all the default settings.

### Running the server

To run the server, navigate to the `deliverables` directory where the `server.jar` file is located and run it.
This is done via the command line with the following command:

```{.sh .copy-to-clipboard}
java -jar ./server.jar [--logging=<debug|info|error>] [--server-address=<server-address>]
```

The `--server-address` option should take as a parameter the IP address of the interface you want the server to listen on as seen from the client. If you don't specify anything, the server will listen on `localhost`

The startup command has these optional parameters:

- `--server-address=<server-address>`: This parameter allows you to specify the IP address of the interface used by the server if it's different from the default one (`127.0.0.1`).
- `--logging=<debug|info|error>`: This parameter allows you to specify the logging level. The logs with the desired detail will be shown to the console and saved on a log file (`server.log`). The default value is `info`.

Example usage:

```{.sh .copy-to-clipboard}
java -jar ./server.jar --server-address=192.168.1.100 --logging=debug
```

> Note that the server uses the ports `1099` for RMI and `1234` for socket communication. 
> Make sure that these ports are not used by other applications on your machine.
