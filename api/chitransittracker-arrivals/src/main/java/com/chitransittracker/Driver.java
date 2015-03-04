package com.chitransittracker;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.cta.util.CTAUtil;

public class Driver {
	
//	static Logger logger = Logger.getLogger(Driver.class);
	public static void main(String[] args) {
		ApplicationContext context = AppContext.startContext();
		CTAUtil ctaUtil = (CTAUtil) new CTAUtil();
		System.getProperties().get("CTA_API_TRAIN_KEY");
		System.out.println(ctaUtil.getCTA_API_TRAIN_KEY());
		System.out.println(System.getenv());
	}

}
