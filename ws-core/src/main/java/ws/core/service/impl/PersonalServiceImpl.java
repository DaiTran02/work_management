package ws.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import ws.core.model.Doc;
import ws.core.model.PersonalRecord;
import ws.core.model.Task;
import ws.core.model.User;
import ws.core.model.embeded.PersonalRecordDetail;
import ws.core.model.embeded.PersonalUser;
import ws.core.model.filter.PersonalRecordFilter;
import ws.core.model.request.ReqPersonalRecordCreate;
import ws.core.model.response.util.DocUtil;
import ws.core.model.response.util.TaskUtil;
import ws.core.respository.PersonalRecordRepository;
import ws.core.respository.PersonalRepositoryCustom;
import ws.core.respository.UserRepository;
import ws.core.services.DocService;
import ws.core.services.PersonalRecordService;
import ws.core.services.TaskService;

@Service
public class PersonalServiceImpl implements PersonalRecordService{
	@Autowired
	private PersonalRecordRepository personalRecordRepository;
	
	@Autowired
	private PersonalRepositoryCustom personalRepositoryCustom;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private DocService docService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DocUtil docUtil;
	
	@Autowired
	private TaskUtil taskUtil;
	

	@Override
	public List<PersonalRecord> findAll(PersonalRecordFilter personalRecordFilter) {
		return personalRepositoryCustom.findAll(personalRecordFilter);
	}

	@Override
	public PersonalRecord create(ReqPersonalRecordCreate reqPersonalRecordCreate) {
		PersonalRecord personalRecord = modelMapper.map(reqPersonalRecordCreate, PersonalRecord.class);
		Optional<User> dataUser = userRepository.findById(new ObjectId(reqPersonalRecordCreate.getUserId()));
		if(dataUser.isPresent()) {
			User user = dataUser.get();
			PersonalUser personalUser = new PersonalUser();
			personalUser.setIdUser(user.getId());
			personalUser.setFullName(user.getFullName());
			personalRecord.setCurrentUser(personalUser);
			personalRecord.setRootUser(personalUser);
		}
		personalRecord.setCreatedTime(new Date());
		personalRecord.setUpdatedTime(null);
		
		return personalRecordRepository.save(personalRecord);
	}
	
	@Override
	public PersonalRecord update(String id,ReqPersonalRecordCreate updatePersonal) {
		PersonalRecord personalRecordUpdate = getOne(id);
		Date createDate = personalRecordUpdate.getCreatedTime();
		PersonalUser rootUser = personalRecordUpdate.getRootUser();
		List<PersonalUser> listOlds = personalRecordUpdate.getOldUsers();
		List<PersonalUser> listTransfered = personalRecordUpdate.getUsersTransfered();
		Date transferedTime = personalRecordUpdate.getTransferTime();
		
		
		personalRecordUpdate = modelMapper.map(updatePersonal, PersonalRecord.class);
		personalRecordUpdate.setRootUser(rootUser);
		
		Optional<User> user = userRepository.findById(new ObjectId(updatePersonal.getUserId()));
		if(user.isPresent()) {
			User data = user.get();
			PersonalUser personalUser = new PersonalUser();
			personalUser.setIdUser(data.getId());
			personalUser.setFullName(data.getFullName());
			personalRecordUpdate.setCurrentUser(personalUser);
		}
		
		personalRecordUpdate.setUpdatedTime(new Date());
		personalRecordUpdate.setCreatedTime(createDate);
		personalRecordUpdate.setOldUsers(listOlds);
		personalRecordUpdate.setUsersTransfered(listTransfered);
		personalRecordUpdate.setTransferTime(transferedTime);
		
		personalRecordUpdate.setId(new ObjectId(id));
		
		return personalRecordRepository.save(personalRecordUpdate);
	}

	@Override
	public PersonalRecord getOne(String id) {
		Optional<PersonalRecord> data = personalRecordRepository.findById(new ObjectId(id));
		if(data.isPresent()) {
			return data.get();
		}
		throw new RuntimeException("Không tìm thấy dữ liệu");
	}
	

	@Override
	public void delete(String id) {
		if(getOne(id) == null) throw new NotFoundException("Không tìm thấy hồ sơ cá nhân");
		personalRecordRepository.deleteById(new ObjectId(id));
	}

