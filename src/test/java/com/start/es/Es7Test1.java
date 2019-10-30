package com.start.es;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.es.entity.Article;
import com.start.es.entity.Comments;

public class Es7Test1 {
	public static void main(String[] args) throws IOException {
		///////// Client
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

		Article article = new Article();
		Comments comments = new Comments();
		comments.setAge(18);
		comments.setComment("bbbbbbbbbbbbbbb");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date date =new Date();
		comments.setDate(new Date());
		comments.setStars(3);
		comments.setName("zhang");
		List<Comments> l = new ArrayList<Comments>();
		l.add(comments);
		article.setComments(l);
		article.setAuthor("tom li1");
		article.setKeyword("hk kill");
		article.setTitle("love me");

		String jsonData = new ObjectMapper().writeValueAsString(article);

		IndexRequest indexRequest = new IndexRequest("article_index").id("12").source(jsonData, XContentType.JSON);
		
		/**
		 * 字符串形式
		 */
		IndexRequest request = new IndexRequest(""); //索引
		request.id("1"); //文档id
		String jsonString = "{" +
		        "\"user\":\"kimchy\"," +
		        "\"postDate\":\"2013-01-30\"," +
		        "\"message\":\"trying out Elasticsearch\"" +
		        "}";
		request.source(jsonString, XContentType.JSON);
		
		/**
		 * 以Map形式提供的文档源，可自动转换为JSON格式
		 */
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("user", "kimchy");
		jsonMap.put("postDate", new Date());
		jsonMap.put("message", "trying out Elasticsearch");
		IndexRequest indexRequest1 = new IndexRequest("posts")
		    .id("1").source(jsonMap); 
		
		/**
		 * 使用XConttentBuilder构建内容
		 */
		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		{
		    builder.field("user", "kimchy");
		    builder.timeField("postDate", new Date());
		    builder.field("message", "trying out Elasticsearch");
		}
		builder.endObject();
		IndexRequest indexRequest2 = new IndexRequest("posts")
		    .id("1").source(builder);
		
		/**
		 * 直接用键值对对象构架数据
		 */
		IndexRequest indexRequest3 = new IndexRequest("posts")
			    .id("1")
			    .source("user", "kimchy",
			        "postDate", new Date(),
			        "message", "trying out Elasticsearch");
		
		
		//以下是官方文档提供的可选参数。
		request.routing("routing"); //路由值
		request.timeout(TimeValue.timeValueSeconds(1)); //设置超时
		request.timeout("1s"); ////以字符串形式设置超时时间
		request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); //以WriteRequest.RefreshPolicy实例形式设置刷新策略
		request.setRefreshPolicy("wait_for");//以字符串形式刷新策略                     
		request.version(2); //文档版本
		request.versionType(VersionType.EXTERNAL); //文档类型
		request.opType(DocWriteRequest.OpType.CREATE); //操作类型
		request.opType("create"); //操作类型 可选create或update
		request.setPipeline("pipeline"); //索引文档之前要执行的摄取管道的名称
		
		
		
		
		

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		String index = indexResponse.getIndex();
		String id = indexResponse.getId();
		long version = indexResponse.getVersion();
		System.out.println(index);
		System.out.println(id);
		System.out.println(version);
		
		
		
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchSourceBuilder.query(QueryBuilders.termQuery("author", "xxxx")); 
		searchSourceBuilder.from(0);//设置from确定结果索引的选项以开始搜索。默认为0
		searchSourceBuilder.size(10);//设置size确定要返回的搜索匹配数的选项。默认为10 
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
		SearchRequest searchRequest = new SearchRequest(); 
		searchRequest.indices("article_index");
		searchRequest.source(searchSourceBuilder); 
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();
		TotalHits totalHits = hits.getTotalHits();
		long numHits = totalHits.value;
		TotalHits.Relation relation = totalHits.relation;
		System.out.println("numHits:"+numHits);
		
		
		//QueryBuilder query.;
		//Object queryBuilders = QueryBuilders.nestedQuery("comments", query., scoreMode);
		
		//ScoreMode.Total s = new ScoreMode() ;
		SearchRequest requestSearch = new SearchRequest("article_index")
			    .source(new SearchSourceBuilder().query(QueryBuilders.boolQuery()
			        .should(QueryBuilders.matchQuery("author", "xxxx"))
			        .should(QueryBuilders.matchQuery("title", "Nest coff"))
			        .should(QueryBuilders.nestedQuery("comments", QueryBuilders.matchQuery("age", "31"),ScoreMode.Total))));
		
		searchResponse = client.search(requestSearch, RequestOptions.DEFAULT);
		hits = searchResponse.getHits();
		totalHits = hits.getTotalHits();
		numHits = totalHits.value;
		relation = totalHits.relation;
		System.out.println("numHits----:"+numHits);
		SearchHit [] searchHits = hits.getHits();
		for(SearchHit hit:searchHits){
		    //使用SearchHit做一些事情
			index = hit.getIndex();
			id = hit.getId();
			float score = hit.getScore();
			System.out.println(hit.getSourceAsString());
		}
		

		
		//////////////////////////////////////////////// 关闭连接
		client.close();
	}
}