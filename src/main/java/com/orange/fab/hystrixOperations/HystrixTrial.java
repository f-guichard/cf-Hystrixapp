package com.orange.fab.hystrixOperations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand; // ;)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

//Hystrix from Netflix oss
@Service
public class HystrixTrial {
	
	private final RestOperations restOperations;
	
	@Value("${hystrix.message}") //depuis src/main/resources/application.properties
	private String hystrixFromAppProps;
	
	private String hystrixFromOtherApp;
	
	@Autowired
	public HystrixTrial(RestOperations restOperations)
	{
		//Added 4 Hystrix
		this.restOperations = restOperations;
		
		this.hystrixFromOtherApp = this.SearchUrl4Broker();
	}
			
	    public String getContent() {
	    	return this.hystrixFromAppProps+"  <=> "+this.hystrixFromOtherApp;
    }
    
    //Circuit break pattern : mon back de broker est down, j'ouvre mon circuit
    //Je commence par declarer la méthode de fallback
    //Merci Hystrix...
    public String fallbackSearchUrl4Broker() {
		return "fallback://camarchepas:whatswiththissecret@leserviceestcasse/";
	}

	@HystrixCommand(fallbackMethod = "fallbackSearchUrl4Broker")
	public String SearchUrl4Broker()
	{
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl("http://unmicroservicesurcloudfoundry/version/fonctionMetier")
				.queryParam("size", 5)
				.queryParam("lagedetoto", "4");
		RequestEntity<Void> request = RequestEntity
				.get(builder.build().encode().toUri())
				.accept(MediaType.APPLICATION_JSON)
				.build();

		//Jutilise request pour invoquer mon service distant
		ResponseEntity<String> result = restOperations.exchange(request,
				new ParameterizedTypeReference<String>() {
				});

		return result.getBody();
	}
}
