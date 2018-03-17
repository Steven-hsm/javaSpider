package com.jiangzh.launch;

import java.util.List;

import com.jiangzh.model.BookInfo;
import com.jiangzh.util.JsoupUtil;
import com.jiangzh.util.ProxyJsoupUtil;
import com.jiangzh.util.ExcelUtil;

/**
 * 启动类
 * @author jiangzh
 */
public class Main {
	public static void main(String[] args) {
		JsoupUtil ju = JsoupUtil.getInstance();  
		//ProxyJsoupUtil ju = ProxyJsoupUtil.getInstance();
		List<BookInfo> bookInfoLists= ju.getDoubanBookInfo();
		ExcelUtil.writeExcel(bookInfoLists);
	}
}
