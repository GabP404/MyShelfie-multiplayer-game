package org.myshelfie.network.messages.gameMessages;

import java.io.Serializable;

public class EventWrapper implements Serializable {

        private GameEvent type;
        private GameView message;

        /**
         * @param m Information to be sent
         * @param t Type of the information to be sent
         */
        public EventWrapper(GameView m, GameEvent t) {
            type = t;
            message = m;
        }

        public GameEvent getType() {
            return type;
        }

        public GameView getMessage() {
            return message;
        }
}
