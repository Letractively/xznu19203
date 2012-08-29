package com.c_platform.parsebody.parsebody;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.c_platform.parsebody.parsebody.filter.BlockFilter;
import com.c_platform.parsebody.parsebody.filter.HtmlDocumentFilter;
import com.c_platform.parsebody.parsebody.filter.HtmlElementFilter;
import com.c_platform.parsebody.parsebody.filter.HttpClientFilter;
import com.c_platform.parsebody.parsebody.filter.ReadFilter;
import com.c_platform.parsebody.parsebody.filter.SequenceFilter;
import com.c_platform.parsebody.parsebody.filter.WrapFilter;

public class ParseBodyTest {

//	String url = "http://news.163.com/12/0829/11/8A2RB9FT0001124J.html";
	String url = "http://finance.sina.com.cn/stock/yjdt/20120829/135212988867.shtml";
	String charset = "gb2312";
	String[] screen = new String[] { "480", "320" };
	boolean isIframe = false;
	boolean isRss = true;

	@Test
	public void createTest() {
		WebContext ctx = new WebContext();
		ctx.setUrl(url);
		ctx.setCharset(charset);
		ctx.setScreen(screen);
		ctx.setIframe(isIframe);
		ctx.setRss(isRss);

		HttpClientFilter.getHtmlSource(ctx);
		HtmlDocumentFilter.htmlDocumentParse(ctx);
		try {
			BlockFilter.block(ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SequenceFilter.sequence(ctx);
		ReadFilter.read(ctx);
		WrapFilter.wrap(ctx);
		try {
			HtmlElementFilter.tagFilter(ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> results = (ArrayList<String>) ctx.getContent();
		for (int i = 0; i < results.size(); i++) {
			FileWriter fw;
			try {
				fw = new FileWriter("D://test/test_" + i + ".html");
				fw.write(results.get(i), 0, results.get(i).length());
				fw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
