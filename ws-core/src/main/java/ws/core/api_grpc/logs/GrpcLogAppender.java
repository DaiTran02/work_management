package ws.core.api_grpc.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class GrpcLogAppender extends AppenderBase<ILoggingEvent>{
	
	private static boolean workerStarted = false;
	
	
	@Override
	public void start() {
		super.start();
		
		if(!workerStarted) {
			workerStarted = true;
			Thread work = new Thread(new GrpcLogWorker(),"grpc-log-worker");
			work.setDaemon(true);
			work.start();
		}
		
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		LogQueue.QUEUE.offer(eventObject);
	}

}
