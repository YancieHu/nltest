package com.tidemedia.nntv.util;

import java.util.List;

public class ListUtil {

	// 判断是否为空
	public static boolean isEmpty(List list) {
		boolean bool = true;
		if (null != list && !list.isEmpty()) {
			bool = false;
		}
		return bool;
	}
	
	public static boolean isNotEmpty(List list){
		boolean bool = true;
		if(null == list || list.isEmpty()){
			bool = false;
		}
		return bool;
	}
}
