package src;
import java.util.*;
import java.io.*;

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

public class retag_sentence{
	public String sentence;
	public HashMap<Integer, String> store_modified = new HashMap<Integer, String>();
	private ArrayList<String> individual_words = new ArrayList<String>();
	private ArrayList<String> words_shifted;

	public retag_sentence(String sentence){
		this.sentence = sentence;
		String[] temp = this.sentence.split("\\s+");
		for (int i = 0; i < temp.length; i++){
			//temp[i] = temp[i].replaceAll("[^\\w]", " ");
			this.individual_words.add(i, temp[i]);
			System.out.println(temp[i]);
		}
		this.store_modified = new HashMap<Integer,String>();
		for (int i = 0; i < individual_words.size(); i++){
			this.store_modified.put(i + 1, this.individual_words.get(i));
		}
	}

	public String replace_coreference_modified_sentence(int start, int end, String replacing_words){
		int count = 1;
		int count_after_replace = 0;
		String modified_sentence = "";
		int size_phrase_to_replace = end - start;
		HashMap<Integer, String> replacing =  new HashMap<Integer, String>();
		for (Map.Entry<Integer, String> entry : this.store_modified.entrySet()){
			if (count == start){
				if (size_phrase_to_replace > 0){ // The replacing phrase is greater than 2 words. We are going to have to shift the hashtable around a bit.
					String[] replacing_words_split = replacing_words.split(" "); 
					replacing.put(start, this.store_modified.get(start));
					System.out.println(this.store_modified.get(start));
					for (int i = 0; i < replacing_words_split.length; i++){
						replacing.put(start + i, replacing_words_split[i]);
					}
					count_after_replace = count + size_phrase_to_replace + 1;
					count += replacing_words_split.length;
				}
				else{ // You are in luck. Just replace the one word in the entry, and the rest of the sentence stays the same.
					replacing.put(start, this.store_modified.get(start));
					count_after_replace = count + 1; 
					count++;
				}
			}
			else{
				if (count > start){	// count_after_replace will tell you which word you are on. Store the size of the phrase outside the loop.
					if (this.store_modified.get(count_after_replace) != null){
					replacing.put(count, this.store_modified.get(count_after_replace));
					count_after_replace++;
					count++;
					}
				}
				else{
					replacing.put(count, entry.getValue());	// Check for compiler error on Line 74 
					count++;
				}
			}
		}
		String modified_2 = "";
		ArrayList<Integer> keys = new ArrayList<Integer>();
		for (Map.Entry<Integer, String> entry2 : replacing.entrySet()){
			keys.add(entry2.getKey());
			Collections.sort(keys);
		}
		for (int i = 0; i < keys.size(); i++){
			modified_2 = modified_2 + " " + replacing.get(keys.get(i));
		}
		// System.out.println(modified_2);
		return modified_2;
	}
}
 


