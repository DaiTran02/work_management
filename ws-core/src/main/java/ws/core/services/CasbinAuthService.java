package ws.core.services;

import java.util.List;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CasbinAuthService {

	@Autowired
    private Enforcer enforcer;

	@Autowired
	private PropsService propsService;
	
	public boolean isEnable() {
		return propsService.isCasbinEnable();
	}
	 
	public void clearAll() {
		enforcer.clearPolicy();
		enforcer.savePolicy();
	}
	
    // Kiểm tra quyền truy cập của người dùng
    public boolean isAllowed(String username, String api, String action) {
        // 'sub' là subject (người dùng), 'obj' là object (API), 'act' là hành động (GET, POST, ...)
        return enforcer.enforce(username, api, action);
    }
    
    // Thêm policy (quyền cho người dùng)
    public boolean addPolicy(String sub, String obj, String act) {
    	if (!hasPolicy(sub, obj, act)) {  // Kiểm tra nếu policy chưa tồn tại
            return enforcer.addPolicy(sub, obj, act);  // Thêm policy nếu chưa tồn tại
        }
        return false;  // Nếu policy đã tồn tại, không thêm nữa
    }

    // Xóa policy
    public boolean removePolicy(String sub, String obj, String act) {
        return enforcer.removePolicy(sub, obj, act);
    }

    // Cập nhật policy (sửa policy)
    public boolean updatePolicy(String oldSub, String oldObj, String oldAct, String newSub, String newObj, String newAct) {
        // Xóa policy cũ
        removePolicy(oldSub, oldObj, oldAct);
        // Thêm policy mới
        return addPolicy(newSub, newObj, newAct);
    }

    // Lấy tất cả các policy
    public List<List<String>> getAllPolicies() {
        return enforcer.getPolicy();
    }

    // Thêm vai trò cho người dùng
    public boolean addRoleForUser(String user, String role) {
    	if(!hasRole(user, role)) { // Kiểm tra role nếu chưa tồn tại
    		return enforcer.addGroupingPolicy(user, role); // Thêm người dùng vào vài trò
    	}
    	return false; // Không thêm mới nếu đã tồn tại
    }

    // Xóa vai trò khỏi người dùng
    public boolean removeRoleForUser(String user, String role) {
        return enforcer.removeGroupingPolicy(user, role);
    }

    // Lấy tất cả các vai trò của một người dùng
    public List<String> getRolesForUser(String user) {
        return enforcer.getRolesForUser(user);
    }

    // Kiểm tra xem người dùng có quyền truy cập hay không
    public boolean checkPermission(String user, String obj, String act) {
        return enforcer.enforce(user, obj, act);
    }

    // Lấy tất cả các vai trò
    public List<List<String>> getAllRoles() {
        return enforcer.getGroupingPolicy();
    }
    
    // Kiểm tra xem một policy có tồn tại không
    public boolean hasPolicy(String sub, String obj, String act) {
        return enforcer.hasPolicy(sub, obj, act);
    }

    // Kiểm tra xem một vai trò có tồn tại không
    public boolean hasRole(String user, String role) {
        return enforcer.hasGroupingPolicy(user, role);
    }

    // Kiểm tra xem một policy có tồn tại trong tất cả các policy không
    public boolean hasPolicy(List<String> policy) {
        return enforcer.getPolicy().contains(policy);
    }

    // Kiểm tra xem vai trò có tồn tại trong tất cả các vai trò không
    public boolean hasRole(List<String> role) {
        return enforcer.getGroupingPolicy().contains(role);
    }
}
