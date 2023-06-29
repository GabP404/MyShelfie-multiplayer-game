---
template: with_announcement_1_back.html
---

# Server documentation

This documentation page aims to provide you with detailed instructions on how to set up and run the server-side software of the game.

## Running the Server

Initiating the server requires a few simple steps. First, navigate to the `deliverables/jar` directory. In this directory, you'll find the `server.jar` file which contains the server program.

The format of the command to run the server is the following:

```sh
java -jar ./server.jar [--logging=<debug|info|error>] [--server-address=<server-address>]
```

### Command-line arguments

For custom server configurations, we provide several optional command-line parameters:

- `--server-address=<server-address>`: Specifies the IP address of the interface on which the RMI registry will be bound, as seen by the client. If not specified, the server will bind to the loopback interface (127.0.0.1).
- `--logging=<debug|info|error>`: Sets the server's logging level (see below for more information).
- `--backup`: Tries to restore the server state from a backup file. If the file is not found, the server will start with a clean state.

!!! tip
    If you are running the server after a crash, maybe due to a power outage or connectivity problem, use the `--backup`
    flag to restore the previous state. If you want to learn more about how the server state is backed up, head over to the [persistence](../advanced-features/persistence/index.md) page.

Here's an example of using these parameters:

```console
java -jar ./server.jar --server-address=192.168.1.51 --logging=debug
```

## Logging

Our server features robust logging capabilities, designed to provide comprehensive insights into server operations.
Every log message is simultaneously displayed on the console and recorded in a log file named `server.log`.
The log file is appended with new entries each time the server is run.

The logging level can be defined by setting the `--logging` parameter at startup. Available values are:

- `info`: Records all significant server events. This level corresponds to the `Loglevel.INFO` enum value.
- `debug`: Captures all server events. This level corresponds to the `Loglevel.FINE` enum value.
- `error`: Logs only server errors. This level corresponds to the `Loglevel.SEVERE` enum value.

- By default, if not specified, the logging level is set to `info`.

Enjoy your journey as the game server operator and happy gaming!
