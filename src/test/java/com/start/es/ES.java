package com.start.es;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
      
		this.client=esService.getClient();
    }
	
	@Test
    public void testSearch() throws IOException {
		
		
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
		article.setTitle("love me"+UUID.randomUUID().toString());

		String jsonData = new ObjectMapper().writeValueAsString(article);

		IndexRequest indexRequest = new IndexRequest("article_index").source(jsonData, XContentType.JSON);

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		String index = indexResponse.getIndex();
		String id = indexResponse.getId();
		long version = indexResponse.getVersion();
		System.out.println("---------------------------------"+index);
		System.out.println(id);
		System.out.println(version);
	}
	

}
