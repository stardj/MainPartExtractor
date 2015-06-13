package com.stardj.qa.chinesefactored_beta.mainpart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stardj.qa.chinesefactored_beta.segment.Segment;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;

public class MainPartExtractor {

	private static final Logger LOG = LoggerFactory
			.getLogger(MainPartExtractor.class);
	private static LexicalizedParser LP;
	private static GrammaticalStructureFactory GSF;

	public static TreeGraphNode subject;
	public static TreeGraphNode predicate;
	public static TreeGraphNode object;

	public static String result;

	static {
		// 模型
		String models = "models/chineseFactored.ser.gz";
		LOG.info("载入文法模型：" + models);
		LP = LexicalizedParser.loadModel(models);
		// 汉语
		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GSF = tlp.grammaticalStructureFactory();
	}

	/**
	 * 获取最小粒度短语
	 * 
	 * @param type
	 * @param tree
	 * @return
	 */

	public static List<String> getPhraseList(String type, String sentence) {
		return getPhraseList(type, LP.apply(segment(sentence)));
	}

	private static List<String> getPhraseList(String type, Tree tree) {
		List<String> phraseList = new LinkedList<String>();
		for (Tree subtree : tree) {
			if (subtree.isPrePreTerminal()
					&& subtree.label().value().equals(type)) {
				StringBuilder sbResult = new StringBuilder();
				for (Tree leaf : subtree.getLeaves()) {
					sbResult.append(leaf.value());
				}
				phraseList.add(sbResult.toString());
			}
		}
		return phraseList;
	}

	private static void NPPhraseMainPart(Tree tree) {
		StringBuilder sbResult = new StringBuilder();
		List<String> phraseList = getPhraseList("NP", tree);
		for (String phrase : phraseList) {
			sbResult.append(phrase);
		}
		result = sbResult.toString();
	}

	/**
	 * 合并名词性短语
	 * 
	 * @param tdls
	 * @param target
	 */
	private static void combineNN(Collection<TypedDependency> tdls,
			TreeGraphNode target) {
		if (target == null)
			return;
		for (TypedDependency td : tdls) {
			// 依存关系的出发节点，依存关系，以及结束节点
			TreeGraphNode gov = td.gov();
			GrammaticalRelation reln = td.reln();
			String shortName = reln.getShortName();
			TreeGraphNode dep = td.dep();
			if (gov == target) {
				if (shortName.equals("nn")) {
					target.setValue(dep.toString("value") + target.value());
				}
			}
		}
	}

	/**
	 * 获取root节点
	 * 
	 * @param tdls
	 * @return
	 */
	private static TreeGraphNode getRootNode(Collection<TypedDependency> tdls) {
		for (TypedDependency td : tdls) {
			if (td.reln() == GrammaticalRelation.ROOT) {
				return td.dep();
			}
		}
		return null;
	}

	/**
	 * 获取主谓宾
	 * 
	 * @param tree
	 * @return
	 */
	public void getMainPart(Tree tree) {

		if (tree.firstChild().label().toString().equals("NP")) {
			// 名词短语则认为只有主语，将所有短NP拼起来作为主语即可
			NPPhraseMainPart(tree);
		} else {
			GrammaticalStructure gs = GSF.newGrammaticalStructure(tree);
			if (gs == null) {
				NPPhraseMainPart(tree);
				return ;
			}
			Collection<TypedDependency> tdls = gs
					.typedDependenciesCCprocessed(true);
			TreeGraphNode rootNode = getRootNode(tdls);
			if (rootNode == null) {
				NPPhraseMainPart(tree);
				return ;
			}
			LOG.info("中心语：" + rootNode);
			predicate = rootNode;
			for (TypedDependency td : tdls) {
				// 依存关系的出发节点，依存关系，以及结束节点
				TreeGraphNode gov = td.gov();
				GrammaticalRelation reln = td.reln();
				String shortName = reln.getShortName();
				TreeGraphNode dep = td.dep();
				if (gov == rootNode) {
					if (shortName.equals("nsubjpass")
							|| shortName.equals("dobj")
							|| shortName.equals("attr")) {
						object = dep;
					} else if (shortName.equals("nsubj")
							|| shortName.equals("top")) {
						subject = dep;
					}

				}
				if (object != null && subject != null) {
					break;
				}
				combineNN(tdls, subject);
				combineNN(tdls, object);
			}

		}

	}

	/**
	 * 得到语法树
	 * 
	 * @param words
	 * @return
	 */
	public void getTree(List<Word> words) {

		Tree tree = LP.apply(words);
		LOG.info("语法树结构为：\n", tree.pennString());
		getMainPart(tree);

	}

	/**
	 * 分词
	 * 
	 * @param text
	 * @return
	 */

	private static List<Word> segment(String text) {
		List<Word> words = new ArrayList<Word>();
		LOG.info("分词：" + text);
		List<String> result = Segment.getWord(text);
		StringBuilder sb = new StringBuilder();
		for (String word : result) {
			words.add(new Word(word));
			sb.append(word + " ");
		}
		LOG.info("分词结果为：" + sb);
		return words;
	}

	public void getResult(String text) {
		getTree(segment(text));
		if (result != null) {
			System.out.println(result);
		} else {
			System.out.println(subject);
			System.out.println(predicate);
			System.out.println(object);
		}
	}

	public static void main(String[] args) {

		String str = "北京的大学";
		String str1 = "谁是清华大学第一任校长？";
		String str2 = "曾提出“当代青年拥有网络资源却沉浸于自我的小世界”这一问题的美国大学教授马克·鲍尔因悲观地认为人类或许将进入一个只剩下娱乐和成功的黑暗无知的时代";
		
		
		MainPartExtractor t = new MainPartExtractor();
		t.getResult(str2);

	}

}
