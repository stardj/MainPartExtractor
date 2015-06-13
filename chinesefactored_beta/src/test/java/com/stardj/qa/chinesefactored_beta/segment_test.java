package com.stardj.qa.chinesefactored_beta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.stardj.qa.chinesefactored_beta.segment.Segment;

public class segment_test {

	@Test
	public void testSegment(){
		String[] str = {
				
			"北京的大学",
			"谁是清华大学第一任校长？",
			"曾提出“当代青年拥有网络资源却沉浸于自我的小世界”这一问题的美国大学教授马克·鲍尔因悲观地认为人类或许将进入一个只剩下娱乐和成功的黑暗无知的时代"
			
		};
		
		HashMap<String,String> result = new HashMap<String,String>();
		List<String> result1 = new ArrayList<String>();
		
		for(String text : str){
			System.out.println("分词："+text);
			result = Segment.getParser(text);
			for(String temp : result.keySet()){
				System.out.println(temp+"|"+result.get(temp));
			}
			
			result1 = Segment.getWord(text);
			for(String temp1 : result1){
				System.out.println(temp1);
			}
			
		}
	}
}
