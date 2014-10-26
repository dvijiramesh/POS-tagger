package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class NaiveBayes {

	ArrayList<String> list_words = new ArrayList<String>();
	
	ArrayList<Features> word_list = new ArrayList<Features>();
	ArrayList<Features> test_word_list = new ArrayList<Features>();

	
	ArrayList<String> test_list_words = new ArrayList<String>();
	ArrayList<String> list_uniq_words = new ArrayList<String>();
	HashMap<String,Features> map_words_features = new HashMap<String,Features>();
	
    HashMap<String,Float> map_tagcount = new HashMap<String,Float>();
    
	ArrayList<String> list_pos = new ArrayList<String>();
	ArrayList<String> test_list_pos = new ArrayList<String>();
	ArrayList<String> calc_list_pos = new ArrayList<String>();
	Pattern p = Pattern.compile("[^a-z0-9 ]",Pattern.CASE_INSENSITIVE);

	int uniq;
	int totwords;
	int ct1;
	String pos_for_unq = "";


	HashMap<String,HashMap<String,Integer>> map_words_pos = new HashMap<String,HashMap<String,Integer>>();
	HashMap<String,Integer> map_pos = new HashMap<String,Integer>();
	String[] pos = new String[] { "CC", "CD", "DT", "EX","FW","IN","JJ","JJR","JJS","LS","MD","NN","NNS","NNP","NNPS","PDT","POS","PRP","PRP$","RB","RBR","RBS","RP","SYM","TO","UH","VB","VBD","VBG","VBN","VBP","VBZ","WDT","WP","WP$","WRB"};
    HashMap<String,Integer> innermap = new HashMap<String,Integer>();

	public void readFile() throws IOException{
		
		File file = new File("C:\\Users\\Vijayalakshmi\\Desktop\\nlp\\Assign2\\wsj-train.txt");

	    System.out.println("****************TRAIN FILE PRCESSING STARTS***********");
		
		String wholetextdata = FileUtils.readFileToString(file);
	    String[] words = wholetextdata.split("\\s+");

	    for (int i = 1;i<words.length;i++){
	    	for (int j = 0; j<pos.length;j++){ 
	            if( words[i].equals(pos[j])){
	            	Features wordfeature =createfeaturevector(words[i-1],words[i]); 
	            	word_list.add(wordfeature);
	           }
	    	}
	    }

    	for (int j = 0; j<pos.length;j++){ 
    		map_tagcount.put(pos[j], gettagcount(pos[j]));
    	}
    	
	    System.out.println("Word list count   " + word_list.size() + ". Calculating Feature count.");
/*	    
	    for (int i=0;i<word_list.size();i++) {
	    	
	    	Features feature = 	word_list.get(i);
	    	
	    	float f1_num = getfeaturecount(feature.word,feature.iswordtag,"WORD");
	    	float tagcount_den = gettagcount(feature.iswordtag);
	    	
	    	float f1 = (f1_num +1)/(tagcount_den + (2 * 1));
	    	Double logf1 = Math.log(f1);

	    	float f2_num = getfeaturecount(feature.iswordnumeric ,feature.iswordtag,"NUNERIC");
	    	float f2 = (f2_num +1)/(tagcount_den + (2 * 1));
	    	Double logf2 = Math.log(f2);	    	

	    	float f3_num = getfeaturecount(feature.iswordpunct ,feature.iswordtag,"PUNCT");
	    	float f3 = (f3_num +1)/(tagcount_den + (2 * 1));
	    	Double logf3 = Math.log(f3);	    	

	    	float f4_num = getfeaturecount(feature.iswordtitle ,feature.iswordtag,"TITLE");
	    	float f4 = (f4_num +1)/(tagcount_den + (2 * 1));
	    	Double logf4 = Math.log(f4);	    	

	    	float f5_num = getfeaturecount(feature.iswordupper ,feature.iswordtag,"UPPER");
	    	float f5 = (f5_num +1)/(tagcount_den + (2 * 1));
	    	Double logf5 = Math.log(f5);	    	

	    	feature.logprob = logf1 + logf2 + logf3 + logf4 + logf5;
	    	//System.out.println(feature.word + " -" + feature.iswordnumeric + " - " + feature.iswordpunct + " " + feature.iswordtag + "  " + feature.iswordtitle + "  " +feature.iswordupper + "  " +feature.logprob);
	    }
*/	    
	    System.out.println("****************TRAIN FILE PROCESSINF DONE ***********");
}

	public void test() throws IOException{
		
		File file = new File("C:\\Users\\Vijayalakshmi\\Desktop\\nlp\\Assign2\\wsj-test.txt");
	    String test_wholetextdata = FileUtils.readFileToString(file);
	    String[] test_words = test_wholetextdata.split("\\s+");
	    
	    double prob;
	    float correct = 0;
	    float accuracy;
	    float f1, f2,f3,f4,f5;
	    float f1_num,f2_num,f3_num,f4_num,f5_num;
	    double logf1, logf2,logf3,logf4,logf5;
	    double maxprob;
	    float tagcount_den=0;
	    
	    HashMap<String,Float> map_tagfeature = new HashMap<String,Float>();

	    
	    String mapkey;
	    
	    System.out.println("**************** TEST FILE PRCESSING STARTS ***********");
	    
	    for (int i = 1;i<test_words.length;i++){
	    	for (int j = 0; j<pos.length;j++){ 
	            if( test_words[i].equals(pos[j])){
//	            	String word = words[i-1];
	            	Features wordfeature =createfeaturevector(test_words[i-1],test_words[i]); 
	            	test_word_list.add(wordfeature);
	           }
	    	}
	    }
	    
	    System.out.println("TEST FILE SIZE - " + test_words.length + " - " + test_word_list.size());
	    
	    for (int i=0;i<test_word_list.size();i++) {

	    	if(i 
	    			% 4000 == 0){
	    		System.out.println("Processed " + i + "/" + test_word_list.size() + " more records");
	    	}
	    	
	    	Features feature = 	test_word_list.get(i);
	    	
	    	maxprob = -100;
	    	
	    	for( int j = 0; j<pos.length;j++){
		
	    		f1_num=0;f2_num=0;f3_num=0;f4_num=0;f5_num=0;
	    		
			    	f1_num = getfeaturecount(feature.word,pos[j],"WORD");
			    	tagcount_den = map_tagcount.get(pos[j]);
			    	
			    	
			    	mapkey = pos[j].concat("NUMERIC").concat(feature.iswordnumeric);
			    	
			    	if(!map_tagfeature.containsKey(mapkey)){
			    		f2_num = getfeaturecount(feature.iswordnumeric ,pos[j],"NUNERIC");
			    		map_tagfeature.put(mapkey, f2_num);
			    	}else{
			    		f2_num = map_tagfeature.get(mapkey);
			    	}
			    	
			    	mapkey = pos[j].concat("PUNCT").concat(feature.iswordpunct);
			    	
			    	if(!map_tagfeature.containsKey(mapkey)){
			    		f3_num = getfeaturecount(feature.iswordpunct ,pos[j],"PUNCT");
			    		map_tagfeature.put(mapkey, f3_num);
			    	}else{
			    		f3_num = map_tagfeature.get(mapkey);
			    	}
			    	
			    	mapkey = pos[j].concat("TITLE").concat(feature.iswordtitle);
			    	
			    	if(!map_tagfeature.containsKey(mapkey)){
			    		f4_num = getfeaturecount(feature.iswordtitle ,pos[j],"TITLE");
			    		map_tagfeature.put(mapkey, f4_num);
			    	}else{
			    		f4_num = map_tagfeature.get(mapkey);
			    	}			    	
		
			    	mapkey = pos[j].concat("UPPER").concat(feature.iswordupper);
			    	
			    	if(!map_tagfeature.containsKey(mapkey)){
			    		f5_num = getfeaturecount(feature.iswordupper ,pos[j],"UPPER");
			    		map_tagfeature.put(mapkey, f5_num);
			    	}else{
			    		f5_num = map_tagfeature.get(mapkey);
			    	}			    				    	
		
			    	
		
			    	logf1=0; logf2=0;logf3=0;logf4=0;logf5=0;
			    	
			    	if(wordexists(test_word_list.get(i).word)){
				    	f1 = (f1_num +1)/(tagcount_den + (2 * 1));
				    	logf1 = Math.log(f1);
			
				    	f2 = (f2_num +1)/(tagcount_den + (2 * 1));
				    	logf2 = Math.log(f2);	    	
				    	
				    	f3 = (f3_num +1)/(tagcount_den + (2 * 1));
				    	logf3 = Math.log(f3);	    	
				    	
				    	f4 = (f4_num +1)/(tagcount_den + (2 * 1));
				    	logf4 = Math.log(f4);	    	
			
				    	f5 = (f5_num +1)/(tagcount_den + (2 * 1));
				    	logf5 = Math.log(f5);	    	
			    	}else
			    	{
					    	f1 = (1)/(tagcount_den + (2 * 1));
					    	logf1 = Math.log(f1);
				
					    	f2 = (1)/(tagcount_den + (2 * 1));
					    	logf2 = Math.log(f2);	    	
					    	
					    	f3 = (1)/(tagcount_den + (2 * 1));
					    	logf3 = Math.log(f3);	    	
					    	
					    	f4 = (1)/(tagcount_den + (2 * 1));
					    	logf4 = Math.log(f4);	    	
				
					    	f5 = (1)/(tagcount_den + (2 * 1));
					    	logf5 = Math.log(f5);	    	
				    	}
			    	
			    	prob = logf1 + logf2 + logf3 + logf4 + logf5;
			    	feature.logprob = logf1 + logf2 + logf3 + logf4 + logf5;
			    	if (prob > maxprob) {
			    		maxprob = prob;
			    		feature.calctag = pos[j];
			    	}
	    	}	

	    	if (feature.calctag.equals(feature.iswordtag)){
	    		correct++;
	    	}
	    //	System.out.println(feature.word + " -" + feature.iswordnumeric + " - " + feature.iswordpunct + " " + feature.iswordtag + "  " + feature.iswordtitle + "  " +feature.iswordupper + "  " +feature.logprob + "  " +feature.calctag);
	    
	}

	    accuracy = correct/test_word_list.size();
	    
	    System.out.println("Accuracy  " + accuracy);
	    
	    System.out.println("****************TEST FILE PRCESSING DONE***********");
	    
	}

	
	private float getfeaturecount(String word, String tag, String feature){
		float wordcount=0;

	    for (int i=0;i<word_list.size();i++) {
	    	
			if(feature.equals("WORD")){
		    	if(word_list.get(i).word.equals(word) && word_list.get(i).iswordtag.equals(tag)){
		    		wordcount +=1;
		    	}
			}
			
			if(feature.equals("NUNERIC")){
			    	if(word_list.get(i).iswordnumeric.equals(word) && word_list.get(i).iswordtag.equals(tag)){
			    		wordcount +=1;
			    	}
			}
			
		    if(feature.equals("PUNCT")){
			    	if(word_list.get(i).iswordpunct.equals(word) && word_list.get(i).iswordtag.equals(tag)){
			    		wordcount +=1;
			    	}
			}
		    
		    if(feature.equals("TITLE")){
		    	if(word_list.get(i).iswordtitle.equals(word) && word_list.get(i).iswordtag.equals(tag)){
			    		wordcount +=1;
		    	}				    	
			}
		    
		    if(feature.equals("UPPER")){
		    	if(word_list.get(i).iswordupper.equals(word) && word_list.get(i).iswordtag.equals(tag)){
		    		wordcount +=1;
		    	}
			}
			}
	    return wordcount;
	}
	
	private float gettagcount(String tag){
		float tagcount=0;
	    for (int i=0;i<word_list.size();i++) {
	    	if(word_list.get(i).iswordtag.equals(tag)){
	    		tagcount +=1;
	    	}
	    }
	    return tagcount;
	}
		

	private Features createfeaturevector(String word,String tag){
		Matcher m = p.matcher(word);
		boolean b = m.find();
    	Features feature = new Features();
    	feature.word=word;
    	feature.iswordtag=tag;
    	
 //   	if(word.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
    	if(word.matches("[0-9]+")){
  //  		System.out.println("NUmeric");
    		feature.iswordnumeric="1";
    	}
    	
    	if (Character.isUpperCase(word.charAt(0))==true){
    		feature.iswordtitle="1";
    	}
    	
    	if (StringUtils.isAllUpperCase(word)){
    		feature.iswordupper="1";
    	}
    	
    	if(b){
    		feature.iswordpunct="1";
    	}
		return feature;
	}
	
	
	boolean wordexists(String word) {
		
		for (int i=0;i<word_list.size();i++){
			if (word.equals(word_list.get(i).word)){
				return true;
			}
			
		}
		return false;
		
		
	}
	

	public static void main(String[] args) throws IOException {
		NaiveBayes n = new NaiveBayes();
        n.readFile();
        n.test();
       
		
	}

}
