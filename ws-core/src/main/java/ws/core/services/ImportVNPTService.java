package ws.core.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.enums.UserProvider;
import ws.core.model.Organization;
import ws.core.model.RoleTemplate;
import ws.core.model.User;
import ws.core.model.embeded.RoleOrganizationExpand;
import ws.core.model.embeded.UserOrganizationExpand.AddFromSource;
import ws.core.model.filter.RoleTemplateFilter;
import ws.core.util.TextUtil;

@Service
public class ImportVNPTService {

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected RoleTemplateService roleTemplateService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Thêm Đơn vị và Người dùng từ kho dữ liệu
	 * @param pathFolder
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public List<Document> importOrganizationsAndUser(String pathFolder, String provider, String passwordDefault){
		 // Tạo đối tượng Path từ đường dẫn thư mục
        Path path = Paths.get(pathFolder);

        // Kiểm tra xem đường dẫn có tồn tại và là thư mục không
        if(Files.exists(path) && Files.isDirectory(path)) {
        	List<Document> results=new ArrayList<>();
        	
        	List<Path> pathFiles;
			try {
				pathFiles = listFilesInDirectory(pathFolder);
			} catch (IOException e) {
				throw new NotAcceptableExceptionAdvice("Thư mục rỗng không có tệp");
			}
			
//			ExecutorService executor = Executors.newFixedThreadPool(5);
//			if(pathFiles.size()>0) {
//				executor = Executors.newFixedThreadPool(pathFiles.size());
//			}
//			List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
			
        	for (Path pathFile : pathFiles) {
//        		listFuture.add(executor.submit(new Callable<Document>() {
//					@Override
//					public Document call() throws Exception {
//						Document dataImport=new Document();
//		        		dataImport.put("file", pathFile.getFileName().toString());
//						try {
//							Document statusOrganizationsImport=new Document();
//				    		try { 
//				    			statusOrganizationsImport = importOrganizations(pathFile.toAbsolutePath().toString());
//				    		} catch (InvalidFormatException e) {
//				    			e.printStackTrace();
//				    		} catch (IOException e) {
//				    			e.printStackTrace();
//				    		}
//				    		 
//				    		User administratorUser=userService.getUserByUserName("administrator");
//				    		Document statusUsersImport=new Document();
//				    		try {
//				    			statusUsersImport = importUsers(pathFile.toAbsolutePath().toString(), administratorUser, provider, passwordDefault);
//				    		} catch (InvalidFormatException e) {
//				    			e.printStackTrace();
//				    		} catch (IOException e) {
//				    			e.printStackTrace();
//				    		}
//				    		dataImport.put("organizations", statusOrganizationsImport);
//				    		dataImport.put("users", statusUsersImport);
//						} catch (Exception e) {
//							e.printStackTrace();
//							dataImport.put("error", e.getMessage());
//						}
//						return dataImport;
//					}
//				}));
        		
        		Document dataImport=new Document();
        		dataImport.put("file", pathFile.getFileName().toString());
				try {
					Document statusOrganizationsImport=new Document();
		    		try { 
		    			statusOrganizationsImport = importOrganizations(pathFile.toAbsolutePath().toString());
		    		} catch (InvalidFormatException e) {
		    			e.printStackTrace();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    		 
		    		User administratorUser=userService.getUserByUserName("administrator");
		    		Document statusUsersImport=new Document();
		    		try {
		    			statusUsersImport = importUsers(pathFile.toAbsolutePath().toString(), administratorUser, provider, passwordDefault);
		    		} catch (InvalidFormatException e) {
		    			e.printStackTrace();
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    		dataImport.put("organizations", statusOrganizationsImport);
		    		dataImport.put("users", statusUsersImport);
				} catch (Exception e) {
					e.printStackTrace();
					dataImport.put("error", e.getMessage());
				}
				results.add(dataImport);
			}
        	
//        	for (Future<Document> future : listFuture) {
//        		try {
//					results.add(future.get());
//				} catch (InterruptedException | ExecutionException e) {
//					e.printStackTrace();
//				}
//        	}
//        	executor.shutdown();
        	
			/* Cập nhật lại */
        	//userService.updateOrganizationEachUser();
    		return results;
        }
        throw new NotFoundElementExceptionAdvice("["+pathFolder+"] không tồn tại hoặc không phải là thư mục");
	}
	
	public Document importOrganizations(String pathFile) throws InvalidFormatException, IOException {
		Document data=readFileExcelOrganizationsAndImportOrganizations(pathFile);
		return data;
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
	
	protected Document readFileExcelOrganizationsAndImportOrganizations(String pathFile) throws IOException, InvalidFormatException{
		System.out.println("pathFile: "+pathFile);
		List<Organization> organizationImportRaws=new ArrayList<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(new File(pathFile));
        
        // Lấy sheet 1
        XSSFSheet sheet = workbook.getSheetAt(1);
        
        // Tạo DataFormatter để định dạng giá trị ô
        DataFormatter dataFormatter = new DataFormatter();

        // Tạo FormulaEvaluator để đánh giá công thức
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
		/* Danh sách những dòng bị lỗi không thêm vào được */
        List<Document> errorRows=new ArrayList<>();
        
        Iterator<Row> rowIterator = sheet.iterator();
        String parentUnitCode=null;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum()>=1) {
				/* Dữ liệu ghi lại lỗi */
            	Document errorRow=new Document();
            	errorRow.put("rowNumber", row.getRowNum());
            	
            	String unitCode=null;
            	String name=null;
            	//String parent_id=null;
            	
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    
					/* Tên đơn vị */
                    if(cell.getColumnIndex()==1) {
                    	name=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("name", name);
                    }
                    
					/* Mã định danh */
                    if(cell.getColumnIndex()==2) {
                    	unitCode=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("unitCode", unitCode);
                    }
                    
					/* Mã định danh cha */
                    if(cell.getColumnIndex()==3) {
						/*
						 * Nếu khác rỗng thì mới lấy, còn lại là lấy liền kế trước đó làm cha cho Đơn vị
						 * hiện tại
						 */
                    	if(!StringUtils.isEmpty(dataFormatter.formatCellValue(cell, evaluator))) {
                    		parentUnitCode=dataFormatter.formatCellValue(cell, evaluator);
                    		errorRow.put("parentUnitCode", parentUnitCode);
                    	}
                    }
                }
                
				/* Nếu không có mã định danh thì bỏ qua và ghi nhận lỗi */
                if(unitCode==null || unitCode.isEmpty()) {
                	errorRows.add(errorRow);
                }else {
					/* Nếu unitCode là parentUnitCode là sai -> bỏ qua */
                	if(parentUnitCode!=null && unitCode.equals(parentUnitCode)) {
                		errorRows.add(errorRow);
                		continue;
                	}
                	
                	Organization organizationInfo=new Organization();
                	Optional<Organization> checkOrgExists=organizationService.findOrganizationByUnitCode(unitCode);
    				if(checkOrgExists.isPresent()) {
    					organizationInfo.setId(checkOrgExists.get().getObjectId());
    				}
    				organizationInfo.setName(name);
					organizationInfo.setDescription(name);
					/* Lưu tạm parentId là mã đơn vị */
					organizationInfo.setParentId(parentUnitCode);
					
					/* Backup mã đơn vị cha */
					organizationInfo.setParentUnitCode(parentUnitCode);
					organizationInfo.setUnitCode(unitCode);
					if(organizationInfo.getUnitCode()!=null) {
						organizationInfo.setLevel(organizationService.autoDetectDefaultByUnitCode(organizationInfo.getUnitCode()).getKey());
					}
					
                	organizationImportRaws.add(organizationInfo);
                }
            }
        }
        workbook.close();
		
		/* Lặp qua danh sách đơn vị trong excel để kiểm tra đơn vị cha*/
        organizationImportRaws.stream().forEach(e->{
			boolean checkParentExists=false;
			String parent_organization_id=e.getParentId();
			
			/* Cập nhật lại parentId trong Organization Model lại ObjectId theo mã định danh (nhưng chỉ trong đơn vị) */
			for(Organization ee:organizationImportRaws) {
				String organization_id=ee.getUnitCode();
				if(parent_organization_id!=null && parent_organization_id.equals(organization_id)) {
					e.setParentId(ee.getId());
					checkParentExists=true;
					break;
				}
			}
			
			/*
			 * Kiểm tra nếu đơn vị đó parentOrganizationId không tồn tại thì sẽ set lại
			 * null, sử dụng parentUnitCode đồng bộ sau
			 */
			if(checkParentExists==false) {
				/* Phải set parentId là null để tránh lỗi setPath, đã có parentUnitCode hỗ trợ lấy lại parentId sau khi import toàn bộ */
				e.setParentId(null);
				System.out.println("====> NO "+e.getName());
			}
		});
        
        /* Kiểm tra những Đơn vị parentUnitCode có giá trị thì tìm trong DB */
        organizationImportRaws.stream().forEach(e->{
			/* Có mã đơn vị cha là mã định danh */
			if(e.getParentUnitCode()!=null) {
				Optional<Organization> findOrganization=organizationService.findOrganizationByUnitCode(e.getParentUnitCode());
				if(findOrganization.isPresent()) {
					e.setParentId(findOrganization.get().getId());
				}
			}
		});
        
		/* Thêm vai trò vào đơn vị sẵn */
        List<RoleTemplate> roleTemplates = roleTemplateService.findRoleTemplateAll(new RoleTemplateFilter());
		organizationImportRaws.stream().forEach(e->{
			try {
				organizationService.save(e);
				e.setPath(organizationService.getPathOrganization(e));
				
				/* Gán thêm vai trò mẫu */
				e=addOrganizationRoleTemplates(roleTemplates, e);
				organizationService.save(e);
				
				System.out.println("--> Đơn vị ["+e.getName()+"] import/update thành công");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		Document result=new Document();
		result.put("errorRows", errorRows);
		
		return result;
	}
	
	public Document importUsers(String pathFile, User creator, String userProvider, String passwordDefault) throws InvalidFormatException, IOException{
		Document data =readFileExcelUsersAndImportUsers(pathFile, creator, userProvider, passwordDefault);
		return data;
	}
	
	protected Document readFileExcelUsersAndImportUsers(String pathFile, User creator, String userProvider, String passwordDefault) throws IOException, InvalidFormatException{
		System.out.println("pathFile: "+pathFile);
		List<User> userImportRaws=new ArrayList<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(new File(pathFile));
		
		// Lấy sheet đầu tiên
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        // Tạo DataFormatter để định dạng giá trị ô
        DataFormatter dataFormatter = new DataFormatter();

        // Tạo FormulaEvaluator để đánh giá công thức
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
		/* Danh sách dòng bị lỗi không thêm vào được (không tính đã tồn tại)*/
        List<Document> errorRows=new ArrayList<>();
        
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum()>=1) {
				/* Ghi lại dòng bị lỗi nếu có */
            	Document errorRow=new Document();
            	errorRow.put("rowNumber", row.getRowNum());
            	
            	String fullName=null;
            	String jobTitle=null;
            	String roleLists=null;
            	String numLevel=null;
            	String username=null;
            	String email=null;
            	String phone=null;
            	boolean active=true;
            	String activeCode=TextUtil.generalRandomActiveCode();
            	String unitCode=null;
            	String roomName=null;
            	String organizationName=null;

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    
					/* Họ tên */
                    if(cell.getColumnIndex()==1) {
                    	fullName=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("fullName", fullName);
                    }
                    
					/* Chức vụ */
                    if(cell.getColumnIndex()==2) {
                    	jobTitle=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("jobTitle", jobTitle);
                    }
                    
                    /* Vai trò */
                    if(cell.getColumnIndex()==3) {
                    	roleLists=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("roleLists", roleLists);
                    }
                    
                    /* Cấp bậc */
                    if(cell.getColumnIndex()==4) {
                    	numLevel=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("numLevel", numLevel);
                    }
                    
                    /* Email */
                    if(cell.getColumnIndex()==5) {
                    	email=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("email", email);
                    }
                
                    /* Username */
                    if(cell.getColumnIndex()==6) {
                    	username=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("username", username);
                    }
                    
                    /* Phone */
                    if(cell.getColumnIndex()==7) {
                    	phone=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("phone", phone);
                    }
                    
                    /* Mã đơn vị */
                    if(cell.getColumnIndex()==8) {
                    	unitCode=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("unitCode", unitCode);
                    }
                    
                    /* Tên đơn vị (Phòng ban) */
                    if(cell.getColumnIndex()==9) {
                    	roomName=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("roomName", roomName);
                    }
                    
                    /* Tên đơn vị (Cơ quan)*/
                    if(cell.getColumnIndex()==10) {
                    	organizationName=dataFormatter.formatCellValue(cell, evaluator);
                    	errorRow.put("organizationName", organizationName);
                    }
                }
                	
				/* Xử lý username luôn có nếu bị trống */
                if(username==null && email!=null && email.contains("@")){
                	username=email.substring(0, email.indexOf("@"));
                }
                
                System.out.println("Row: "+username);
				/* Nếu user có username và có mã đơn vị */
				if(username!=null && unitCode!=null) {
	                try {
						/* Kiểm tra xem user đã có trong hệ thống chưa? */
	                	Optional<User> findUser=userService.findUserByUserName(username);
						/* Nếu đã tồn tại */
	                	if(findUser.isPresent()) {
	                		User userInfo=findUser.get();
	                		System.out.println("--> Người dùng ["+userInfo.getFullName()+"] đã tồn tại");
	                        
	                		Optional<Organization> findOrganization = organizationService.findOrganizationByUnitCode(unitCode);
	                        if(findOrganization.isPresent()) {
	                        	String organizationId=findOrganization.get().getId();
	                        	organizationService.addUsersToOrganization(organizationId, Arrays.asList(userInfo.getId()), AddFromSource.self);
	                        }else {
	                        	Document meta=new Document();
	        	                meta.put("unitCode", unitCode);
	        	                meta.put("roomName", roomName);
	        	                meta.put("organizationName", organizationName);
	        	                meta.put("importStatus", "fail");
	        	                
	        	                userInfo.setMeta(meta);
	        	                userInfo.setActive(false);
	        	                
	        	                userInfo=userService.saveUser(userInfo);
	        	                System.out.println(" .... ghi nhận import/update lỗi");
	                        }
	                    	userImportRaws.add(userInfo);
	                    	System.out.println("Luu da ton tai: "+username);
	                	}
						/* Nếu chưa có trong hệ thống */
	                	else {
	                		User userInfo=new User();
	                        userInfo.setUsername(username);
	                        userInfo.setEmail(email);
	                        userInfo.setActive(active);
	                        userInfo.setActiveCode(activeCode);
	                        userInfo.setFullName(fullName);
	                        userInfo.setJobTitle(jobTitle);
	                        userInfo.setPassword(passwordEncoder.encode(passwordDefault));
	                        userInfo.setPhone(phone);
	                        userInfo.setProvider(UserProvider.valueOf(userProvider));
	                        userInfo.setCreatorId(creator.getId());
	                        userInfo.setCreatorName(creator.getFullName());
	                        
	                        userInfo=userService.saveUser(userInfo);
	                        System.out.println("--> Người dùng ["+userInfo.getFullName()+"] import/update thành công");
	                        
	                        Optional<Organization> findOrganization = organizationService.findOrganizationByUnitCode(unitCode);
	                        if(findOrganization.isPresent()) {
	                        	String organizationId=findOrganization.get().getId();
	                        	organizationService.addUsersToOrganization(organizationId, Arrays.asList(userInfo.getId()), AddFromSource.self);
	                        }else {
	                        	Document meta=new Document();
	        	                meta.put("unitCode", unitCode);
	        	                meta.put("roomName", roomName);
	        	                meta.put("organizationName", organizationName);
	        	                meta.put("importStatus", "fail");
	        	                
	        	                userInfo.setMeta(meta);
	        	                userInfo.setActive(false);
	        	                
	        	                userInfo=userService.saveUser(userInfo);
	        	                System.out.println(" .... ghi nhận import/update lỗi");
	                        }
	                    	userImportRaws.add(userInfo);
	                    	System.out.println("Luu nguoi dung moi: "+username);
	                	}
	                	
					} catch (Exception e) {
						e.printStackTrace();
						errorRow.put("cause", e.getCause());
						errorRows.add(errorRow);
					}
				}else {
					errorRows.add(errorRow);
				}
            }
        }
        workbook.close();
        
        Document result=new Document();
        result.put("errorRows", errorRows);
        
		return result;
	}
	
	
	public List<Path> listFilesInDirectory(String directoryPath) throws IOException {
        // Tạo đối tượng Path từ đường dẫn thư mục
        Path path = Paths.get(directoryPath);
        
        // Lấy danh sách các file trong thư mục
        List<Path> files = Files.list(path)
                .filter(Files::isRegularFile) // Chỉ lấy file, không lấy thư mục con
                //.map(Path::getFileName) // Lấy tên file
                //.map(Path::toString) // Chuyển đổi thành chuỗi
                .collect(Collectors.toList());

        return files;
    }
}
