package com.start.es;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import sun.misc.BASE64Encoder;


/**
 * 
 * @author hsn
 * 
 *  实现DOC/PDF/TXT 导入到ES
 *
 */
@SuppressWarnings("restriction")
public class Es7TestAttachment {
	public static void main(String[] args) throws IOException {
		
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("127.0.0.1", 9200))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(5000) // 连接超时（默认为1秒）
								.setSocketTimeout(60000);// 套接字超时（默认为30秒）
					}
				})
				// .setMaxRetryTimeoutMillis(60000)//调整最大重试超时时间（默认为30秒）
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder
								.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build());// 线程数
					}
				});
		RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);

		// String base64Contentpath = pdfToBase641("d:\\TEST.pdf");
		String base64Contentpath = fileDocToBase64("E:\\新建 DOC 文档.doc");

		// base64Contentpath =
		// "e1xydGYxXGFuc2kNCkxvcmVtIGlwc3VtIGRvbG9yIHNpdCBhbWV0DQpccGFyIH0=";

		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("data", base64Contentpath);
		IndexRequest request = new IndexRequest("attachment_index").setPipeline("attachment") // 这里就是前面通过json创建的管道
				.id("9").source(jsonMap);
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

		client.close();
	}

	
	@SuppressWarnings("restriction")
	public static String UrlDocToBase64(String contentpath) throws IOException {
		InputStream is = null;
		String url = contentpath;
		URL url2 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
		conn.setDoInput(true);
		conn.connect();
		is = conn.getInputStream();

		byte[] data = null;
		
		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = is.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			data = swapStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new BASE64Encoder().encode(data).replace("\n", "").replace("\r", "");
	}

	@SuppressWarnings("restriction")
	public static String fileDocToBase64(String contentpath) throws IOException {
		InputStream is = null;
		String url = contentpath;
		File file = new File(contentpath);
		is = new FileInputStream(file);

		byte[] data = null;

		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = is.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			data = swapStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new BASE64Encoder().encode(data).replace("\n", "").replace("\r", "");
	}

}
