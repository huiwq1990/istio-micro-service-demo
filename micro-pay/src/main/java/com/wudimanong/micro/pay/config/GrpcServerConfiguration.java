package com.wudimanong.micro.pay.config;

import com.wudimanong.micro.pay.provider.PayCoreProvider;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author jiangqiao
 */
@Slf4j
@Component
public class GrpcServerConfiguration {

    @Autowired
    PayCoreProvider service;

    /**
     * 注入配置文件中的端口信息
     */
    @Value("${grpc.server-port}")
    private int port;
    private Server server;

    public void start() throws IOException {
        // 构建服务端
        log.info("Starting gRPC on port {}.", port);
        server = ServerBuilder.forPort(port).addService(service).build().start();
        log.info("gRPC server started, listening on {}.", port);

        // 添加服务端关闭的逻辑
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down gRPC server.");
            GrpcServerConfiguration.this.stop();
            log.info("gRPC server shut down successfully.");
        }));
    }

    private void stop() {
        if (server != null) {
            // 关闭服务端
            server.shutdown();
        }
    }

    public void block() throws InterruptedException {
        if (server != null) {
            // 服务端启动后直到应用关闭都处于阻塞状态，方便接收请求
            server.awaitTermination();
        }
    }
}
