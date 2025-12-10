package ws.core.api_grpc.logs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogQueue {
	public static final BlockingQueue<ILoggingEvent> QUEUE = new LinkedBlockingDeque<ILoggingEvent>(20000);
}
