package ws.core.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ws.core.enums.UserProvider;
import ws.core.model.Organization;
import ws.core.model.RoleTemplate;
import ws.core.model.User;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.filter.RoleTemplateFilter;

@Service
public class DevService {

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected RoleTemplateService roleTemplateService;
	
	public List<Organization> importOrganization(String pathFile) throws InvalidFormatException, IOException{
		List<RoleTemplate> roleTemplates = roleTemplateService.findRoleTemplateAll(new RoleTemplateFilter());
		
		List<Organization> organizations=readFileExcel(pathFile);
		organizations.stream().forEach(e->{
			organizationService.save(e);
			e.setPath(organizationService.getPathOrganization(e));
			
			/* Gán thêm vai trò mẫu */
			e=addOrganizationRoleTemplates(roleTemplates, e);
			organizationService.save(e);
		});
		return organizations;
	}
	
	private Organization addOrganizationRoleTemplates(List<RoleTemplate> roleTemplates, Organization organization) {
		LinkedList<RoleOrganizationExpand> organizationExpands=new LinkedList<>();
		for (RoleTemplate roleTemplate : roleTemplates) {
			RoleOrganizationExpand organizationExpand=new RoleOrganizationExpand();
			organizationExpand.setActive(true);
			organizationExpand.setName(roleTemplate.getName());
			organizationExpand.setDescription(organizationExpand.getDescription());
			organizationExpand.setPermissionKeys(roleTemplate.getPermissionKeys());
			organizationExpand.setRoleTemplateId(roleTemplate.getId());
			
			organizationExpands.add(organizationExpand);
		}
		organization.setRoleOrganizationExpands(organizationExpands);
		return organization;
	}
	
	protected List<Organization> readFileExcel(String pathFile) throws IOException, InvalidFormatException{
		List<Organization> userImportRaws=new ArrayList<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(new File(pathFile));
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum()>=1) {
            	String organization_id=null;
            	String name=null;
            	String parent_id=null;
            	
            	DataFormatter dataFormatter=new DataFormatter();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    
                    if(cell.getColumnIndex()==0) {
                    	organization_id=dataFormatter.formatCellValue(cell);
                    	if(organization_id.isEmpty()) {
                    		break;
                    	}
                    }
                    
                    if(cell.getColumnIndex()==1) {

                    }
                    
                    if(cell.getColumnIndex()==2) {
                    	name=dataFormatter.formatCellValue(cell);
                    }
                    
                    if(cell.getColumnIndex()==3) {
                    	parent_id=dataFormatter.formatCellValue(cell);
                    }
                }
                
                Organization ldapInfo=new Organization();
                ldapInfo.setName(name);
                ldapInfo.setDescription(name);
                ldapInfo.setParentId(parent_id);
                ldapInfo.setUnitCode(organization_id);
            	userImportRaws.add(ldapInfo);
            }
        }
        workbook.close();
		
        userImportRaws.stream().forEach(e->{
			boolean check=false;
			String parent_id=e.getParentId();
			for(Organization ee:userImportRaws) {
				String organization_id=ee.getUnitCode();
				if(parent_id.equals(organization_id)) {
					e.setParentId(ee.getId());
					check=true;
					break;
				}
			}
			if(check==false) {
				e.setParentId(null);
				System.out.println("====> NO "+e.getName());
			}
		});
		
		return userImportRaws;
	}
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public void fixDataUsers() {
		List<User> users=userService.findUserAll();
		for (User user : users) {
			if(user.getUsername()!=null && !user.getUsername().equals("administrator")) {
				user.setPassword(passwordEncoder.encode("123"));
				user.setProvider(UserProvider.local);
				userService.saveUser(user);
			}
		}
	}
}
