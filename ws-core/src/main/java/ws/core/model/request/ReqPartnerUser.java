package ws.core.model.request;

import java.util.List;

import lombok.Data;

@Data
public class ReqPartnerUser {
    private int pageIndex;
    private int pageSize;
    private int totalItems;
    private List<ReqPartnerItemUser> items;
}
