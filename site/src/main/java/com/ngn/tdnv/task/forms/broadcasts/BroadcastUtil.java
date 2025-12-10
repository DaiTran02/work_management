package com.ngn.tdnv.task.forms.broadcasts;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.vaadin.flow.shared.Registration;

public class BroadcastUtil {
    static Executor executor = Executors.newSingleThreadExecutor();
    static List<Consumer<List<JsonObject>>> listeners = new LinkedList<>();

    public static synchronized Registration register(Consumer<List<JsonObject>> listener) {
        listeners.add(listener);
        return () -> {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(List<JsonObject> message) {
        for (Consumer<List<JsonObject>> listener : listeners) {
            executor.execute(() -> listener.accept(message));
        }
    }
}

