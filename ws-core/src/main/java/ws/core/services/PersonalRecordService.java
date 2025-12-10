package ws.core.services;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ws.core.model.PersonalRecord;
import ws.core.model.embeded.PersonalRecordDetail;
import ws.core.model.filter.PersonalRecordFilter;
import ws.core.model.request.ReqPersonalRecordCreate;

public interface PersonalRecordService {
	public List<PersonalRecord> findAll(PersonalRecordFilter personalRecordFilter);
	public List<PersonalRecord> findByOldUser(PersonalRecordFilter personalRecordFilter);
	public List<PersonalRecord> findByTransferredUser(PersonalRecordFilter personalRecordFilter);
	public PersonalRecord create(ReqPersonalRecordCreate reqPersonalRecordCreate);
	public PersonalRecord update(String id,ReqPersonalRecordCreate updatePersonal);
	public PersonalRecord getOne(String id);
	public PersonalRecord transferUser(String id,String idUser);
	public PersonalRecordDetail getDetail(String id);
	public void delete(String id);
	public PersonalRecord doPutDocOrTask(String id,ReqPersonalRecordCreate update);
	public List<String> getListObjectIdsByPersonalIds(Pair<String, String> itemId);
}
