import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		root=privateBuild();
	}
	private TagNode privateBuild(){
		int i;
		String sLine=null;
		boolean bLine=sc.hasNextLine();
		if(bLine==true)
			sLine=sc.nextLine();
		else return null;
		i=sLine.length();
		boolean x=false;
		if(sLine.charAt(0)=='<'){
			sLine=sLine.substring(1, i-1);
			if(sLine.charAt(0)=='/'){
				return null;
			}else{
				x=true;
			}
		}
		TagNode temp=new TagNode(sLine,null,null);
		if(x==true)
			temp.firstChild=privateBuild();
		temp.sibling=privateBuild();
		return temp;
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		privateReplaceTag(root,oldTag,newTag);
	}
	private void privateReplaceTag(TagNode n, String oldT, String newT){
		TagNode current=n;
		if(current==null) return;
		if(current.tag.equals(oldT)){
			current.tag=newT;
		}
		privateReplaceTag(n.firstChild,oldT,newT);
		privateReplaceTag(n.sibling, oldT, newT);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		TagNode current=new TagNode(null,null,null);
		TagNode temp;
		current=privateBoldRow(root);
		if(current==null){
			System.out.println("Not Found");
			return;
		}
		current=current.firstChild;
		for(int i=1;i<row;i++){
			current=current.sibling;
		}
		for(temp=current.firstChild;temp!=null;temp=temp.sibling){
			temp.firstChild=new TagNode("b",temp.firstChild,null);
		}
	}
	private TagNode privateBoldRow(TagNode current){
		if (current == null)
			return null; 
		TagNode nTemp = null;
		String sTemp = current.tag;
		if(sTemp.equals("table")) { 
			nTemp = current; 
			return nTemp;
		} 
		if(nTemp == null) {
			nTemp = privateBoldRow(current.firstChild);
		}
		if(nTemp == null) { 
			nTemp = privateBoldRow(current.sibling);
		} 
		return nTemp;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		privateRemoveTag(null,root,tag);
	}
	private void privateRemoveTag(TagNode previous, TagNode current, String s){
		if (current == null)
			return;
				if(current.tag.equals("b") || current.tag.equals("em") || current.tag.equals("p")) {
		if (current.tag.equals(s)) {
			if (previous.firstChild != null && previous.firstChild.tag.equals(current.tag)) {						
				if(current.sibling != null) {
					if (current.firstChild.sibling != null) {												
						TagNode temp = current.firstChild;
						previous.sibling = temp;
						while (temp.sibling != null) {
							temp = temp.sibling;
						}
						temp.sibling = current.sibling;
						current.firstChild = null;
						current.sibling = null;
					}else {																				
						current.firstChild.sibling = current.sibling;
						previous.firstChild = current.firstChild;
					}
				}else {
					previous.firstChild = current.firstChild;							
				}
			}else if (previous.sibling != null) {
				if(current.sibling != null) {
				if (current.firstChild.sibling != null) {
					TagNode temp = current.firstChild;
					previous.sibling = temp;
					while (temp.sibling != null) {
						temp = temp.sibling;
					}
					temp.sibling = current.sibling;
					current.firstChild = null;
					current.sibling = null;
				}else {
					current.firstChild.sibling = current.sibling;											
					previous.sibling = current.firstChild;
				}
				}else {
					previous.sibling = current.firstChild;
				}
			}
		}
		}else if(current.tag.equals("ol") || current.tag.equals("ul")) {
				if (current.tag.equals(s)) {
					if (previous.firstChild != null && previous.firstChild.tag.equals(current.tag)) {										
						if(current.sibling != null) {
							if (current.firstChild.sibling != null) {
								TagNode temp = current.firstChild;
								while (temp.sibling != null) {
									if (temp.tag.equals("li"))
										temp.tag = "p";
									temp = temp.sibling;
								}
								if (temp.tag.equals("li"))
									temp.tag = "p";
								temp.sibling = current.sibling;
								previous.firstChild = current.firstChild;
							}else {
								if (current.firstChild.tag.equals("li")) 
									current.firstChild.tag = "p";
								current.firstChild.sibling = current.sibling;
								previous.firstChild = current.firstChild;
							}
						}else {
							if (current.firstChild.sibling != null) {
								TagNode temp = current.firstChild;
								while(temp.sibling != null) {
									if (temp.tag.equals("li"))
										temp.tag = "p";
									temp = temp.sibling;
								}
								if (temp.tag.equals("li"))
									temp.tag = "p";
								previous.firstChild = current.firstChild;
							}else {
								if (current.firstChild.tag.equals("li")) 
									current.firstChild.tag = "p";	
								previous.firstChild = current.firstChild;							
							}
						}
					}else if (previous.sibling != null) {
						if(current.sibling != null) {
							if (current.firstChild.tag.equals("li"))
								current.firstChild.tag = "p";
							if (current.firstChild.sibling != null) {	
								TagNode temp=current.firstChild;
								previous.sibling = temp;
								while (temp.sibling != null) {
									if (temp.tag.equals("li"))
										temp.tag = "p";
									temp = temp.sibling;
								}
								if (temp.tag.equals("li"))
									temp.tag = "p";
								temp.sibling = current.sibling;
								current.firstChild = null;
								current.sibling = null;
							}else {
								current.firstChild.sibling = current.sibling;
								previous.sibling = current.firstChild;
							}
						}else {
							if (current.firstChild.sibling != null) {
								TagNode temp = current.firstChild;
								while(temp.sibling != null) {
									if (temp.tag.equals("li"))
										temp.tag = "p";
									temp = temp.sibling;
								}
								if (temp.tag.equals("li"))
									temp.tag = "p";
								previous.sibling = current.firstChild;
							}else {
								if (current.firstChild.tag.equals("li"))
									current.firstChild.tag = "p";
								previous.sibling = current.firstChild;
							}
						}
					}
				}
				}
		privateRemoveTag(current, current.firstChild, s);
		privateRemoveTag(current, current.sibling, s);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		privateAddTag(root,word,tag);
	}
	private void privateAddTag(TagNode current, String w, String t){
		TagNode t2 = current;
        Boolean x = true;
        if(current.tag.toLowerCase().contains(w.toLowerCase()))
        {      
            String punc = ".,?!:;";
            String sent = current.tag;
            String tempP = "";
            for(String testingW: sent.split(" "))
            {  
                if(testingW.length() >0 && punc.contains(testingW.substring(testingW.length()-1)))
                {
                    tempP = testingW.substring(testingW.length()-1);
                    testingW = testingW.substring(0, testingW.length()-1);
                }
                if(testingW.toLowerCase().equals(w.toLowerCase()))
                {
                    boolean power = false;
                    testingW += tempP;
                    String[]a  = sent.split(testingW);
               
                    int c = a.length;
                    String[]use = new String[c];
                    int d = 0;
                    for(int e = 0; e < a.length; e++)
                    {
                        if(a[e].length() == 0)
                        {
                        	power = true;
                            c--;
                        }
                        else
                        {
                            use[d] = a[e];
                            d++;
                        }
                    }
                    String[]copy = new String[d];
                    for(int i = 0; i < d; i++)
                        copy[i] = use[i];
                    a = copy;
                    TagNode add = new TagNode(testingW, null, null);
                    if(a.length == 0)
                    {
                        current.tag = t;
                        current.firstChild = add;
                    }
                    else if(a.length == 1)
                    {
                        if(power)
                        {
                            current.tag = t;
                            current.firstChild = add;
                            TagNode tt = new TagNode(a[0], null, current.sibling);
                            current.sibling = tt;
                        }
                        else
                        {
                            current.tag = a[0];
                            TagNode toTag = new TagNode(t, add , current.sibling);
                            current.sibling = toTag;
                            t2 = toTag;
                        }
                    }
                    else if(a.length >= 2)
                    {
                        String tempL = a[1];
                        for(int j = 2; j<a.length; j++)
                        	tempL += testingW + a[j];   
                        TagNode ptr = current.sibling;
                        current.tag = a[0];
                        t2 = new TagNode(tempL, null, ptr);
                        TagNode toTag = new TagNode(t, add ,t2);
                        current.sibling = toTag;
                        privateAddTag(t2, w, t);
                        return;
                    }
                    x = false;
                }
            }
        }
            if(x && current.firstChild != null)
                privateAddTag(current.firstChild, w, t);
            if(t2.sibling != null)
            	privateAddTag(t2.sibling, w, t); 
    }

	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
