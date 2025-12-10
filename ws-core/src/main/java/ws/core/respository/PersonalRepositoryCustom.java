package ws.core.respository;

import java.util.List;

import ws.core.model.PersonalRecord;
import ws.core.model.filter.PersonalRecordFilter;

public interface PersonalRepositoryCustom {
	public List<PersonalRecord> findAll(PersonalRecordFilter filter);
	public PersonalRecord findById(PersonalRecordFilter filter);
	public List<PersonalRecord> findPersonalsByOldUser(PersonalRecordFilter filter);
	public List<PersonalRecord> findPersonalsByTransferredUser(PersonalRecordFilter filer);
}
