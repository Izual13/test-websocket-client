package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class WSClient {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SpringApplication.run(WSClient.class, args);
        List<Transport> transports = new ArrayList<>(2);
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        transports.add(new WebSocketTransport(webSocketClient));


        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        String url = "ws://localhost:8080/socket";
        stompClient.connect(url, new StompSessionHandler());

    }


    static class StompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            super.afterConnected(session, connectedHeaders);
            session.subscribe("/topic", new StringHandler());
            System.out.println("connected");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            super.handleException(session, command, headers, payload, exception);

            exception.printStackTrace();
        }

    }

    static class StringHandler extends StompSessionHandlerAdapter {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            super.handleException(session, command, headers, payload, exception);
            System.out.println("exception: " + exception.getMessage());
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            super.handleFrame(headers, payload);
            System.out.println(new String((byte[]) payload));
        }
    }
}