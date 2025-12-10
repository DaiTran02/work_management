package ws.core.model.filter;

import lombok.Data;

@Data
public class SkipLimitFilter {
	private long skip=0;
	private int limit=0;
	
	public SkipLimitFilter() {

	}
	
	public SkipLimitFilter(long skip, int limit) {
		super();
		this.skip = skip;
		this.limit = limit;
	}
}
