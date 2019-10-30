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
import java.util.UUID;

//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ES {

	@Autowired
	EsService esService;

	RestHighLevelClient client;

	@Before
	public void testBefore() throws UnknownHostException {

		this.client = esService.getClient();
	}

	
	  @Test public void testSearch() throws IOException {
	  
	  Article article = new Article(); 
	  Comments comments = new Comments();
	  comments.setAge(18); 
	  comments.setComment("bbbbbbbbbbbbbbb"); 
	  SimpleDateFormat	  df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date date =new Date();
	  comments.setDate(new Date()); 
	  comments.setStars(3);
	  comments.setName("zhang"); 
	  List<Comments> l = new ArrayList<Comments>();
	  l.add(comments); 
	  article.setComments(l); 
	  article.setAuthor("tom li1");
	  article.setKeyword("hk kill"); 
	  article.setTitle("love me" +
	  UUID.randomUUID().toString());
	  
	  String jsonData = new ObjectMapper().writeValueAsString(article);
	  
	  IndexRequest indexRequest = new
	  IndexRequest("article_index").source(jsonData, XContentType.JSON);
	  
	  IndexResponse indexResponse = client.index(indexRequest,
	  RequestOptions.DEFAULT); 
	  String index = indexResponse.getIndex(); 
	  String id = indexResponse.getId(); long version = indexResponse.getVersion();
	  System.out.println("---------------------------------" + index);
	  System.out.println(id); System.out.println(version); 
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
	   UpdateRequest request = new UpdateRequest("article_index", "1")
	           .doc(jsonMap); 
	   
	   UpdateResponse updateResponse = client.update(
		        request, RequestOptions.DEFAULT);
   }
   
   @Test
   public void addNestedObj() throws IOException {
	   
	   UpdateRequest request = new UpdateRequest( "article_index",  "2");   
	
	   Map<String, Object> jsonMap1 = new HashMap<>();
	   jsonMap1.put("date", new Date());
	   jsonMap1.put("starts", "1");
	   jsonMap1.put("name", "cccccc");
	   jsonMap1.put("comment", "cccccc");
	   jsonMap1.put("age", "20");
	   //parameters.put("new", jsonMap1);
	   Map<String, Object> parameters = Collections.singletonMap("jsonMap1",jsonMap1);

	   Script inline = new Script(ScriptType.INLINE, "painless",
	           "ctx._source.comments.add(params.jsonMap1)", parameters);  
	   request.script(inline); 	   //comments.add(params.new_tag)	   
   
	   UpdateResponse updateResponse = client.update(
		        request, RequestOptions.DEFAULT);
   }
	 

	@Test
	public void testCreateIndex() throws IOException {

		CreateIndexRequest request = new CreateIndexRequest("twitter_xx");
		request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));

		String json = "{\r\n" + 
				"      \"properties\" : {\r\n" + 
				"        \"author\" : {\r\n" + 
				"          \"type\" : \"text\",\r\n" + 
				"          \"fields\" : {\r\n" + 
				"            \"keyword\" : {\r\n" + 
				"              \"type\" : \"keyword\",\r\n" + 
				"              \"ignore_above\" : 256\r\n" + 
				"            }\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        \"comments\" : {\r\n" + 
				"          \"type\": \"nested\", \r\n" + 
				"          \"properties\" : {\r\n" + 
				"            \"age\" : {\r\n" + 
				"              \"type\" : \"long\"\r\n" + 
				"            },\r\n" + 
				"            \"comment\" : {\r\n" + 
				"              \"type\" : \"text\",\r\n" + 
				"              \"fields\" : {\r\n" + 
				"                \"keyword\" : {\r\n" + 
				"                  \"type\" : \"keyword\",\r\n" + 
				"                  \"ignore_above\" : 256\r\n" + 
				"                }\r\n" + 
				"              }\r\n" + 
				"            },\r\n" + 
				"            \"date\" : {\r\n" + 
				"              \"type\" : \"long\"\r\n" + 
				"            },\r\n" + 
				"            \"name\" : {\r\n" + 
				"              \"type\" : \"text\",\r\n" + 
				"              \"fields\" : {\r\n" + 
				"                \"keyword\" : {\r\n" + 
				"                  \"type\" : \"keyword\",\r\n" + 
				"                  \"ignore_above\" : 256\r\n" + 
				"                }\r\n" + 
				"              }\r\n" + 
				"            },\r\n" + 
				"            \"stars\" : {\r\n" + 
				"              \"type\" : \"long\"\r\n" + 
				"            }\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        \"keyword\" : {\r\n" + 
				"          \"type\" : \"text\",\r\n" + 
				"          \"fields\" : {\r\n" + 
				"            \"keyword\" : {\r\n" + 
				"              \"type\" : \"keyword\",\r\n" + 
				"              \"ignore_above\" : 256\r\n" + 
				"            }\r\n" + 
				"          }\r\n" + 
				"        },\r\n" + 
				"        \"title\" : {\r\n" + 
				"          \"type\" : \"text\",\r\n" + 
				"          \"fields\" : {\r\n" + 
				"            \"keyword\" : {\r\n" + 
				"              \"type\" : \"keyword\",\r\n" + 
				"              \"ignore_above\" : 256\r\n" + 
				"            }\r\n" + 
				"          }\r\n" + 
				"        }\r\n" + 
				"      }\r\n" + 
				"    }";

		request.mapping(json, XContentType.JSON);
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

		System.out.println("---------------createIndexResponse------------------" + createIndexResponse.toString());
	}

}
