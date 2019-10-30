package com.start.es;

import java.io.IOException;

public class JsonTest {
	
	public static void main(String[] args) throws IOException {
		String json= "{\r\n" + 
				"    \"mappings\" : {\r\n" + 
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
				"    }\r\n" + 
				"  }";
		
		
		
	}

}
