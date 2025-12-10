package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.TagType;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidListObjectId;
import ws.core.validation.ValidObjectId;

@Data
public class ReqTagRemoveClass {
	@NotBlank(message = "tagId không được trống")
	@ValidObjectId(message = "tagId không hợp lệ")
	private String tagId;
	
	@NotNull(message = "classIds không được trống")
	@ValidListObjectId(message = "classIds có phần tử không hợp lệ")
	private List<String> classIds=new ArrayList<>();
	
	@NotBlank(message = "type không được trống")
	@ValidEnum(message = "type không hợp lệ", enumClass = TagType.class)
	private String type;
	
	@Valid
	private ReqCreator creator;
}
