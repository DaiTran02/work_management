package vn.com.ngn.utils;

import java.util.List;

public class SearchUtils {
	
	public<T> boolean binarySearch(List<? extends Comparable<? super T>> list,T key) {
		int left = 0;
		int right = list.size() -1;
		System.out.println("Check key: "+key);
		while(left <= right) {
			int mid = left + (right - left) /2;
			Comparable<? super T> midvalue = list.get(mid);
			int cmp = midvalue.compareTo(key);
			if(cmp == 0) {
				return true;
			} else if(cmp < 0) {
				left = mid + 1;
			} else {
				right = mid -1;
			}
		}
		return false;
	}
}
