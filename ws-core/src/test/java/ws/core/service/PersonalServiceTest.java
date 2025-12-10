package ws.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import ws.core.model.PersonalRecord;
import ws.core.model.filter.PersonalRecordFilter;
import ws.core.model.request.ReqPersonalRecordCreate;
import ws.core.respository.PersonalRecordRepository;
import ws.core.respository.PersonalRepositoryCustom;
import ws.core.service.impl.PersonalServiceImpl;
import ws.core.services.TaskService;

@ExtendWith(MockitoExtension.class)
public class PersonalServiceTest {
	
	@Mock
	private PersonalRecordRepository personalRecordRepository;
	
	@Mock
	private PersonalRepositoryCustom personalRepositoryCustom;
	
	@Mock
	private TaskService taskService;
	
	@Mock
	private ModelMapper modelMapper;
	
	@InjectMocks
	private PersonalServiceImpl personalServiceImpl;
	
	private PersonalRecord mockPersonalRecord;
	private ReqPersonalRecordCreate reqPersonalRecordCreate;
	
	@BeforeEach
	void setUp() {
		mockPersonalRecord = new PersonalRecord();
        mockPersonalRecord.setId(new ObjectId());
        mockPersonalRecord.setTitle("Test Record");
        mockPersonalRecord.setDescription("Test Description");
        mockPersonalRecord.setCreatedTime(new Date());
        mockPersonalRecord.setUpdatedTime(null);
        mockPersonalRecord.setDocs(new ArrayList<String>(Arrays.asList("abc")));
        mockPersonalRecord.setTasks(new ArrayList<String>(Arrays.asList("123")));

        reqPersonalRecordCreate = new ReqPersonalRecordCreate();
        reqPersonalRecordCreate.setTitle("Test Record");
        reqPersonalRecordCreate.setDescription("Test Description");
        reqPersonalRecordCreate.setUserId("user123");
        reqPersonalRecordCreate.setDocs(new ArrayList<String>(Arrays.asList("abc")));
        reqPersonalRecordCreate.setTasks(new ArrayList<String>(Arrays.asList("123")));
	}
	
	@Test
	void whenFindAll_thenReturnListOfPersonalRecords() {
		PersonalRecordFilter filter = new PersonalRecordFilter();
		when(personalRepositoryCustom.findAll(filter)).thenReturn(List.of(mockPersonalRecord));
		
		List<PersonalRecord> result = personalServiceImpl.findAll(filter);
		
		assertEquals(1, result.size()); // Kiểm tra số lượng phần tử
	    assertEquals("Test Record", result.get(0).getTitle()); // Kiểm tra giá trị title
	    verify(personalRepositoryCustom, times(1)).findAll(filter); // Verify đã gọi repository
	}
	
	
	

}
