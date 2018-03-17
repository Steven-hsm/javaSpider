package com.jiangzh.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jiangzh.Constants.Constants;
import com.jiangzh.model.BookInfo;

public class ProxyJsoupUtil {
	/** 无代理ip测试 此项没有做测试 */
	private String ip = "117.90.7.83";
	private int port = 	9000;
	/** 单例模式创建 JsoupUtil对象*/
    private ProxyJsoupUtil() {  
    }  
    private static final ProxyJsoupUtil instance = new ProxyJsoupUtil();  
  
    public static ProxyJsoupUtil getInstance() {  
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
            	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                String href = Constants.URL + Constants.START + String.valueOf(i * Constants.NUM)+"&type=S";  
                URL url = new URL(href); 
                HttpsURLConnection urlcon = (HttpsURLConnection)url.openConnection(proxy);  
                urlcon.connect();         //获取连接  
                InputStream is = urlcon.getInputStream();  
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                StringBuffer content = new StringBuffer();  
                String line = null;  
                while((line=buffer.readLine())!=null){  
                	content.append(line);  
                }  
                Document document = Jsoup.parse(content.toString()); 
                Elements ul = document.select("div.info"); // 得到ul标签,标签中包含了一本书的所有信息
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
                    if(evaluPeople < 100) {
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
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return bookInfoLists;
    }
}
