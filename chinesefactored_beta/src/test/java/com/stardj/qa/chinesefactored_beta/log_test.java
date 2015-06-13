package com.stardj.qa.chinesefactored_beta;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class log_test {
//	private final static Logger LOG = LoggerFactory.getLogger(log_test.class);
	private final static Logger LOG = LoggerFactory.getLogger("log.stardj");
	@Test
	public void testLog(){
		String str = "This is a test";
		try {  
            LOG.error("this is an error test.");  
            LOG.info(str);
            System.out.println("OK.");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		
	}

}
