package ws.core.respository.listener;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

import ws.core.model.Doc;

@Component
public class DocRepositoryListener extends AbstractMongoEventListener<Doc>{

	@Override
	public void onAfterSave(AfterSaveEvent<Doc> event) {
		super.onAfterSave(event);
		System.out.println("Listening doc save done ...");
	}
}
