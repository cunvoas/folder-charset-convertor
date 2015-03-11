package com.github.cunvoas.folderconvert.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEncodingConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileEncodingConverter.class);
	
	public static void convert(File toConvert, Charset source, Charset target) {
		
		FileInputStream fis = null;
		Reader reader = null;
		FileWriterWithEncoding fw =null;
		try {
			File tmpFile = new File(System.getProperty("java.io.tmpdir") + "tmp_"+toConvert.getName());
			FileUtils.deleteQuietly(tmpFile);
			
			fis = new FileInputStream(toConvert);
			reader = new InputStreamReader(fis, source);
			
			fw = new FileWriterWithEncoding(tmpFile, target);
			
			IOUtils.copy(fis, fw);
			

			IOUtils.closeQuietly(fw);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
			
			FileUtils.deleteQuietly(toConvert);
			FileUtils.moveFile(tmpFile, toConvert);
			FileUtils.deleteQuietly(tmpFile);
			
		} catch (FileNotFoundException e) {
			LOGGER.error("convert",e);
		} catch (IOException e) {
			LOGGER.error("convert",e);
		} finally {

			IOUtils.closeQuietly(fw);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
		}
		
		
	}

}
