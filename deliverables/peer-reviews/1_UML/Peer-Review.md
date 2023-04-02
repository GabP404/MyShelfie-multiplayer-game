# Peer-Review 1: UML

[Gabriele Puglisi], [Matteo Santelmo], [Giuseppe Steduto], [Mattia Vicenzotto]

Gruppo GC07

Valutazione del diagramma UML delle classi del gruppo GC47.

## Lati positivi

- L’enumerazione `State` è interessante e utile (probabilmente anche a livello di distribuzione dell’MVC)
- La classe `GoalBoardCell` come estensione di `BoardCell` è un modo intelligente per racchiudere l’informazione sulla posizione delle celle nella carta dell’obiettivo personale in modo chiaro e leggibile

## Lati negativi

- In generale, l’UML è impreciso: mancano i parametri a molti metodi, i tipi di molti attributi e il tipo dei valori restituiti di diversi metodi, mancano molti setter e getter, e allo stesso modo non c’è uniformità nei nomi (alcuni con camelCase altri no)
    - mancano i parametri a molti metodi
    - mancano i tipi di molti attributi e il tipo dei valori restituiti di diversi metodi
    - mancano **molti** setter e getter (es.: `PersonalBoard`, `BoardCell`, `GoalBoardCell` non sono osservabili, e così tanti altri)
    - Non c’è uniformità nei nomi (alcuni con camelCase, altri no), sono presenti dei typo e talvolta nomi fuorvianti (esempio: in `LivingRoomBoard` il metodo `checkEmptyBoard()` ha un nome poco rappresentativo, dato che da regolamento la board potrebbe non essere completamente vuota per necessitare un refill)
    - Le associazioni tra alcune classi sono sbagliate (`PersonalBoard` e `BoardCell` dovrebbero essere collegate da una relazione di composizione, la freccia di generalizzazione tra `GoalBoardCell` e `BoardCell` è incorretta, la freccia tra `Player` e `PersonalBoard` dovrebbe essere in direzione opposta, …)
- `firstPlayer` non serve: basta assegnare all’inizio del gioco il `currentPlayer`
- Gli obiettivi comuni possono essere 1 nel caso di partita con regole semplificate o 2 nel caso di una partita con regole standard; perché l’attributo `commonGoal`di `Game` è un array di dimensione 3?
- a livello di progettazione e astrazione del gioco ha più senso forse assegnare direttamente al giocatore, nella classe `Player`, i `Token` che ha vinto, piuttosto che mantenerli in una struttura all’interno di `GameModel`. Questo comporterebbe modificare anche la posizione del metodo `checkCommonGoal()`
- `addTileToPlayer()` dovrebbe essere un metodo direttamente della `PersonalBoard`, di modo che non sia necessario ogni volta chiamarlo dandogli come attributi la `Tile` e il `Player`
- non avendo ulteriori informazioni su cosa faccia il metodo, `endTurn()` sembra un antipattern del MVC - non dovrebbe essere gestito nel model (il controller ha già infatti a disposizione metodi `getPlayer()`, `setCurrentPlayer()` e `getNextPlayer()` per gestire l’andamento della partita)
- l’informazione relativa ai giocatori online è ridondante tra classe `GameModel` e `Player`
- ridondante anche l’informazione relativa allo stato del `Player`
- wrapping superfluo per quanto riguarda `ItemTile` e `BoardCell`
- per come sono state progettate le `CommonGoal` card avendo il check all’interno del `GameModel` probabilmente il check sarà un unico metodo con uno`switch/case` che guarda al tipo dell’attributo `type` e implementa per ciascun caso il check di quello specifico.

## Confronto tra le architetture

- Il modo di gestire i Token per tener traccia del punteggio è molto simile.
- A livello di inizializzazione del gioco, potrebbe risultare comodo avere delle classi di appoggio (che siano factory che istanziano solo le carte da pescare oppure dei deck che collezionano tutte quelle possibili per poi permettere di pescarne alcune) come nella nostra architettura.
- Nella nostra implementazione, per gestire la Board (che non ha forma rettangolare) abbiamo utilizzato una seconda matrice di appoggio che costituisce una maschera. Quest’ultima varia in base al numero di giocatori della partita, così come specificato dalle regole. Di conseguenza la nostra maschera contiene l’informazione relativa al numero di Player necessari affinché una certa casella sia utilizzabile, mentre quelle escluse avranno settato un numero di giocatori più alto di quello possibile. 
Questa informazione non è contenuta all’interno dell’UML esaminato.
- Il modo in cui la classe `TileBag` è gestita è diverso. Noi usiamo una lista che può variare in dimensione, così da non dover “ricordare” in `size` quali tile siano state già rimosse, come invece viene fatto nell’architettura esaminata, che sembra usare un array di dimensione fissa.
- Diversi metodi come `addTileToPlayer()`, `fillBoardFromBag()` e `checkCommonGoal()` fanno parte dei metodi di `GameModel` quando potrebbero essere “delegati” alle classi che rappresentano gli oggetti su cui tali classi vanno ad interagire (per maggiore correttezza a livello di astrazione). I metodi equivalenti a questi presenti nella nostra architettura sono rispettivamente descritti nelle classi `Bookshelf` (equivalente di `PersonalBoard`), `Board` (equivalente di `LivingRoomBoard`) e `CommonGoalCard`.
- La nostra implementazione delle CommonGoal card prevede l’utilizzo di uno strategy pattern: abbiamo implementato una classe `CommonGoal` che mantenga l’informazione sui token e un metodo astratto `*checkGoal*()` (che a livello di astrazione ha poco senso essere metodo di `GameModel`), e una classe figlio per ogni diversa carta che implementi a modo suo il metodo astratto. In realtà nella nostra implementazione abbiamo raccolto alcune carte diverse all’interno di una unica sottoclasse dal momento che i check di questi vincoli possono essere fattorizzati utilizzando parametri.