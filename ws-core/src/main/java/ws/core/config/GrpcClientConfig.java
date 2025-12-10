package ws.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ws.core.proto.LogCollectorGrpc;

@Configuration
public class GrpcClientConfig {
	@Bean
	public ManagedChannel grpcChannel() {
		return ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
	}
	
	@Bean LogCollectorGrpc.LogCollectorBlockingStub logClient(ManagedChannel channel){
		return LogCollectorGrpc.newBlockingStub(channel);
	}
	
}
