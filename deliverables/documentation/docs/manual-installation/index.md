---
template: with_announcement_1_back.html
---

# Manual installation

So you want to get your hands dirty install the game manually? No problem! This page will guide you through the process.

!!! note
    Keep in mind that both the server and the client application are already pre-built and ready to run!
    You can find them in the `deliverables/jar` folder of the repository, and the instruction on how to run them
    are in the [client](../client) and [server](../server) documentation pages.

## Requirements

In order to run the game, you need to have the following software installed on your machine:

- Java SE JDK 19 or higher
- JavaFX 19 or higher (they don't come bundled with the JDK anymore)
- Maven 3.6.3 or higher

## Building the project

First of all, you need to clone the repository on your machine. You can do so by running the following command:

```bash
git clone GabP404/ing-sw-2023-Puglisi-Vicenzotto-Santelmo-Steduto
cd ing-sw-2023-Puglisi-Vicenzotto-Santelmo-Steduto
```

Then, you need to build the project. You can do so by running the following command:

```bash
mvn clean package
```

A new folder called `target` will be created in the root of the project. Inside it, you will find the jar files,
which already include all the dependencies needed to run the game.

Easier than you thought, right?

## Running the game

Head over to the [client](../client) and [server](../server) documentation pages to learn how to run the game.