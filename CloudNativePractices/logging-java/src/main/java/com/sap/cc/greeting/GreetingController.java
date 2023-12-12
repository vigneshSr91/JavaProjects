package com.sap.cc.greeting;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
public class GreetingController {

	private GreetingService service;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public GreetingController(GreetingService service) {
		this.service = service;
	}

	@GetMapping("/hello")
	public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		MDC.put("path", "/hello");
		return getGreeting("Hello", name);
	}

	@GetMapping("/howdy")
	public String deprecatedGreeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		//System.err.println("Deprecated endpoint used.");
		MDC.put("path", "/howdy");
		logger.error("Deprecated endpoint used.", name);
		return getGreeting("Howdy", name);
	}

	private String getGreeting(String greeting, String name) {
		try {
			return service.createGreetingMessage(greeting, name);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		} finally {
			MDC.clear();
		}
	}

}
