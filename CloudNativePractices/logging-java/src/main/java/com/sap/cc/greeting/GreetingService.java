package com.sap.cc.greeting;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class GreetingService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	public String createGreetingMessage(String greeting, String name) {
		if (name.matches(".*[0-9].*")) {
			throw new IllegalArgumentException("Name must not contain numbers!");
		}
		//System.out.println("created greeting.");
		logger.debug("created greeting for {}.", name);
		return greeting + " " + name;
	}
}