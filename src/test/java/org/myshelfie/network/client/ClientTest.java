//package org.myshelfie.network.client;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.io.OutputStream;
//import java.net.Socket;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//public class ClientTest {
//
//    @Mock
//    Socket serverSocketMock;
//
//    @Mock
//    OutputStream serverOutputStreamMock;
//
//    @InjectMocks
//    Client client;
//
////    @Test
////    public void testConstructorWithRMI() {
////        // Arrange
////        String nickname = "TestUser";
////        boolean isRMI = true;
////        Server rmiServerMock = mock(Server.class);
////        String serverURL = "//localhost/" + Client.RMI_SERVER_NAME;
////        doNothing().when(rmiServerMock).register(any());
////        Client client;
////        try {
////            Registry registry = LocateRegistry.createRegistry(8799);
////
////            // bind the serverMock to the RMI registry
////            registry.rebind(serverURL, rmiServerMock);
////
////            // Act
////            client = new Client(nickname, isRMI);
////
////            // unbind the serverMock from the RMI registry
////            registry.unbind(serverURL);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////
////        // Assert
////        assertEquals(nickname, client.getNickname());
////        assertTrue(client.isRMI());
////        verify(rmiServerMock).register(client);
////    }
//
//    @Test
//    public void testSocketConstructor() {
//        // Arrange
//        String nickname = "TestUser";
//        boolean isRMI = false;
//
//        // Act
//        client = new Client(nickname, isRMI);
//
//        // Assert
//        assertEquals(nickname, client.getNickname());
//        assertFalse(client.isRMI());
//    }
//
//    /*
//    @Test
//    public void testUpdate() {
//        // Arrange
//        Client client = new Client("TestUser", true);
//        Object argument = "test argument";
//        Event ev = Event.BOOKSHELF_UPDATE;
//
//        // Act and Assert
//        // Since the method only prints to the console, there is no way to directly test the output.
//        // We can indirectly test that the method doesn't throw an exception.
//        assertDoesNotThrow(() -> client.update(argument, ev));
//    }
//
//    @Test
//    public void testUpdateServerWithRMI() {
//        // Arrange
//        Client client = new Client("TestUser", true);
//        CommandMessageWrapper msg = new CommandMessageWrapper("test command");
//
//        // Act and Assert
//        assertDoesNotThrow(() -> client.updateServer(msg));
//    }
//
//    @Test
//    public void testUpdateServerWithoutRMI() throws Exception {
//        // Arrange
//        Client client = new Client("TestUser", false);
//        CommandMessageWrapper msg = new CommandMessageWrapper("test command");
//        Socket socketMock = mock(Socket.class);
//        when(socketMock.getOutputStream()).thenReturn(mock(OutputStream.class));
//
//        // Act and Assert
//        assertDoesNotThrow(() -> client.updateServer(msg));
//    }*/
//}