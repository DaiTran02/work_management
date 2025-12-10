package com.ngn.utils;

import com.ngn.api.doc.ApiDocService;
import com.ngn.api.doc.ApiFilterListDocModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.enums.DataOfEnum;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;

public class CountMenuUtil {
	private boolean isUser = SessionUtil.checkDataOf() == null ? true : SessionUtil.checkDataOf().getKey().equals(DataOfEnum.TOCANHAN.getKey());
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	@SuppressWarnings("unused")
	private CheckPermisstionUtil checkPermisstionUtil = new CheckPermisstionUtil();
	
	private int max = 0;
	private int min = 0;

	public CountMenuUtil(BelongOrganizationModel belongOrganizationModel,SignInOrgModel signInOrgModel) {
		this.belongOrganizationModel = belongOrganizationModel;

	}

	public int countDocAway() {
		int count = 0;
		try {
			ApiFilterListDocModel apiFilterListDocModel = new ApiFilterListDocModel();

			apiFilterListDocModel.setOrganizationId(belongOrganizationModel.getOrganizationId());


			apiFilterListDocModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
			apiFilterListDocModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
			apiFilterListDocModel.setLimit(max);
			apiFilterListDocModel.setSkip(min);
			apiFilterListDocModel.setActive(true);
			apiFilterListDocModel.setCategory("CVDi");
			
//			if(PropsUtil.isPermission()) {
//				if(checkPermisstionUtil.checkPermissionGroupManager()) {
//					if(checkPermisstionUtil.checkUserIsInGroupOrg()) {
//						apiFilterListDocModel.setOrganizationGroupId(signInOrgModel.getGroup().getId());
//					}
//				}else {
//					if(checkPermisstionUtil.checkUserIsInGroupOrg()) {
//						apiFilterListDocModel.setOrganizationGroupId(signInOrgModel.getGroup().getId());
//						apiFilterListDocModel.setOrganizationUserId(userAuthenticationModel.getId());
//					}
//				}
//			}

			ApiResultResponse<Object> responseCountDoc = ApiDocService.countDoc(apiFilterListDocModel);
			if(responseCountDoc.isSuccess()) {
				count += responseCountDoc.getTotal();
			}
		} catch (Exception e) {
			System.out.println("exception doc in CountMenuUtil");
			return 0;
		}
		
		return count;

	}
	
	public int countDocIncoming() {
		int count = 0;
		try {
			ApiFilterListDocModel apiFilterListDocModel = new ApiFilterListDocModel();


			apiFilterListDocModel.setOrganizationId(belongOrganizationModel.getOrganizationId());


			apiFilterListDocModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
			apiFilterListDocModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
			apiFilterListDocModel.setLimit(max);
			apiFilterListDocModel.setSkip(min);
			apiFilterListDocModel.setActive(true);
			apiFilterListDocModel.setCategory("CVDen");
			
//			if(PropsUtil.isPermission()) {
//				if(checkPermisstionUtil.checkPermissionGroupManager()) {
//					if(checkPermisstionUtil.checkUserIsInGroupOrg()) {
//						apiFilterListDocModel.setOrganizationGroupId(signInOrgModel.getGroup().getId());
//					}
//				}else {
//					if(checkPermisstionUtil.checkUserIsInGroupOrg()) {
//						apiFilterListDocModel.setOrganizationGroupId(signInOrgModel.getGroup().getId());
//						apiFilterListDocModel.setOrganizationUserId(userAuthenticationModel.getId());
//					}
//				}
//			}

			ApiResultResponse<Object> responseCountDoc = ApiDocService.countDoc(apiFilterListDocModel);
			if(responseCountDoc.isSuccess()) {
				count += responseCountDoc.getTotal();
			}
		} catch (Exception e) {
			System.out.println("exception doc in CountMenuUtil");
			return 0;
		}
		
		return count;

	}

	public int countTaskOwner() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setSkip(min);
		apiFilterTaskModel.setLimit(max);

		apiFilterTaskModel.setOwnerOrganizationId(belongOrganizationModel.getOrganizationId());
		
		if(isUser) {
			apiFilterTaskModel.setOwnerOrganizationId(null);
			apiFilterTaskModel.setOwnerOrganizationUserId(userAuthenticationModel.getId());
		}
		
		
		try {
			ApiResultResponse<Object> count = ApiTaskService.countTaskOwner(apiFilterTaskModel);
			if(count.isSuccess()) {
				return count.getTotal();
			}else {
				return 0;
			}
		} catch (Exception e) {
			System.out.println("Exception Task Owner in CountMenuUtil");
			return 0;
		}
	}

	public int countTaskAssignee() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setAssigneeOrganizationId(belongOrganizationModel.getOrganizationId());
		if(isUser) {
			apiFilterTaskModel.setAssigneeOrganizationId(null);
			apiFilterTaskModel.setAssigneeOrganizationUserId(userAuthenticationModel.getId());
		}

		try {
			ApiResultResponse<Object> count = ApiTaskService.countTaskAssignee(apiFilterTaskModel);
			if(count.isSuccess()) {
				return count.getTotal();
			}else {
				return 0;
			}
		} catch (Exception e) {
			System.out.println("Exception Task Assignee in CountMenuUtil");
			return 0;
		}
	}

	public int countTaskSupport() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setSkip(min);
		apiFilterTaskModel.setLimit(max);
		apiFilterTaskModel.setSupportOrganizationId(belongOrganizationModel.getOrganizationId());
		if(isUser) {
			apiFilterTaskModel.setSupportOrganizationId(null);
			apiFilterTaskModel.setSupportOrganizationUserId(userAuthenticationModel.getId());
		}
		try {
			ApiResultResponse<Object> count = ApiTaskService.countTaskSupport(apiFilterTaskModel);
			if(count.isSuccess()) {
				return count.getTotal();
			}else {
				return 0;
			}
		} catch (Exception e) {
			System.out.println("Exception TaskSP in CountMenuUtil");
			return 0;
		}
	}

	public int countTaskFollow() {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		apiFilterTaskModel.setSkip(min);
		apiFilterTaskModel.setLimit(max);
		apiFilterTaskModel.setFollowerOrganizationId(belongOrganizationModel.getOrganizationId());
		if(isUser) {
			apiFilterTaskModel.setFollowerOrganizationId(null);
			apiFilterTaskModel.setFollowerOrganizationUserId(userAuthenticationModel.getId());
		}
		
		try {
			ApiResultResponse<Object> count = ApiTaskService.countTaskFollower(apiFilterTaskModel);
			if(count.isSuccess()) {
				return count.getTotal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
