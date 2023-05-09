# Peer-Review 2: Network

[Gabriele Puglisi], [Matteo Santelmo], [Giuseppe Steduto], [Mattia Vicenzotto]

Gruppo GC07.

Valutazione del gruppo GC47.

## Ipotesi

Per poter scrivere la peer review, abbiamo dovuto fare delle assunzioni per completare il quadro offerto dal diagramma e dalla descrizione. Alcune di queste ipotesi potrebbero non essere corrette, ma abbiamo provato ad essere il più logici possibili e a mettere assieme i diversi pezzi della descrizione e del class diagram.

- `EventDispatcher` è responsabile della gestione e dell'instradamento degli eventi tra i listener registrati e gli oggetti che generano gli eventi. `EventDispatcher` funziona come un intermediario tra i listener e gli eventi (una chiamata al metodo `dispatch(ev)` va a chiamare a sua volta un metodo implementato dal Listener corrispondente all’evento `ev`)
- Il flusso di una modifica della view con conseguente notifica del server è il seguente:
    - Il Client chiama `sent(evento)` sul proprio `NetMessageHandler`
    - A sua volta, `NetMessageHandler` invia attraverso il socket e l’`ObjectOutputStream` (che sono attributi della classe) l’evento di modifica della view
    - Il server riceve l’evento serializzato attraverso socket, lo deserializza, e chiama `EventDispatcher.dispatch(evento)`. Ipotizziamo ci sia un’istanza di `EventDispatcher` all’interno della classe `Server`, o che il metodo `dispatch` sia statico.
    - Il metodo dispatch chiama un metodo del controller che modifica il model come richiesto dal client.
- Una volta modificato un model, gli altri client vengono notificati come segue:
    - Assumiamo che il server memorizzi dei riferimenti ai socket dei client connessi.
    - Il server invia a ogni client una versione serializzato dell’evento di modifica del model (sottoclasse di `Event`).
    - Ricevuto il messaggio, viene chiamato `onEvent(NetResponse)` sul `NetMessageHandler` del client, che a sua volta chiama il metodo `dispatch` del proprio dispatcher e viene aggiornata la view sul client.
- `NetResponse`, `NetHello`, `NetClose` sono sottoclassi di `Event` e contengono le informazioni da scambiare con diverse strutture.
- `ExecutorService` viene usato per assegnare ad ogni client un thread diverso che il server usa per restare in ascolto senza interrompere il proprio funzionamento

## Lati positivi

- La gestione dei log con la classe `Logger` della standard library di Java è una scelta intelligente. Potrebbe tornare molto utile soprattutto in fase di debug.
- L'invio di istanze di sottoclassi di eventi attraverso la rete potrebbe permettere di trasmettere in modo strutturato le richieste di modifica del model e della view riutilizzando codice e rispettando i principi della programmazione orientata agli oggetti.

## Lati negativi

- Non è chiaro come Client e Server siano collegati, in particolare nel Server non ci sono riferimenti ai Client (mentre pare di capire che il riferimento al Server all’interno del Client sia integrato all’interno dell’istanza della classe `NetMessaggeHandler`).
- Dalla descrizione sembra che il funzionamento sia di Client che di Server sia legato all’invio e ricezione di sottoclassi di una classe Event, ma né la classe né alcuna sottoclasse sono specificate, rendendone poco chiaro il contenuto ancora prima dell’utilizzo.
- L’UML è poco chiaro e incompleto - è molto complesso capire il funzionamento del processo di scambio dei messaggi senza il resto delle informazioni sulle classi e/o un sequence diagram.
  In genere, mancano diverse ulteriori informazioni necessarie: il testo fornisce solo una panoramica generale del sistema e delle sue funzionalità, senza entrare in dettagli specifici. Questo rende difficile valutare in modo esaustivo i lati positivi e negativi del sistema e le possibili aree di miglioramento.
- Il pattern MVC non è ben rispettato: ad esempio, esiste un riferimento diretto a `GameModel` come attributo nella classe `Server`. Il pattern MVC vuole che il model, sul lato server, possa essere acceduto direttamente solo attraverso il controller, di modo che la logica di modifica del modello sia concentrata appunto solo nel controller.

## Confronto tra le architetture

- La nostra implementazione consente anche l’uso di RMI, mentre a giudicare dall’UML, qui non è presente.
- La nostra architettura incapsula i diversi tipi di messaggi in una classe wrapper, che contiene il messaggio, il tipo del messaggio, l’identificativo del client e l’identificativo del game per la gestione delle partite multiple. Questa viene quindi serializzata, inviata al server, e seguendo un command design pattern, esegue le modifiche sul controller.
  L’architettura revisionata, invece, sembra inviare direttamente la sottoclasse di `Event` corrispondente al messaggio in questione, probabilmente richiamando istruzioni diverse a seconda del valore restituito da `instanceof` sull’evento trasmesso.