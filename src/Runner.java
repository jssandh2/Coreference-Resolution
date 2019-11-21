import java.io.FileNotFoundException;

import retag_sentence;


public class Runner {

	public static void main(String[] args) throws FileNotFoundException {
		nlp_read_in_data thing = new nlp_read_in_data();
		thing.read_in_file();
		thing.master_coreference_call();
		//retag_sentence thing2 = new retag_sentence("Juspreet is an interesting human being, and he should introspect deeply in life.");
		//thing2.replace_coreference_modified_sentence(3, 5, "should he really");
	}

}
