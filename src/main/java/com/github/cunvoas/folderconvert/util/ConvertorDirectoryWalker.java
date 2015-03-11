/**
 * 
 */
package com.github.cunvoas.folderconvert.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CUNVOAS
 */
public class ConvertorDirectoryWalker extends DirectoryWalker<File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertorDirectoryWalker.class);
	private static final Logger LOGGER_REJECT = LoggerFactory.getLogger("REJECTED_FILES");

	private static final IOFileFilter webPageFilter = FileFilterUtils.or(
			FileFilterUtils.suffixFileFilter(".html"),
			FileFilterUtils.suffixFileFilter(".htm"));

	private boolean performActive=true;
	private Charset sourceCharset = null;
	private Charset targetCharset = null;

	public ConvertorDirectoryWalker(Charset sourceCharset, Charset targetCharset) {
		super(FileFilterUtils.directoryFileFilter(), webPageFilter, -1);
		this.sourceCharset = sourceCharset;
		this.targetCharset = targetCharset;
	}
	
	private int nbPerformed=0;
	private int nbSkipped=0;
	private int nbError=0;

	/**
	 * @see org.apache.commons.io.DirectoryWalker#handleFile(java.io.File, int,
	 *      java.util.Collection)
	 */
	@Override
	public void handleFile(File file, int depth, Collection<File> results)
			throws IOException {
		String encoding = getCharset(file);
				
		if (this.sourceCharset.displayName().toUpperCase().equals(encoding)) {
			LOGGER.debug("perform {}", file.getAbsolutePath());
			if (performActive) {
				 FileEncodingConverter.convert(file, 
						 this.sourceCharset,
						 this.targetCharset);
				 
			}
			nbPerformed++;
		} else if (encoding==null || this.targetCharset.displayName().toUpperCase().equals(encoding)) {
			LOGGER.debug("skip {}", file.getAbsolutePath());
			nbSkipped++;
			
		} else {
			nbError++;
			LOGGER.debug("other encoding {} for {}", encoding, file.getAbsolutePath());
			LOGGER_REJECT.error("REJECT;{};{}", encoding, file.getAbsolutePath());
			//results.add(file);
		}

	}

	public List<File> perform(File directory) {
		List<File> others = new ArrayList<File>();
		long start = System.currentTimeMillis();
		nbPerformed=0;
		nbSkipped=0;
		nbError=0;
		try {
			walk(directory, others);

		} catch (IOException e) {
			LOGGER.error("Problem finding configuration files!", e);
		} finally {
			long duration =  (System.currentTimeMillis()-start)/1000;
			LOGGER.info("performed: {}, Skipped: {}, Error: {}, in {}s", nbPerformed, nbSkipped, nbError, duration);
		}
		return others;
	}

	private UniversalDetector detector = new UniversalDetector(null);
	/**
	 * 
	 * @param toDetect
	 * @return
	 * @see https://code.google.com/p/juniversalchardet/
	 */
	private String getCharset(File toDetect) {
		byte[] buf = new byte[4096];
		
		// (4)
		String encoding=null;
		FileInputStream fis = null;
		try {
			detector.reset();
			
			fis = new FileInputStream(toDetect);
			// (2) Feed some data (typically several thousands bytes) to the detector by calling UniversalDetector.handleData().
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// (3) Notify the detector of the end of data by calling UniversalDetector.dataEnd().
			detector.dataEnd();
			
			// (4) Get the detected encoding name by calling UniversalDetector.getDetectedCharset().
			encoding = detector.getDetectedCharset();


		} catch (FileNotFoundException e) {
			LOGGER.error("not found", e);
		} catch (IOException e) {
			LOGGER.error("read error", e);
		} finally {
			IOUtils.closeQuietly(fis);
			// (5) Don't forget to call UniversalDetector.reset() before you reuse the detector instance.
			detector.reset();
		}
		return encoding;
	}

	/**
	 * Setter for performActive.
	 * @param performActive the performActive to set
	 */
	public void setPerformActive(boolean performActive) {
		this.performActive = performActive;
	}

}
