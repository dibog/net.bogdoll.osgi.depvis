package net.bogdoll.osgi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OsgiUtil {
	public static Map<String,List<String>> unpack(String aOsgiHeaderLine) {
		if(aOsgiHeaderLine==null)
			return Collections.emptyMap();
		
		Map<String,List<String>> result = new LinkedHashMap<String, List<String>>();
		String[] split = split(aOsgiHeaderLine.replaceAll("\n|\r", ""), ',');
		for(String splitline : split) {
			String[] split2 = split(splitline.trim(), ';');
			List<String> elem;
			if(split2==null || split2.length==0)
				elem = Collections.emptyList();
			else {
				elem = new ArrayList<String>(split2.length);
				String key = split2[0].trim();
				if(split2.length==1)
					result.put(key,  Collections.<String>emptyList());
				else {
					for(int i=1, size=split2.length; i<size; ++i) 
					{
						elem.add(split2[i].trim());
					}
					result.put(key, elem);
				}
			}
		}
		
		return result;		
	}

	private static String[] split(String aText, char aSplitter) {
		List<String> result = new ArrayList<String>();
		int lastpos = 0;
		boolean quote = false;
		char[] chars = aText.toCharArray();
		for(int pos=0, size=chars.length; pos<size; pos++) {
			if(chars[pos]=='"') {
				quote = !quote;
				continue;
			}
			
			if(!quote && chars[pos]==aSplitter) {
				String fragement = aText.substring(lastpos, pos);
				result.add(fragement);
				pos++;
				lastpos = pos;
			}
		}
		result.add(aText.substring(lastpos));

		return result.toArray(new String[0]);
	}
}
