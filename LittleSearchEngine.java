import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		 HashMap<String,Occurrence> kws = new HashMap<String, Occurrence>();
	     Scanner scanner = new Scanner(new File(docFile));
	     while(scanner.hasNext()){
	    	 String word=getKeyword(scanner.next());
	    	 if(word!=null){
	    		 if(kws.containsKey(word))
	    			 kws.get(word).frequency++;
	    		 else 
	    			 kws.put(word, new Occurrence(docFile,1));
	    	 }
	     }
	     return kws;
	    }

	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		Iterator iterator = kws.keySet().iterator();
		int a; 
		while(iterator.hasNext()) {
			a = 0; 
			String s = (String)iterator.next();
			Occurrence occur = kws.get(s);
			if (keywordsIndex.containsKey(s)){
				ArrayList<Occurrence> arrayOccur = keywordsIndex.get(s); 
				arrayOccur.add(occur); 
				ArrayList<Integer> result = insertLastOccurrence(arrayOccur); 
				ArrayList<Occurrence> newOccur = new ArrayList<Occurrence>(); 
				for (int i = 0; i < arrayOccur.size()-1; i++)
					newOccur.add(arrayOccur.get(i)); 
				if (newOccur.size() == 1)
				{
					if (newOccur.get(0).frequency > occur.frequency)
						newOccur.add(occur); 	
					else 
						newOccur.add(result.get(result.size()-1), occur);
				}else if (result.get(result.size()-1) == newOccur.size()-1)
				{
					if (occur.frequency <= newOccur.get(newOccur.size()-1).frequency)
						newOccur.add(occur); 	
					else
						newOccur.add(result.get(result.size()-1), occur);
				}else if (result.get(result.size()-1) == 0)
				{
					if (occur.frequency >= newOccur.get(0).frequency)
					{
						a = 1; 
						ArrayList<Occurrence> temp = new ArrayList<Occurrence>(); 
						temp.add(occur); 
						temp.addAll(newOccur); 
						keywordsIndex.put(s, temp);
					}else
						newOccur.add(1, occur);
				}else
					newOccur.add(result.get(result.size()-1), occur); 
				if (a!=1)
					keywordsIndex.put(s, newOccur); 
			}
			else{
				ArrayList<Occurrence> arrayInt = new ArrayList<Occurrence>();
				arrayInt.add(occur); 
				keywordsIndex.put(s, arrayInt);
			}
				 
		}
    }
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		String first = "";
		String mid = ""; 
		String last = ""; 
		int a, b, c;
		if (checkPunc(word)) {	
			for (a = 0; a < word.length(); a++)
			{
				if (!Character.isLetter(word.charAt(a)))
					break;  
				else
					first = first + word.charAt(a); 
			}
			for (b = a; b < word.length(); b++)
			{
				if (Character.isLetter(word.charAt(b)))
					break; 
				else
					mid = mid + word.charAt(b);
			}
			for (c = b; c < word.length(); c++)
				last = last + word.charAt(c);
			if (last.isEmpty())
			{
			if (noiseWords.contains(first.toLowerCase()))
					return null;
				else{
					if (!first.trim().isEmpty())
						return first.toLowerCase();
					else
						return null; 
				}
			}else 
				return null; 
		}
		else{
			if (noiseWords.contains(word.toLowerCase()))
				return null;
			else{
				if (!word.trim().isEmpty())
					return word.toLowerCase();
				else
					return null;  
			}
		}
    }
	
	private static boolean checkPunc(String word){
		for (int i = 0; i < word.length(); i++)
		{
			if (!Character.isLetterOrDigit(word.charAt(i)))
				return true; 
		}
		return false; 
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> arrayInt = new ArrayList<Integer>(); 
		for (int i = 0; i < occs.size()-1; i++)
		{
			arrayInt.add(occs.get(i).frequency); 
		}
		int a = occs.get(occs.size()-1).frequency; 
		ArrayList<Integer> result = BS(arrayInt, a, 0, arrayInt.size()-1); 
		return result;
	    }
	
	private ArrayList<Integer> BS(ArrayList<Integer> array, int key, int min, int max)
	{
	  ArrayList<Integer> midpt = new ArrayList<Integer>(); 
	  while (max >= min)
	  {
	      int mid = (min + max) / 2;
	      midpt.add(mid); 
	      if (array.get(mid) <  key)
	    	 max = mid - 1;
	      else if (array.get(mid) > key )
	    	  min = mid + 1;
	      else
	    	  break; 
	  }
	  return midpt; 
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Occurrence> firstList = keywordsIndex.get(kw1);
		ArrayList<Occurrence> secondList = keywordsIndex.get(kw2);
		int i = 0, j = 0; 
		int total = 0; 
		if (firstList == null && secondList == null)
			return result; 
		else if (firstList==null){
			while (j < secondList.size() && total < 5){
				result.add(secondList.get(j).document); 
				j++; 
				total++; 
			}
		}
		else if (secondList==null){
			while (i < firstList.size() && total < 5){
				result.add(firstList.get(i).document); 
				i++; 
				total++; 
			}
		}
		else {	
			while ((i < firstList.size() || j < secondList.size()) && total < 5) {
				if (firstList.get(i).frequency > secondList.get(j).frequency 
						&& (!result.contains(firstList.get(i).document))) {
					result.add(firstList.get(i).document); 
					i++;
					total++; 
				}
				else if (firstList.get(i).frequency < secondList.get(j).frequency 
						&& (!result.contains(secondList.get(j).document))){
					result.add(secondList.get(j).document); 
					j++;
					total++; 
				}
				else{
					if (!result.contains(firstList.get(i).document)){
						result.add(firstList.get(i).document);
						total++; 
						i++;
					}
					else
						i++; 
					if ((!result.contains(secondList.get(j).document))){
						if (total < 5){
							result.add(secondList.get(j).document); 
							j++;
							total++; 
						}
					}
					else 
						j++; 
					
				}
			}
		}
		return result;
	}
}
