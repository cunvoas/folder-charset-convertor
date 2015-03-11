/**
 * 
 */
package com.github.cunvoas.folderconvert;

import java.io.File;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.cunvoas.folderconvert.util.ConvertorDirectoryWalker;

/**
 * @author CUNVOAS
 */
public class Main {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.setProperty("file.encoding", "Cp1252");
		
		LOGGER.info("START WALKING");
		ConvertorDirectoryWalker walker = new ConvertorDirectoryWalker(
				Charset.forName("Windows-1252"), //ISO-8859-1
				Charset.forName("UTF-8")
				);
		walker.setPerformActive(false);
		walker.perform(new File("D:/_POC/CARTUM"));
		
		LOGGER.info("STOP WALKING");
		
	}

}
