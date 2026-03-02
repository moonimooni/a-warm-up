package kr.co.growmeal.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
public class EmbeddedRedisConfig {

    private static final RedisServer redisServer;
    private static final int redisPort;

    static {
        try {
            redisPort = findAvailablePort();
            redisServer = new RedisServer(redisPort);
            redisServer.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start embedded Redis", e);
        }
    }

    public static int getRedisPort() {
        return redisPort;
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
