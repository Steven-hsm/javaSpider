package com.jiangzh.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jiangzh.Constants.Constants;
import com.jiangzh.model.BookInfo;

/**
 * Jsoup解析工具
 * @author jiangzh
 */
public class JsoupUtil {  
	/** 单例模式创建 JsoupUtil对象*/
    private JsoupUtil() {  
    }  
    private static final JsoupUtil instance = new JsoupUtil();  
  
    public static JsoupUtil getInstance() {  
        return instance;  
    }  
    /**
     * 
     */
    public List<BookInfo> getDoubanBookInfo(){ 
    	List<BookInfo> bookInfoLists = new ArrayList<BookInfo>();
        try {  
        	int i=0;
            while(true) {
                String url = Constants.URL + Constants.START + String.valueOf(i * Constants.NUM)+"&type=S";  
                System.out.println(url);  
                Connection connection = Jsoup.connect(url);  
                Document document = connection.get();  
                Elements ul = document.select("div.info"); // 得到ul标签,标签中包含了一本书的所有信息
                if(ul.size() <= 0) {//没有书籍数据时，返回已经存在的数据
                	return bookInfoLists;
                }
                Iterator<Element> ulIter = ul.iterator();  
                BookInfo bookInfo = null;
                while (ulIter.hasNext()) { 
                	//获取元素
                    Element element = ulIter.next();
                    bookInfo = new BookInfo();
                    //书名
                    String bookName = element.select("h2 > a").attr("title");
                    bookInfo.setBookName(bookName);
                    //评价人数
                    String evaluPeopleStr = element.select(".pl").text();
                    int evaluPeople = Integer.parseInt(evaluPeopleStr.replaceAll("[^0-9]",""));
                    if(evaluPeople < 1000) {
                    	System.out.println(bookName+"评价人数太少,不做收录");
                    	continue;
                    }
                    bookInfo.setEvaluPeople(evaluPeople);
                    //评分
                    String score = element.select(".rating_nums").text();
                    bookInfo.setScore(Float.parseFloat(score));
                    String bookBaseInfo = element.select(".pub").text();
                    String a[] = bookBaseInfo.split("/");
                    if(a.length != 5) {
                    	System.out.println(bookName+"的基本信息有误,此条不收录");
                    	continue;
                    }
                    //作者
                    bookInfo.setAuthor(a[0]);
                    //出版社
                    bookInfo.setPublisher(a[2]);
                    //出版时间
                    bookInfo.setPublishDate(a[3]);
                    //价格
                    bookInfo.setPrice(a[4]);
                    bookInfoLists.add(bookInfo);
                    System.out.println(bookInfo.toString());
                    if(bookInfoLists.size() >= 40) {
                    	return bookInfoLists;
                    }
                }  
                i++;
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return bookInfoLists;
    }
      
}  