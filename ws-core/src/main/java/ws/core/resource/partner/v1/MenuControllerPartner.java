package ws.core.resource.partner.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.services.OrganizationService;
import ws.core.services.UserService;

@RestController
@RequestMapping("/api/partner/v1")
public class MenuControllerPartner {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/menu")
	public Object getMenu(
			@RequestParam(name = "username", required = true) String username,
			@RequestParam(name = "year", required = false, defaultValue = "0") int year
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		User user= userService.getUserByUserName(username);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setIncludeUserId(user.getId());
		
		List<Document> menuEachOrgnizations=new ArrayList<>();
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		for(Organization organization:organizations) {
			menuEachOrgnizations.add(buildMenu(user, organization));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(menuEachOrgnizations);
		return responseAPI.build();
	}
	
	private String generalLink(String nav, int year, User user) {
		return "https://hanoi-tdnv-site.ngn.vn/login?nav="+nav+"&year="+year;
	}
	
	private Document buildMenu(User user, Organization organization) {
		
		/* Tổng quan */
		Document menuTongQuan=new Document();
		menuTongQuan.put("key", "tong-quan");
		menuTongQuan.put("name", "Tổng quan");
		menuTongQuan.put("link", null);
		menuTongQuan.put("count", 0);
		menuTongQuan.put("displayCount", false);
		menuTongQuan.put("hasSubMenu", true);
		
		Document subMenuTongQuat=new Document();
		subMenuTongQuat.put("key", "tong-quat");
		subMenuTongQuat.put("name", "Tổng quat");
		subMenuTongQuat.put("link", generalLink("tong-quat", 2024, user));
		subMenuTongQuat.put("count", 0);
		subMenuTongQuat.put("displayCount", false);
		subMenuTongQuat.put("hasSubMenu", false);
		subMenuTongQuat.put("subMenu", Arrays.asList());
		
		Document subMenuBaoCao=new Document();
		subMenuBaoCao.put("key", "bao-cao");
		subMenuBaoCao.put("name", "Báo cáo");
		subMenuBaoCao.put("link", generalLink("bao-cao", 2024, user));
		subMenuBaoCao.put("count", 0);
		subMenuBaoCao.put("displayCount", false);
		subMenuBaoCao.put("hasSubMenu", false);
		subMenuBaoCao.put("subMenu", Arrays.asList());
		
		List<Document> listSubMenuTongQuan=new ArrayList<>();
		listSubMenuTongQuan.add(subMenuTongQuat);
		listSubMenuTongQuan.add(subMenuBaoCao);
		menuTongQuan.put("subMenu", listSubMenuTongQuan);
		
		
		/* Văn bản */
		Document menuVanBan=new Document();
		menuVanBan.put("key", "van-ban");
		menuVanBan.put("name", "Văn bản");
		menuVanBan.put("link", null);
		menuVanBan.put("count", 0);
		menuVanBan.put("displayCount", false);
		menuVanBan.put("hasSubMenu", true);
		
		Document subMenuVanBanDi=new Document();
		subMenuVanBanDi.put("key", "van-ban-di");
		subMenuVanBanDi.put("name", "Văn bản đi");
		subMenuVanBanDi.put("link", generalLink("van-ban-di", 2024, user));
		subMenuVanBanDi.put("count", 11);
		subMenuVanBanDi.put("displayCount", true);
		subMenuVanBanDi.put("hasSubMenu", false);
		subMenuVanBanDi.put("subMenu", Arrays.asList());
		
		Document subMenuVanBanDen=new Document();
		subMenuVanBanDen.put("key", "van-ban-den");
		subMenuVanBanDen.put("name", "Văn bản đến");
		subMenuVanBanDen.put("link", generalLink("van-ban-den", 2024, user));
		subMenuVanBanDen.put("count", 21);
		subMenuVanBanDen.put("displayCount", true);
		subMenuVanBanDen.put("hasSubMenu", false);
		subMenuVanBanDen.put("subMenu", Arrays.asList());
		
		List<Document> listSubMenuVanBan=new ArrayList<>();
		listSubMenuVanBan.add(subMenuVanBanDi);
		listSubMenuVanBan.add(subMenuVanBanDen);
		menuVanBan.put("subMenu", listSubMenuVanBan);
		
		
		/* Theo dõi nhiệm vụ */
		Document menuTheoDoiNhiemVu=new Document();
		menuTheoDoiNhiemVu.put("key", "theo-doi-nhiem-vu");
		menuTheoDoiNhiemVu.put("name", "Theo dõi nhiệm vụ");
		menuTheoDoiNhiemVu.put("link", null);
		menuTheoDoiNhiemVu.put("count", 0);
		menuTheoDoiNhiemVu.put("displayCount", false);
		menuTheoDoiNhiemVu.put("hasSubMenu", true);
		
		Document subMenuNhiemVuDaGiao=new Document();
		subMenuNhiemVuDaGiao.put("key", "da-giao");
		subMenuNhiemVuDaGiao.put("name", "Đã giao");
		subMenuNhiemVuDaGiao.put("link", generalLink("nhiem-vu-da-giao", 2024, user));
		subMenuNhiemVuDaGiao.put("count", 213);
		subMenuNhiemVuDaGiao.put("displayCount", true);
		subMenuNhiemVuDaGiao.put("hasSubMenu", false);
		subMenuNhiemVuDaGiao.put("subMenu", Arrays.asList());
		
		Document subMenuNhiemVuDuocGiao=new Document();
		subMenuNhiemVuDuocGiao.put("key", "duoc-giao");
		subMenuNhiemVuDuocGiao.put("name", "Được giao");
		subMenuNhiemVuDuocGiao.put("link", generalLink("duoc-giao", 2024, user));
		subMenuNhiemVuDuocGiao.put("count", 213);
		subMenuNhiemVuDuocGiao.put("displayCount", true);
		subMenuNhiemVuDuocGiao.put("hasSubMenu", false);
		subMenuNhiemVuDuocGiao.put("subMenu", Arrays.asList());
		
		Document subMenuNhiemVuPhoiHop=new Document();
		subMenuNhiemVuPhoiHop.put("key", "phoi-hop");
		subMenuNhiemVuPhoiHop.put("name", "Phối hợp");
		subMenuNhiemVuPhoiHop.put("link", generalLink("phoi-hop", 2024, user));
		subMenuNhiemVuPhoiHop.put("count", 213);
		subMenuNhiemVuPhoiHop.put("displayCount", true);
		subMenuNhiemVuPhoiHop.put("hasSubMenu", false);
		subMenuNhiemVuPhoiHop.put("subMenu", Arrays.asList());
		
		Document subMenuNhiemVuTheoDoi=new Document();
		subMenuNhiemVuTheoDoi.put("key", "theo-doi");
		subMenuNhiemVuTheoDoi.put("name", "Theo-doi");
		subMenuNhiemVuTheoDoi.put("link", generalLink("theo-doi", 2024, user));
		subMenuNhiemVuTheoDoi.put("count", 213);
		subMenuNhiemVuTheoDoi.put("displayCount", true);
		subMenuNhiemVuTheoDoi.put("hasSubMenu", false);
		subMenuNhiemVuTheoDoi.put("subMenu", Arrays.asList());
		
		List<Document> listSubMenuTheoDoiNhiemVu=new ArrayList<>();
		listSubMenuTheoDoiNhiemVu.add(subMenuNhiemVuDaGiao);
		listSubMenuTheoDoiNhiemVu.add(subMenuNhiemVuDuocGiao);
		listSubMenuTheoDoiNhiemVu.add(subMenuNhiemVuPhoiHop);
		listSubMenuTheoDoiNhiemVu.add(subMenuNhiemVuTheoDoi);
		menuTheoDoiNhiemVu.put("subMenu", listSubMenuTheoDoiNhiemVu);
		
		List<Document> menus=new ArrayList<>();
		menus.add(menuTongQuan);
		menus.add(menuVanBan);
		menus.add(menuTheoDoiNhiemVu);
		
		Document menu=new Document();
		menu.put("organizationId", organization.getId());
		menu.put("organizationName", organization.getName());
		menu.put("roles", organization.getListRoleOrganizationExpandsOfUser(user.getId()).stream().map(e->e.getName()).collect(Collectors.toList()));
		menu.put("menus", menus);
		return menu;
	}
}
