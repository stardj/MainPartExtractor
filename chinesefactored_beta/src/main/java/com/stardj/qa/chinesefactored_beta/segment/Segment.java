package com.stardj.qa.chinesefactored_beta.segment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Segment {
	private static final Logger LOG = LoggerFactory.getLogger(Segment.class);


	/**
	 * 加载停用词典
	 */
	static {
		String filepath = "library/stopword.dic";
		LOG.info("停用词典 "+ filepath);
		File file = new File(filepath);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file.getAbsolutePath()));
			while (true) {
				String str = in.readLine();
				FilterModifWord.insertStopWord(str);
				if (in.readLine() == null)
					break;
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Segment() {

	}

	/**
	 * 分词以及标注词性
	 * 
	 * @return Map<String,String> 分词结果和词性
	 */
	public static HashMap<String, String> getParser(String text) {
		List<Term> iparser = ToAnalysis.parse(text.replace("?", "").replace("？", ""));
		String regEx = "./w|./nr|./m|\\[|\\]|:, ";
		Pattern pat=Pattern.compile(regEx);
		Matcher mat=pat.matcher(iparser.toString());
		String s=mat.replaceAll("");
//		System.out.println(s);
		String[] parser = s.split(", ");
		// 用hashmap来存分词结果和词性
		HashMap<String, String> result = new HashMap<String, String>();
		for (String slip : parser) {
			if(!slip.equals("")){
				String[] str = slip.split("/");
				if(str.length==2)
				result.put(str[0],str[1]);
			}
		}
		return result;
	}

	/**
	 * @param text
	 * @return List<String> 获取分词结果
	 */
	public static List<String> getWord(String text) {

		List<String> result = new ArrayList<String>();
		List<Term> iparser = ToAnalysis.parse(text.replace("?", "").replace("？", ""));
		String regEx = "./w|./nr|./m|\\[|\\]|:, ";
		Pattern pat=Pattern.compile(regEx);
		Matcher mat=pat.matcher(iparser.toString());
		String s=mat.replaceAll("");
//		System.out.println(s);
		String[] parser = s.split(", ");
		for (String slip : parser) {
			if(!slip.equals("")){
				String[] str = slip.split("/");
				if(str.length==2)
				result.add(str[0]);
			}
		}

		return result;

	}

	/**
	 * 
	 * @param text
	 * @return String 获取词性
	 */
	public static String getPOS(String text) {
		List<String> lists = Arrays.asList(text);
		try {
			List<Term> recognition = NatureRecognition.recognition(lists, 0);
			for (int i = 0, j = 1; i < recognition.toString().toCharArray().length; i++) {
				if (recognition.toString().charAt(i) == '/')
					j = i + 1;
				if (recognition.toString().charAt(i) == ']')
					return recognition.toString().substring(j, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
