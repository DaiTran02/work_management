package ws.core.security;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ws.core.model.User;
import ws.core.services.UserService;

@Component
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserService userService;
	
	public CustomUserDetailsService() {
		super();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> findUser = userService.findUserByUserName(username);
		if (!findUser.isPresent()) { 
			throw new UsernameNotFoundException(username); 
		} 
		User user=findUser.get();
		List<String> permissions=userService.getAllPermission(user.getId());
		if(user.getUsername().equals("administrator")) {
			permissions.add("administrator");
		}
		System.out.println("List permission: "+permissions.size());
		permissions.stream().forEach(e->System.out.println("permission: "+e));
		
		CustomUserDetails customUserDetails=new CustomUserDetails();
		customUserDetails.setUser(user);
		customUserDetails.setRoles(permissions);
		return customUserDetails; 
	}

}
