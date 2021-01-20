package util;

import java.util.ArrayList;

public class UtilFunc {
	
	public static ArrayList<String> warpText(String input, int width){
		int curpos = 0;
	    int nextpos = 0;

	    ArrayList<String> lines = new ArrayList<String>();
	    String substr = input.substring(curpos, Math.min(curpos + width + 1, input.length() - 1));

	    while (substr.length() == width + 1 && (nextpos = substr.lastIndexOf(' ')) != -1) {
	        lines.add(input.substring(curpos, Math.min(nextpos + curpos, input.length() - 1)));
	        curpos += nextpos + 1;
	        substr = input.substring(curpos, Math.min(curpos + width + 1, input.length()));
	    }

	    if (curpos != input.length())
	        lines.add(input.substring(curpos));
	    
	    for(String s : lines)
	    	s.trim();
	    	
	    return lines;
	}
	
}
