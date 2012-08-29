package com.c_platform.parsebody.parsebody.filter;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.c_platform.parsebody.parsebody.WebContext;

public class HttpClientFilter {

	public static WebContext getHtmlSource(WebContext ctx) {
		String resource = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(ctx.getUrl());
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				Header header = entity.getContentEncoding();
				if (header != null) {
					System.out.println(header.getName());
					System.out.println(header.getValue());
				}
				resource = EntityUtils.toString(entity, ctx.getCharset());
				ctx.setContent(resource);
			} else {
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return ctx;
	}
}
