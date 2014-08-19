package net.mitchtech.xposed;
    
public class TextReplaceEntry {
   	public String actual;
   	public String replacement;
    	
   	public TextReplaceEntry() {
   		this("actual", "replacement");
   	}
    	
   	public TextReplaceEntry(String actual, String replacement) {
   		super();
   		this.actual = actual;
   		this.replacement = replacement;
   	}
    	
   	public String toString() {
   		return actual + " : " + replacement;
   	}
}