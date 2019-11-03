package com.start.es;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.search.join.ScoreMode;
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.ScrollableHitSource.Hit;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.es.entity.Article;
import com.start.es.entity.Comments;
import com.start.es.service.EsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.MultiSearchTemplateRequest;
import org.elasticsearch.script.mustache.MultiSearchTemplateResponse;
import org.elasticsearch.script.mustache.MultiSearchTemplateResponse.Item;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESTest2 {

	@Autowired
	EsService esService;

	RestHighLevelClient client;

	@Before
	public void testBefore() throws UnknownHostException {

		this.client = esService.getClient();
	}

	@Test
	public void testSearch() throws IOException {

		Article article = new Article();
		Comments comments = new Comments();
		comments.setAge(18);
		comments.setComment("so good !!!");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date date =new Date();
		comments.setDate(new Date());
		comments.setStars(3);
		comments.setName("zhang");
		List<Comments> l = new ArrayList<Comments>();
		l.add(comments);
		article.setComments(l);
		article.setAuthor("tom");
		article.setKeyword(UUID.randomUUID().toString());
		article.setTitle("love me" + UUID.randomUUID().toString());

		String jsonData = new ObjectMapper().writeValueAsString(article);

		IndexRequest indexRequest = new IndexRequest("article_index").source(jsonData, XContentType.JSON);

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		String index = indexResponse.getIndex();
		String id = indexResponse.getId();
		long version = indexResponse.getVersion();
		System.out.println("---------------------------------" + index);
		System.out.println(id);
		System.out.println(version);
	}

	@Test
	public void update() throws IOException {

		Map<String, Object> jsonMap = new HashMap<>();
		Map<String, Object> jsonMap1 = new HashMap<>();
		jsonMap1.put("date", new Date());
		jsonMap1.put("starts", "1");
		jsonMap1.put("name", "cccccc");
		jsonMap1.put("comment", "cccccc");
		jsonMap1.put("age", "20");

		jsonMap.put("comments", jsonMap1);
		UpdateRequest request = new UpdateRequest("article_index", "1").doc(jsonMap);

		UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
	}

	@Test
	public void addNestedObj() throws IOException {

		UpdateRequest request = new UpdateRequest("article_index", "2");

		Map<String, Object> jsonMap1 = new HashMap<>();
		jsonMap1.put("date", new Date());
		jsonMap1.put("starts", "1");
		jsonMap1.put("name", "cccccc");
		jsonMap1.put("comment", "cccccc");
		jsonMap1.put("age", "20");
		// parameters.put("new", jsonMap1);
		Map<String, Object> parameters = Collections.singletonMap("jsonMap1", jsonMap1);

		Script inline = new Script(ScriptType.INLINE, "painless", "ctx._source.comments.add(params.jsonMap1)",
				parameters);
		request.script(inline); // comments.add(params.new_tag)

		UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
	}

	/**
	 * TemplateSearch
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTemplateSearch() throws IOException {
		Request scriptRequest = new Request("POST", "_scripts/title_search");
		scriptRequest.setJsonEntity("{" + "  \"script\": {" + "    \"lang\": \"mustache\"," + "    \"source\": {"
				+ "      \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } },"
				+ "      \"size\" : \"{{size}}\"" + "    }" + "  }" + "}");
		// 只支持老的API
		// Response scriptResponse = restClient.performRequest(scriptRequest);
	}

	/**
	 * MultiSearchTemplateRequest
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMultiSearch() throws IOException {
		String[] searchTerms = { "tom", "xxxx", "huang" };

		MultiSearchTemplateRequest multiRequest = new MultiSearchTemplateRequest();
		for (String searchTerm : searchTerms) {
			SearchTemplateRequest request = new SearchTemplateRequest();
			request.setRequest(new SearchRequest("article_index"));

			request.setScriptType(ScriptType.INLINE);
			request.setScript("{" + "  \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } },"
					+ "  \"size\" : \"{{size}}\"" + "}");

			Map<String, Object> scriptParams = new HashMap<>();
			scriptParams.put("field", "author");
			scriptParams.put("value", searchTerm);
			scriptParams.put("size", 5);
			request.setScriptParams(scriptParams);

			multiRequest.add(request);
		}

		MultiSearchTemplateResponse multiResponse = client.msearchTemplate(multiRequest, RequestOptions.DEFAULT);

		for (Item item : multiResponse.getResponses()) {
			if (item.isFailure()) {
				String error = item.getFailureMessage();
			} else {
				SearchTemplateResponse searchTemplateResponse = item.getResponse();
				SearchResponse searchResponse = searchTemplateResponse.getResponse();
				SearchHits hits = searchResponse.getHits();
				for (SearchHit hit : searchResponse.getHits()) {
					System.out.println("---SearchHit---" + hit.getSourceAsString());
				}
			}
		}
	}

	@Test
	public void testRangSearch() throws IOException {

		QueryBuilder qb = QueryBuilders.matchQuery("user", "tom1");
		QueryBuilder qb1 = QueryBuilders.termQuery("date", "2018-07-07");


		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").gte(30);
		QueryBuilder s = QueryBuilders.boolQuery().should(qb).must(qb1).must(rangeQueryBuilder);// .must(qb5);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(s);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(5);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		// 排序
		searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
		searchSourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));

		// 高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("date");
		highlightTitle.highlighterType("unified");
		highlightBuilder.field(highlightTitle);
		HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
		highlightBuilder.field(highlightUser);
		searchSourceBuilder.highlighter(highlightBuilder);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(searchSourceBuilder);
		searchRequest.indices("ckeck_index");

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		SearchHits hits = searchResponse.getHits();

		for (SearchHit searchHit : hits) {
			System.out.println("---testRangSearch SearchHit---" + searchHit.getSourceAsString());
		}

	}
	
	/**
	 * join的父子关系的插入
	 * 重点 是 routing("1") 和 json 文件中需要指明 parent 关系
	 * @throws IOException
	 */
	@Test
	public void testParentSonInsert() throws IOException {
		//QueryBuilder qb1 = QueryBuilders.termQuery("date", "2018-07-07");
		
		String joinString ="{\r\n" + 
				"  \"text\": \"This is an answer 20\",\r\n" + 
				"  \"relation_field\": {\r\n" + 
				"    \"name\": \"answer\", \r\n" + 
				"    \"parent\": \"1\" \r\n" + 
				"    \r\n" + 
				"  },\r\n" + 
				"  \"info\":\"This is an answer vcd\",\r\n" + 
				"  \"age\":41,\r\n" + 
				"  \"date\":\"2019-12-01\"\r\n" + 
				"}";
		
		IndexRequest indexRequest = new IndexRequest("my_index1").source(joinString, XContentType.JSON);
		indexRequest.routing("1");

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		String index = indexResponse.getIndex();
		String id = indexResponse.getId();
		long version = indexResponse.getVersion();
		System.out.println("-------------my_index1--------------------" + index);
		System.out.println(id);
		System.out.println(version);
	}
	
	@Test
	public void testParentSonSearch() throws IOException {

		QueryBuilder termQuery = QueryBuilders.termQuery("answer", "1");
		
		//查询父级数据--包含answer子的父  ，加上innerHit(new InnerHitBuilder())可以获取子对象的数据
		HasChildQueryBuilder jb = JoinQueryBuilders.hasChildQuery("answer",QueryBuilders.matchAllQuery(),                        
		        ScoreMode.None).innerHit(new InnerHitBuilder());
		//HasChildQueryBuilder jb = JoinQueryBuilders.hasChildQuery("answer",QueryBuilders.matchAllQuery(),                        
        //ScoreMode.None);
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(jb);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(5);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(searchSourceBuilder);
		searchRequest.indices("my_index1");

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		SearchHits hits = searchResponse.getHits();

		for (SearchHit searchHit : hits) {
			System.out.println("---testParentSonSearch SearchHit---" + searchHit.getSourceAsString());
			//这里可以获取子对象的数据
			searchHit.getInnerHits().get("answer");
			searchHit.getId();
		}
		
		
		//查询子级数据-- 父为question同时id=1
		QueryBuilder q = QueryBuilders.idsQuery().addIds("1");
		
		//HasParentQueryBuilder jb1 = JoinQueryBuilders.hasParentQuery("question",QueryBuilders.matchAllQuery(),false); 
		HasParentQueryBuilder jb1 = JoinQueryBuilders.hasParentQuery("question",q,false);		
		searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(jb1);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(5);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		searchRequest = new SearchRequest();
		searchRequest.source(searchSourceBuilder);
		searchRequest.indices("my_index1");

		searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		hits = searchResponse.getHits();

		for (SearchHit searchHit : hits) {
			System.out.println("---SonSearch SearchHit---" + searchHit.getSourceAsString());
		}
		
		
	}
	
	

}
