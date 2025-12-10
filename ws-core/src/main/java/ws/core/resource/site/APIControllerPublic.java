package ws.core.resource.site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import ws.core.enums.TaskStatus;
import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.embeded.TaskAssigneeFilter;
import ws.core.model.filter.embeded.TaskOwnerFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.services.OrganizationService;
import ws.core.services.TaskService;
import ws.core.util.DateTimeUtil;

@RestController
@RequestMapping("/api-public")
public class APIControllerPublic {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private TaskService taskService;
	
	@GetMapping("/report/tasks/list-tasks-owner")
	public Object listTaskOwner(
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "year", required = true, defaultValue = "2024") int year,
			@RequestParam(name = "export", required = false, defaultValue = "false") boolean export) throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Organization organization=organizationService.getOrganizationById(organizationId);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setParentId(organizationId);
		
		Date fromDate=DateTimeUtil.getDateStartOfYear(year);
		Date toDate=DateTimeUtil.getDateEndOfYear(year);
		if(DateTimeUtil.getYearAttmoment()==year) {
			toDate=DateTimeUtil.getDateEndOfDay(new Date());
		}
		
		TaskOrganizationCount taskOrganizationCount=new TaskOrganizationCount();
		taskOrganizationCount.setOrganizationId(organization.getId());
		taskOrganizationCount.setOrganizationName(organization.getName());
		taskOrganizationCount.setNumberOrder(organization.getOrder());
		
		List<TaskOrganizationCount> subOrganizationCounts=new ArrayList<TaskOrganizationCount>();
		List<Organization> organizations = organizationService.findOrganizationAll(organizationFilter);
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<TaskOrganizationCount>> listFuture = new ArrayList<Future<TaskOrganizationCount>>();
		
		for (Organization org : organizations) {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.setFromDate(fromDate.getTime());
			taskFilter.setToDate(toDate.getTime());
			
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(organizationId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
			
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(org.getId());
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
			
			/* Thread */
			CountTask countTask=new CountTask(org, taskFilter);
			listFuture.add(executor.submit(countTask));
		}
		
		for (Future<TaskOrganizationCount> future : listFuture) {
			TaskOrganizationCount item=future.get();
			
			subOrganizationCounts.add(item);
			
			taskOrganizationCount.setDht_th(taskOrganizationCount.getDht_th()+item.getDht_th());
			taskOrganizationCount.setDht_qh(taskOrganizationCount.getDht_qh()+item.getDht_qh());
			taskOrganizationCount.setDht_kh(taskOrganizationCount.getDht_kh()+item.getDht_kh());
			
			taskOrganizationCount.setCht_th(taskOrganizationCount.getCht_th()+item.getCht_th());
			taskOrganizationCount.setCht_qh(taskOrganizationCount.getCht_qh()+item.getCht_qh());
			taskOrganizationCount.setCht_kh(taskOrganizationCount.getCht_kh()+item.getCht_kh());
			
			taskOrganizationCount.setCxn_th(taskOrganizationCount.getCxn_th()+item.getCxn_th());
			taskOrganizationCount.setCxn_qh(taskOrganizationCount.getCxn_qh()+item.getCxn_qh());
			taskOrganizationCount.setCxn_kh(taskOrganizationCount.getCxn_kh()+item.getCxn_kh());
	    }
		executor.shutdown();
		
		Document result=new Document();
		if(export) {
//			String path="/" + fileLocalService.getFolderExportPublic() + "/" + getExportTongHopTinhHinh(fromDate, toDate, taskOrganizationCount, subOrganizationCounts);
//			result.append("filename", "thong-ke-tong-hop-tinh-hinh.xlsx");
//			result.append("path", path);
		}else {
			TaskFilter taskFilter=new TaskFilter();

			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(organizationId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
			
			TaskByMonths taskByMonths = getTaskByMonths(organization, taskFilter, year);
			
			result.append("fromDate", DateTimeUtil.getDateFormat().format(fromDate));
			result.append("toDate", DateTimeUtil.getDateFormat().format(toDate));
			result.append("months", taskByMonths);
			result.append("counts", taskOrganizationCount);
			result.append("subOrganizationCounts", subOrganizationCounts);
		}
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@Data
	public class TaskOrganizationCount {
		private String organizationId;
		private String organizationName;
		private int numberOrder;
		
		private long total=0;
		
		private long cht=0;
		private long cht_th=0;
		private long cht_qh=0;
		private long cht_kh=0;
		
		private long cxn=0;
		private long cxn_th=0;
		private long cxn_qh=0;
		private long cxn_kh=0;
		
		private long dht=0;
		private long dht_th=0;
		private long dht_qh=0;
		private long dht_kh=0;
		
		public long getCht() {
			cht=cht_th+cht_qh+cht_kh;
			return cht;
		}
		
		public long getCxn() {
			cxn=cxn_th+cxn_qh+cxn_kh;
			return cxn;
		}
		
		public long getDht() {
			dht=dht_th+dht_qh+dht_kh;
			return dht;
		}
		
		public long getTotal() {
			total=getCht()+getCxn()+getDht();
			return total;
		}
	}
	
	public class CountTask implements Callable<TaskOrganizationCount>{
		private Organization organization;
		private TaskFilter taskFilter;
		
		public CountTask(Organization organization, TaskFilter taskFilter) {
			this.organization=organization;
			this.taskFilter=taskFilter;
		}
		
		@Override
		public TaskOrganizationCount call() throws Exception {
			return getTaskAssigneeOrganizationCount(organization, taskFilter);
		}
	}
	
	private TaskOrganizationCount getTaskAssigneeOrganizationCount(Organization organization, TaskFilter taskFilter) {
		taskFilter.setStatus(TaskStatus.dahoanthanh_tronghan);
		long dht_th=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.dahoanthanh_quahan);
		long dht_qh=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.dahoanthanh_khonghan);
		long dht_kh=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.dangthuchien_tronghan);
		long cht_th=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.dangthuchien_quahan);
		long cht_qh=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.dangthuchien_khonghan);
		long cht_kh=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.choxacnhan_tronghan);
		long cxn_th=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.choxacnhan_quahan);
		long cxn_qh=taskService.countTaskAll(taskFilter);
		
		taskFilter.setStatus(TaskStatus.choxacnhan_khonghan);
		long cxn_kh=taskService.countTaskAll(taskFilter);
		
		TaskOrganizationCount taskOrganizationCount=new TaskOrganizationCount();
		taskOrganizationCount.setOrganizationId(organization.getId());
		taskOrganizationCount.setOrganizationName(organization.getName());
		taskOrganizationCount.setNumberOrder(organization.getOrder());
		
		taskOrganizationCount.setDht_th(dht_th);
		taskOrganizationCount.setDht_qh(dht_qh);
		taskOrganizationCount.setDht_kh(dht_kh);
		
		taskOrganizationCount.setCht_th(cht_th);
		taskOrganizationCount.setCht_qh(cht_qh);
		taskOrganizationCount.setCht_kh(cht_kh);
		
		taskOrganizationCount.setCxn_th(cxn_th);
		taskOrganizationCount.setCxn_qh(cxn_qh);
		taskOrganizationCount.setCxn_kh(cxn_kh);
		
		return taskOrganizationCount;
	}

	@Data
	public class TaskByMonths {
		private LinkedList<String> month=new LinkedList<String>();
		private LinkedList<Long> cht=new LinkedList<Long>();
		private LinkedList<Long> dht=new LinkedList<Long>();
	}
	
	private TaskByMonths getTaskByMonths(Organization organization, TaskFilter taskFilter ,int year) {
		TaskByMonths taskByMonths=new TaskByMonths();
		
		Date dateStartYear=DateTimeUtil.getDateStartOfYear(year);
		DateTimeZone timeZone = DateTimeZone.forID("Asia/Ho_Chi_Minh");
		int currentMonth=DateTimeUtil.getMonthAttmoment();
		if(year!=DateTimeUtil.getYearAttmoment()) {
			currentMonth=11;
		}
		for(int month=1;month<=currentMonth+1;month++) {
			LocalDateTime fromDateMonth=new LocalDateTime(dateStartYear.getTime(), timeZone);
			fromDateMonth=fromDateMonth.plusMonths(month-1).withDayOfMonth(fromDateMonth.dayOfMonth().getMinimumValue());
			
			LocalDateTime toDateMonth=new LocalDateTime(dateStartYear.getTime(), timeZone);
			toDateMonth=toDateMonth.plusMonths(month-1).withDayOfMonth(fromDateMonth.dayOfMonth().getMaximumValue());
			
			taskFilter.setFromDate(fromDateMonth.toDate().getTime());
			taskFilter.setToDate(toDateMonth.toDate().getTime());
			taskFilter.setStatus(TaskStatus.dangthuchien);
			long cht=taskService.countTaskAll(taskFilter);
			
			taskFilter.setStatus(TaskStatus.dahoanthanh);
			long dht=taskService.countTaskAll(taskFilter);
			
			taskByMonths.getMonth().add(String.valueOf(month));
			taskByMonths.getCht().add(cht);
			taskByMonths.getDht().add(dht);
		}
		return taskByMonths;
	}
}
