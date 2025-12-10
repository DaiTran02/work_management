package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ws.core.enums.TagType;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidListObjectId;

@Data
public class ReqTagCreate {
	@NotBlank(message = "type không được trống")
	@ValidEnum(message = "type không hợp lệ", enumClass = TagType.class)
	private String type;
	
	@NotBlank(message = "name không được trống")
	private String name;
	
	@NotBlank(message = "color không được trống")
	private String color;
	
	@ValidListObjectId(message = "classIds có phần tử không hợp lệ")
	private List<String> classIds=new ArrayList<>();
	
	@Valid
	private ReqCreator creator;
}
