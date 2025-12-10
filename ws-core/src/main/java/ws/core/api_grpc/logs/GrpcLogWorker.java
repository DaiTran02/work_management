package ws.core.api_grpc.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ws.core.proto.LogBatch;
import ws.core.proto.LogCollectorGrpc;
import ws.core.proto.LogEntry;

public class GrpcLogWorker implements Runnable{
	
	private LogCollectorGrpc.LogCollectorBlockingStub stub;
	private ManagedChannel channel;
	
	private final int batchSize = 20;
	private final int flushIntervalMs = 1000;

	@Override
	public void run() {
		connect();
		
		List<ILoggingEvent> buffer = new ArrayList<ILoggingEvent>();
		
		while(true) {
			try {
				ILoggingEvent event = LogQueue.QUEUE.poll(flushIntervalMs, TimeUnit.MILLISECONDS);
				
				if(event != null) {
					buffer.add(event);
				}
				
				if(!buffer.isEmpty() && buffer.size() >= batchSize) {
					flush(buffer);
					buffer.clear();
				}
				
			}catch(Exception e) {
				break;
			}
		}
		
	}
	
	private void flush(List<ILoggingEvent> events) {
		try {
			LogBatch.Builder batch = LogBatch.newBuilder();
			
			for(ILoggingEvent e: events) {
				batch.addLogs(LogEntry.newBuilder().setServiceName("ws-core").setMessage(e.getMessage()).setLevel(e.getLevel().toString()).setTimestamp(e.getTimeStamp()).build());
			}
			stub.pushLogBatch(batch.build());
		}catch(Exception e) {
			reconnect();
		}
		
	}
	
	private void connect() {
		channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		stub = LogCollectorGrpc.newBlockingStub(channel);
	}
	
	private void reconnect() {
		try {
			channel.shutdownNow();
			
		}catch(Exception e) {
		}
		connect();
	}

}
