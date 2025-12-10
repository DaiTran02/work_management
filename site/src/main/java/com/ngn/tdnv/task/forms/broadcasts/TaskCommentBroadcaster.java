package com.ngn.tdnv.task.forms.broadcasts;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.ngn.tdnv.task.models.TaskCommentModel;
import com.vaadin.flow.shared.Registration;

public class TaskCommentBroadcaster {
	static Executor executor = Executors.newSingleThreadExecutor();

	static LinkedList<Consumer<List<TaskCommentModel>>> listeners = new LinkedList<Consumer<List<TaskCommentModel>>>();

	public static synchronized Registration register(
			Consumer<List<TaskCommentModel>> listener) {
		listeners.add(listener);

		return () -> {
			synchronized (TaskCommentBroadcaster.class) {
				listeners.remove(listener);
			}
		};
	}

	public static synchronized void broadcast(List<TaskCommentModel> message) {
		for (Consumer<List<TaskCommentModel>> listener : listeners) {
			executor.execute(() -> listener.accept(message));
		}
	}
}
