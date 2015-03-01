package com.chitransittracker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppContext {
	protected static ApplicationContext startContext(){
		 return new ClassPathXmlApplicationContext("applicationContext.xml");
	}
}
