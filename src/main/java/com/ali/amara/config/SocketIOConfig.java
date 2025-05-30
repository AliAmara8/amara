package com.ali.amara.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour le serveur Socket.IO utilisé dans le chat en temps réel.
 */
@Configuration // Annotation Spring pour définir un bean
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration(); // Qualification explicite
        config.setHostname("localhost");
        config.setPort(8082);
        config.setOrigin("*"); // Autorise toutes origines pour tester
        SocketIOServer server = new SocketIOServer(config);
        server.start();
        System.out.println("Socket.IO server started on port 8082");
        server.addConnectListener(client -> System.out.println("Client connected: " + client.getSessionId()));
        return server;
    }
}