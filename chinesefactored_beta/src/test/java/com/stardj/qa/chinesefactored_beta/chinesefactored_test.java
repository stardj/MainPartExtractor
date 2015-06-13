package com.stardj.qa.chinesefactored_beta;

import org.junit.Test;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class chinesefactored_test {

	@Test
	public void testCinesefactored() {

		String[] arg2 = { "-encoding", "utf-8", "-outputFormat",
				"penn,typedDependenciesCollapsed",
				"models/chineseFactored.ser.gz",
				"E:\\chinesefactored.txt" };
		LexicalizedParser.main(arg2);

	}

}
