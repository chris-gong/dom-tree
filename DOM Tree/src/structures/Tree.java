package structures;

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
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		TagNode curr = null;
		Stack<TagNode> stack = new Stack<TagNode>();
		boolean lastAddedWasTextBased = false;
		boolean counterPartHasBeenFound = false;
		while(sc.hasNextLine()){
			String input = sc.nextLine();
			if(root == null){
				lastAddedWasTextBased = false;
				counterPartHasBeenFound = false;
				root = new TagNode(input.substring(1, input.length()-1),null,null);
				curr = root;
				stack.push(root);	
			}
			else if(root!=null){
				if(input.charAt(0)!='<'){
					if(curr.firstChild != null || lastAddedWasTextBased || counterPartHasBeenFound){
						lastAddedWasTextBased = true;
						counterPartHasBeenFound = false;
						curr.sibling = new TagNode(input,null,null);
						curr = curr.sibling;
					}
					else{
						lastAddedWasTextBased = true;
						counterPartHasBeenFound = false;
						curr.firstChild = new TagNode(input,null,null);
						curr = curr.firstChild;
					}
				}
				else if(input.charAt(0)=='<' && input.charAt(1)!='/'){
					if(counterPartHasBeenFound || lastAddedWasTextBased || curr.firstChild!=null){
						lastAddedWasTextBased = false;
						counterPartHasBeenFound = false;
						curr.sibling = new TagNode(input.substring(1,input.length()-1),null,null);
						curr = curr.sibling;
						stack.push(curr);
					}
					else{
						lastAddedWasTextBased = false;
						counterPartHasBeenFound = false;
						curr.firstChild = new TagNode(input.substring(1,input.length()-1),null,null);
						curr = curr.firstChild;
						stack.push(curr);
					}
				}
				else if(input.substring(0,2).equals("</")){
					lastAddedWasTextBased = false;
					counterPartHasBeenFound = true;
					curr = stack.pop();
				}
				
			}	
		}
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		TagNode temp = root;
		//WHAT ARE THE SPECIAL CASES? WHICH WEIRD REPLACEMENTS DO WE DEAL WITH?
		for(TagNode ptr = root; ptr != null; ptr = ptr.sibling){
			if(ptr.firstChild==null){
				if(ptr.tag.equals(oldTag)){
					ptr.tag = newTag;
				}
			}
			else{
				if(ptr.tag.equals(oldTag)){
					ptr.tag = newTag;
				}
				root = ptr.firstChild;
				replaceTag(oldTag,newTag);
			}
		}
		root = temp;
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		//what to do if row is less than 1 and is bigger than number of rows in table???
		TagNode temp = root;
		for(TagNode ptr = root; ptr != null; ptr = ptr.sibling){
			if(ptr.firstChild != null){
				if(ptr.tag.equals("tr")){
					if(row == 1){
						for(TagNode node = ptr.firstChild; node != null; node = node.sibling){
							if(node.tag.equals("td")){
								TagNode boldTag = new TagNode("b",node.firstChild,null);
								node.firstChild = boldTag;
							}
						}
						return;
					}
					else{
						row--;
						root = ptr.firstChild;
						boldRow(row);
					}
				}
				else{
					root = ptr.firstChild;
					boldRow(row);
				}
			}
			else{
				if(ptr.tag.equals("tr") && row != 1){
					row--;
				}
				else if(ptr.tag.equals("tr") && row == 1){
					return;
				}
			}
			
			
		}
		root = temp;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		TagNode temp = root;
		TagNode prev = null;
		if(tag.equals("p")||tag.equals("em")||tag.equals("b")){
			for(TagNode ptr = root; ptr != null; ptr = ptr.sibling){
				//WHAT IF FOUND TAG HAS NO CHILD?
				if(ptr.firstChild!=null){
					if(ptr.tag.equals(tag)){
						
						if(prev == null){
							TagNode lastSibling = ptr.firstChild;
							for(TagNode node = ptr.firstChild; node != null; node = node.sibling){
								lastSibling = node;
							}
							lastSibling.sibling = ptr.sibling;
							ptr.sibling = ptr.firstChild;
							ptr.tag = ptr.firstChild.tag;
							ptr.firstChild = ptr.sibling.firstChild;
							ptr.sibling = ptr.sibling.sibling;
							
						}
						else{
							TagNode lastSibling = ptr.firstChild;
							for(TagNode node = ptr.firstChild; node != null; node = node.sibling){
								lastSibling = node;
							}
							lastSibling.sibling = ptr.sibling;
							ptr.sibling = ptr.firstChild;
							ptr.tag = ptr.firstChild.tag;
							ptr.firstChild = ptr.sibling.firstChild;
							ptr.sibling = ptr.sibling.sibling;
						}
					}
					root = ptr.firstChild;
					removeTag(tag);	
				}
				
				prev = ptr;	
			}
			
			
		}
		else if(tag.equals("ol")||tag.equals("ul")){
			for(TagNode ptr = root; ptr != null; ptr = ptr.sibling){
				//WHAT IF FOUND TAG HAS NO CHILD?
				if(ptr.firstChild!=null){
					if(ptr.tag.equals(tag)){
	
						if(prev == null){
							TagNode lastSibling = ptr.firstChild;
							for(TagNode node = ptr.firstChild; node != null; node = node.sibling){
								if(node.tag.equals("li")){
									node.tag = "p";
								}
								lastSibling = node;
							}
							
							lastSibling.sibling = ptr.sibling;
							ptr.sibling = ptr.firstChild;
							ptr.tag = ptr.firstChild.tag;
							ptr.firstChild = ptr.sibling.firstChild;
							ptr.sibling = ptr.sibling.sibling;
							
						}
						else{
							TagNode lastSibling = ptr.firstChild;
							for(TagNode node = ptr.firstChild; node != null; node = node.sibling){
								if(node.tag.equals("li")){
									node.tag = "p";
								}
								lastSibling = node;
							}
							
							lastSibling.sibling = ptr.sibling;
							ptr.sibling = ptr.firstChild;
							ptr.tag = ptr.firstChild.tag;
							ptr.firstChild = ptr.sibling.firstChild;
							ptr.sibling = ptr.sibling.sibling;
						}
						
					}
				
					root = ptr.firstChild;
					removeTag(tag);
				}
				prev = ptr;
			}
		}
		
		root = temp;
		
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		//what to do if tag is not em or b
		//don't add tags to words with more than one punctuation character at the end
		TagNode temp = root;
		
		for(TagNode ptr = root; ptr != null; ptr = ptr.sibling){
			if(ptr.firstChild != null){
				root = ptr.firstChild;
				addTag(word, tag);
			}
			//if ptr does not have a firstChild, then it's probably a text-based tag
			else{
				int index = ptr.tag.toLowerCase().indexOf(word.toLowerCase());
				while(index != -1){
					
					int wordLength = word.length();
					//have to check if word can even have an extra character at 
					//the end before actually checking what it is
					if(ptr.tag.substring(index).length() >= wordLength + 1 && (ptr.tag.charAt(index+wordLength)=='?' || ptr.tag.charAt(index+wordLength)=='!' ||
							ptr.tag.charAt(index+wordLength)=='.' || ptr.tag.charAt(index+wordLength)==';' || ptr.tag.charAt(index+wordLength)==':')){
						
						wordLength = wordLength + 1;
					}
					
					if(((ptr.tag.substring(index).length() >= wordLength + 1 && ptr.tag.charAt(index+wordLength)==' ') &&
							(index != 0 && ptr.tag.charAt(index - 1) == ' ')) || ((ptr.tag.substring(index).length() >= wordLength + 1 && ptr.tag.charAt(index+wordLength)==' ') &&
									(index == 0)) || ((ptr.tag.substring(index).length() == wordLength) &&
											(index != 0 && ptr.tag.charAt(index - 1) == ' '))
							||((ptr.tag.substring(index).length()==wordLength) && index == 0)){
						String left = ptr.tag.substring(0,index);
						String middle = ptr.tag.substring(index, index + wordLength);
						String right = ptr.tag.substring(index + wordLength, ptr.tag.length());
						//only problem with this code is that if the word to tag is by itself it
						//will create a sibling of blank text
						if(index == 0){
							/*if(!ptr.tag.contains(" ")){
								TagNode child=new TagNode(middle,null,null);
								ptr.tag=tag;
								ptr.firstChild=child;
							}else{
							TagNode child = new TagNode (middle, null, null);
							TagNode sibling = new TagNode (right, null, ptr.sibling);
							ptr.tag = tag;
							ptr.firstChild = child;
							ptr.sibling = sibling;
							}*/
							ptr.tag = tag;
							ptr.firstChild = new TagNode(middle,null,null);
							TagNode newSibling = new TagNode(right,null,ptr.sibling);
							ptr.sibling = newSibling;
							
						}
						//ptr=ptr.sibling is done at the end of each of these 2 cases because
						//after this iteration, ptr will be ptr.sibling but in these 2 cases
						//ptr.firstChild contains the target word, but we've already addressed it
						//so we can skip the next sibling basically
						else if(index + wordLength >= ptr.tag.length()){
							
							ptr.tag = left;
							TagNode newSibling = new TagNode(tag, new TagNode(middle,null,null),ptr.sibling);
							ptr.sibling = newSibling;
							ptr = ptr.sibling;
							
						}
						else{
							
						    ptr.tag = left;
						    TagNode newSibling = new TagNode(tag, new TagNode(middle, null, null), ptr.sibling);
						    if(ptr.sibling == null){
						    	TagNode mostRight = new TagNode(right, null, null);
						    	newSibling.sibling = mostRight;
						    }
						    else{
						    	newSibling.sibling = new TagNode(right, null, ptr.sibling);
						    	
						    }
						    ptr.sibling = newSibling;
						    ptr = ptr.sibling;
						   
						}
						
						break;
					}
					else{
						index = ptr.tag.toLowerCase().indexOf(word.toLowerCase(), index + 1);
						if(index == -1){
							break;
						}
					}
				}
				
			}
			
		}
		root = temp;
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
	
}
