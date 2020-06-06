package com.orange.fab;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.orange.fab.hystrixOperations.HystrixTrial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class HystrixController {

	@Value("${microservice.version}")
	private String microservice_version;
	
	@Value("${microservice.name}")
	private String microservice_name;

	//Added 4 Hystrix
	private final HystrixTrial ht;

	@Autowired
	public HystrixController(HystrixTrial ht)
	{
		this.ht = ht;
	}
	
    @RequestMapping(method=RequestMethod.GET, value="/v2/hystrix")
    public ResponseEntity<String> testSHystrixFallBack()
    {
    	HttpHeaders httpHeaders = new HttpHeaders();
      	httpHeaders.set("hystrIX", "YES");
        httpHeaders.set(microservice_name, microservice_version);
      	String HystrixOperationState = ht.SearchUrl4Broker();
    	return new ResponseEntity<String>(HystrixOperationState, httpHeaders, HttpStatus.OK);
    }
	
	@RequestMapping(method=RequestMethod.GET, value="/raw")
    public ResponseEntity<String> sendRawQuery(@RequestHeader MultiValueMap<String, String> queryHeaders)
    {
		String rawResponse = "<br>Raw HTTP Query Headers<br>";
		rawResponse += "++++++++++++++++++++++++++++++";
		queryHeaders.forEach((key, value) -> {
        rawResponse += String.format(
          "<br>'%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
		});
		
		HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(microservice_name, microservice_version);
      	
    	return new ResponseEntity<String>(rawResponse, httpHeaders, HttpStatus.OK);
    }
}
