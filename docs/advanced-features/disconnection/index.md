# Client disconnection

One of the advanced features this game implements is the handling of client disconnections.
It is done in a way that is transparent to the client, and that allows the client
to rejoin a game even after it disconnected, without pausing the game for the other players.

## How it works

When a client connects to the server (either via RMI or via Socket), it is registered with a
unique nickname. After the client receives confirmation of the registration, it starts
a thread that periodically (every `HEARTBEAT_PERIOD` seconds) sends a "heartbeat" message to the server.
The default value for `HEARTBEAT_PERIOD` is 5 seconds.

The server, on the other hand, after registering a client, starts *for each client* a thread
that checks if the last heartbeat of the client was received within a certain time interval
(we set it as double the heartbeat interval).

### Disconnection
If the last heartbeat is too old (more than `HEARTBEAT_TIMEOUT`), the server
considers the client disconnected, and performs different actions depending on the status of the
game in which the client was playing:

- If the client had not yet joined a game (i.e. before joining a lobby or in the "waiting stage"
  in the lobby), the server simply removes the client from the list of registered clients.
- If the client was in a game, the server will set the player status to "disconnected", and will notify the other
  clients in the game that the client disconnected with the first message it sends to the game. The game
  does not stop and the player is not removed (so that the player can reconnect later). Simply,
  its turn is skipped, and the other players can continue playing.

### Reconnection
- If the client loses the connection and reconnects before the `HEARTBEAT_TIMEOUT` expires, the server will just
  pretend that nothing happened.

- If the client was playing in a game and got disconnected, to rejoin the game it only has to
  reconnect to the server using the same nickname with which it registered the first time. The server
  will automatically set the player status back to "online", and will notify the other clients in the game
  that the client reconnected with the first message it sends to the game. The game will continue.
