import java.util.*;
import java.io.*;

import retag_sentence;
import edu.stanford.nlp.*;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntTuple;

public class nlp_read_in_data {

	public ArrayList<String> documents;
	public ArrayList<String> output_documents;
	private CorefChain Corefs;
	public List<CoreMap> CoreMaps_Sentences;
	private ArrayList<String> current_coreferenced_document;
	private ArrayList<Boolean> coreferenced_sentences;
	private StanfordCoreNLP pipeline;
	private HashMap<Integer, String> print_sentences = new HashMap<Integer, String>(); 

	public nlp_read_in_data() {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		this.pipeline = new StanfordCoreNLP(props);
	}

	public void read_in_file() throws FileNotFoundException {
		this.documents = new ArrayList<String>();
		this.output_documents = new ArrayList<String>();
		BufferedReader docReader = new BufferedReader(
				new FileReader("imdb.dat"));
		String docLine;
		try {
			while ((docLine = docReader.readLine()) != null) {
				documents.add(docLine);
				output_documents.add(docLine);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			docReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void countWords(String s) {

		int wordCount = 0;

		boolean word = false;
		int endOfLine = s.length() - 1;

		for (int i = 0; i < s.length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before,
				// counter goes up.
			} else if (!Character.isLetter(s.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it
				// wouldn't count without this.
			} else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
				wordCount++;
			}
	}
		this.current_coreferenced_document = new ArrayList<String>(
				2 * wordCount);
	}

	public void master_coreference_call() {
		try {

			// /curr_sentence = entry.getValue();

			File file = new File(
					"/Users/Jus/Documents/Spring-2015/Research_Coref/imdb_coreferenced.dat");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i = 0; i < 2; i++) 
			{
				String modified_2 = "";
				double e = (double) i / (double) this.documents.size();
				System.out.println(e);
				System.out.println(this.documents.size());
				// boolean coreferenced_sentence = false;
				String current_document = this.documents.get(i);
				master_coreference_call_helper(current_document, i);
				 /// This function will be called to print a document once it
					// has been modified. Clear the hashmap once this is done.
				ArrayList<Integer> keys = new ArrayList<Integer>();
				for (Map.Entry<Integer, String> entry2 : this.print_sentences
						.entrySet()) {
					keys.add(entry2.getKey());
					Collections.sort(keys);
				}
				for (int j = 0; j < keys.size(); j++) {
					System.out.println(keys.get(j));
					modified_2 = modified_2 + " "
							+ this.print_sentences.get(keys.get(j));
				}
				modified_2 += "\n";
				this.print_sentences.clear();
				bw.write(modified_2);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void master_coreference_call_helper(String current_document, int i) {
		double b = System.currentTimeMillis();
		ArrayList<ArrayList<ArrayList<Integer>>> master_list = this
				.annotate_identify_coreference(current_document);
		System.out.println(" coreferencing took"
				+ (System.currentTimeMillis() - b)); // Decrease to manipulate
														// run time effectively
		int count_normal_sentence_start = 1;
		this.countWords(this.documents.get(i));
		this.current_coreferenced_document = new ArrayList<String>();
		this.coreferenced_sentences = new ArrayList<Boolean>();
		for (int j = 0; j < master_list.size(); j++) {
			int position_current_coreference = 0;
			ArrayList<ArrayList<Integer>> current_positions = master_list
					.get(j);
			System.out.println(master_list.get(j));
			int target_sent_num = current_positions.get(
					position_current_coreference).get(0);
			int target_start_num = current_positions.get(
					position_current_coreference).get(1);
			int target_end_num = current_positions.get(
					position_current_coreference).get(2);
			CoreMap curr_sentence = this.CoreMaps_Sentences
					.get(target_sent_num - 1);
			List<CoreLabel> target_sentence = curr_sentence
					.get(TokensAnnotation.class);
			List<CoreLabel> target_words = target_sentence.subList(
					target_start_num - 1, target_end_num - 1);
			String target_label = "";
			// int coreference_counter = target_sent_num;
			if (true) // redundant statement -> Replace with correct
						// modification for Hash-table
						// I need to call the constructors for mappings properly
						// here. I need to use some way to keep track of the
						// number of the words.
			{
				for (int k = count_normal_sentence_start; k < target_sent_num - 1; k++) {
					if (this.print_sentences.containsKey(k)) {
						continue;
					} else {
						CoreMap curr_normal_sentence = this.CoreMaps_Sentences
								.get(k - 1);
						List<CoreLabel> normal = curr_normal_sentence
								.get(TokensAnnotation.class);
						String word_normal = "";
						for (int l = 0; l < normal.size(); l++) {
							word_normal = word_normal + " "
									+ normal.get(l).value();
						}
						System.out.println(word_normal); 
						this.print_sentences.put(k, word_normal);

					}
				}
				for (int k = 0; k < target_words.size(); k++) {
					target_label = target_label + " "
							+ target_words.get(k).value();
				}
				String target_sentence_complete = " ";
				for (int a = 0; a < target_sentence.size(); a++) {
					target_sentence_complete = target_sentence_complete + " "
							+ target_sentence.get(a).value();
				}
				this.print_sentences.put(target_sent_num,
						target_sentence_complete);
				// }
				for (int k = 1; k < current_positions.size(); k++) {
					String word_reference = " ";
					int reference_sent_num = current_positions.get(k).get(0);
					int reference_start_num = current_positions.get(k).get(1);
					int reference_end_num = current_positions.get(k).get(2);
					CoreMap curr_coreference = this.CoreMaps_Sentences
							.get(reference_sent_num - 1);
					List<CoreLabel> coreference_sentence = curr_coreference
							.get(TokensAnnotation.class);
					List<CoreLabel> corference_words = coreference_sentence
							.subList(reference_start_num - 1,
									reference_end_num - 1);
					String reference = " ";
					for (int l = 0; l < reference_start_num - 1; l++) {
						reference += " " + coreference_sentence.get(l).value();
					}
					reference += target_label;
					for (int l = reference_end_num; l < coreference_sentence
							.size(); l++) {
						reference += " " + coreference_sentence.get(l).value();
					}
					if (this.print_sentences.containsKey(reference_sent_num)) {
						// call the retag sentence class and make sure that the
						// sentence is reparsed.
						System.out.println(target_label);
						src.retag_sentence temporary = new src.retag_sentence(
								this.print_sentences.get(reference_sent_num));
						String replaced = temporary
								.replace_coreference_modified_sentence(
										reference_start_num,
										reference_end_num, target_label); 
						this.print_sentences.put(reference_sent_num, replaced);
					} else {
						this.print_sentences.put(reference_sent_num, reference);
					}
				}
			}
			count_normal_sentence_start = target_sent_num;
		}
	}

	public ArrayList<ArrayList<ArrayList<Integer>>> annotate_identify_coreference(
			String document) {
		int position_curr = 0;
		Annotation curr_document = new Annotation(document);
		double b = System.currentTimeMillis();
		this.pipeline.annotate(curr_document);
		System.out
				.println("annotation took" + (System.currentTimeMillis() - b));
		List<CoreMap> sentences = curr_document.get(SentencesAnnotation.class);
		this.CoreMaps_Sentences = sentences;
		Map<Integer, CorefChain> graph = curr_document
				.get(CorefChainAnnotation.class);
		List<CorefChain.CorefMention> current = new ArrayList<CorefChain.CorefMention>();
		ArrayList<ArrayList<ArrayList<Integer>>> master_coreference_list = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
			ArrayList<ArrayList<Integer>> array_position = new ArrayList<ArrayList<Integer>>();
			this.Corefs = entry.getValue();
			current = this.Corefs.getMentionsInTextualOrder();
			if (current.size() > 1) {
				int count = 0;
				if (array_position.size() > 0) {
					position_curr = (array_position.size() - 1);
				}
				for (int j = 0; j < current.size(); j++) {
					CorefMention curr = current.get(j);
					ArrayList<Integer> rand = new ArrayList<Integer>();
					int sent_num = curr.sentNum;
					int word_num_start = curr.startIndex;
					int word_num_end = curr.endIndex;
					rand.add(sent_num);
					rand.add(word_num_start);
					rand.add(word_num_end);
					array_position.add(rand);
					count++;
				}
				master_coreference_list.add(array_position);
			}
		}
		System.out.println(master_coreference_list);
		return master_coreference_list;
	}

	public boolean repeating_coreference(String document, int sentnum,
			int startnum, int endnum) {
		Annotation curr_doc = new Annotation(document);
		this.pipeline.annotate(curr_doc);
		Map<Integer, CorefChain> current = curr_doc
				.get(CorefChainAnnotation.class);
		for (Map.Entry<Integer, CorefChain> entry : current.entrySet()) {
			ArrayList<ArrayList<ArrayList<Integer>>> reference_holders = new ArrayList<ArrayList<ArrayList<Integer>>>();
			CorefChain curr_corefchain = entry.getValue();
			List<CorefChain.CorefMention> corefmentions = curr_corefchain
					.getMentionsInTextualOrder();
			System.out.println(corefmentions);
		}
		return true;
	}
}