	@Override
	public PersonalRecord transferUser(String id, String idUser) {
		PersonalRecord personalRecord = getOne(id);
		if(personalRecord == null) throw new NotFoundException("Không tìm thấy dữ liệu");
		personalRecord.setId(new ObjectId(id));
		
		List<PersonalUser> listOldUsers = new ArrayList<PersonalUser>();
		if(personalRecord.getOldUsers() != null) {
			listOldUsers.addAll(personalRecord.getOldUsers());
		}
		
		Optional<User> odlUser = userRepository.findById(new ObjectId(personalRecord.getCurrentUser().getIdUser()));
		if(odlUser.isPresent()) {
			User data = odlUser.get();
			PersonalUser personalUser = new PersonalUser();
			personalUser.setIdUser(data.getId());
			personalUser.setFullName(data.getFullName());
			listOldUsers.add(personalUser);
		}
		personalRecord.setOldUsers(listOldUsers);
		personalRecord.setTransferTime(new Date());
		
		List<PersonalUser> listUserTransfered = new ArrayList<PersonalUser>();
		
		Optional<User> newUser = userRepository.findById(new ObjectId(idUser));
		if(newUser.isPresent()) {
			User data = newUser.get();
			PersonalUser personalUser = new PersonalUser();
			personalUser.setIdUser(data.getId());
			personalUser.setFullName(data.getFullName());
			personalRecord.setCurrentUser(personalUser);
			listUserTransfered.add(personalUser);
		}
		personalRecord.setUsersTransfered(listUserTransfered);
		
		personalRecord.setRootUser(null);
		
		return personalRecordRepository.save(personalRecord);
	}

	@Override
	public PersonalRecordDetail getDetail(String id) {
		PersonalRecord personalRecord = getOne(id);
		if(personalRecord == null) throw new NotFoundException("Không tìm thấy dữ liệu");
		PersonalRecordDetail personalRecordOutput = new PersonalRecordDetail();
		personalRecordOutput.setCreatedTime(personalRecord.getCreatedTime());
		personalRecordOutput.setUpdatedTime(personalRecord.getUpdatedTime());
		personalRecordOutput.setId(personalRecord.getId().toHexString());
		personalRecordOutput.setTitle(personalRecord.getTitle());
		personalRecordOutput.setDescription(personalRecord.getDescription());
		personalRecordOutput.setCreator(personalRecord.getCreator());
		personalRecordOutput.setOldUsers(personalRecord.getOldUsers());
		
		List<Document> listTasks = new ArrayList<Document>();
		if(personalRecord.getTasks() != null) {
			personalRecord.getTasks().forEach(idTaks->{
				try {
					Task task = taskService.getTaskById(idTaks);
					if(task != null) {
						listTasks.add(taskUtil.toDetailSiteResponse(task));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		personalRecordOutput.setTasks(listTasks);
		
		List<Document> listDocs = new ArrayList<Document>();
		if(personalRecord.getDocs() != null) {
			personalRecord.getDocs().forEach(idDoc->{
				try {
					Doc doc = docService.getDocById(idDoc);
					if(doc != null) {
						listDocs.add(docUtil.toSiteResponse(doc));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		personalRecordOutput.setDocs(listDocs);
		
		return personalRecordOutput;
	}

	@Override
	public List<PersonalRecord> findByOldUser(PersonalRecordFilter personalRecordFilter) {
		return personalRepositoryCustom.findPersonalsByOldUser(personalRecordFilter);
	}

	@Override
	public PersonalRecord doPutDocOrTask(String id,ReqPersonalRecordCreate update) {
		PersonalRecord personalRecord = getOne(id);
		personalRecord.setId(new ObjectId(id));
		if(update.getDocs() != null) {
			update.getDocs().forEach(model->{
				personalRecord.getDocs().add(model);
			});
		}
		
		if(update.getTasks() != null) {
			update.getTasks().forEach(model->{
				personalRecord.getTasks().add(model);
			});
		}
		return personalRecordRepository.save(personalRecord);
	}

	@Override
	public List<PersonalRecord> findByTransferredUser(PersonalRecordFilter personalRecordFilter) {
		return personalRepositoryCustom.findPersonalsByTransferredUser(personalRecordFilter);
	}

	@Override
	public List<String> getListObjectIdsByPersonalIds(Pair<String, String> itemId) {
		PersonalRecord personalRecord = getOne(itemId.getValue());
		List<String> listIds = new ArrayList<String>();
		if(itemId.getKey().equals("Doc")) {
			listIds.addAll(personalRecord.getDocs());
		}
		
		if(itemId.getKey().equals("Task")) {
			listIds.addAll(personalRecord.getTasks());
		}
		return listIds;
	}

}
