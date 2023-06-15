# Persistence: making the server crash-proof

Our  game server has a built-in persistence feature that allows it to withstand crashes and restore a
previously saved state. This functionality ensures a seamless gaming experience for the players,
minimizing the impact of unexpected server disruptions.

## How it works

The server maintains a comprehensive map of `GameController` instances which holds the state of each
game and the players connected to it. Whenever a user performs a command in any game, the server's
status is saved to a local `serverBackup.ser` file.

The method responsible for writing this file is declared as `synchronized`. This keyword ensures
that multiple `GameController` instances cannot access the file concurrently, avoiding potential
inconsistencies or corruption in the saved state.

## Restoring the server state

When restarting the server, you can use the `--backup` flag to attempt a restoration of the previous
state from the `serverBackup.ser` file. This way, the game can pick up from where it left off.

!!! warning
    Every time the server starts, the `serverBackup.ser` file is overwritten with the new state of the server.
    If you run it without the `--backup` flag, the previous saved state will be lost.

This server persistence feature contributes to the robustness and reliability of the MyShelfie gaming experience.
Enjoy the benefits of continuity and minimal disruption in your multiplayer games!
