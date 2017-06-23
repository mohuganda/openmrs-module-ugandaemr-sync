package org.openmrs.module.ugandaemrsync.server;

import org.apache.log4j.Logger;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by carapai on 24/04/2017.
 */
public class Zip {
	
	private static final Logger LOG = Logger.getLogger(Zip.class);
	
	public static final int BUFFER = 2048;
	
	private static List<String> filesListInDir = new ArrayList<String>();
	
	private static char[] VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456879".toCharArray();
	
	public static void zipDirectory(File dir, String zipDirName) {
		try {
			populateFilesList(dir);
			//now zip files one by one
			//create ZipOutputStream to write to the zip file
			FileOutputStream fos = new FileOutputStream(zipDirName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (String filePath : filesListInDir) {
				System.out.println("Zipping " + filePath);
				//for ZipEntry we need to keep only relative file path, so we used substring on absolute path
				ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);
				//read the file and write to ZipOutputStream
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024 * 1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
				File file = new File(filePath);
				file.delete();
			}
			zos.close();
			fos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String csRandomAlphaNumericString(int numChars) {
		SecureRandom srand = new SecureRandom();
		Random rand = new Random();
		char[] buff = new char[numChars];
		
		for (int i = 0; i < numChars; ++i) {
			if ((i % 10) == 0) {
				rand.setSeed(srand.nextLong());
			}
			buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
		}
		return new String(buff);
	}
	
	private static void populateFilesList(File dir) throws IOException {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile())
				filesListInDir.add(file.getAbsolutePath());
			else
				populateFilesList(file);
		}
	}
	
	public static String splitFile(File f) throws IOException {
        int partCounter = 1;

        int sizeOfFiles = 1024 * 1024;
        byte[] buffer = new byte[sizeOfFiles];

        String uniqueString = String.valueOf(csRandomAlphaNumericString(6));

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
            String name = uniqueString + "-" + f.getName();
            int tmp;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = new File(f.getParent(), name + "." + String.format("%03d", partCounter++));
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, tmp);
                }
            }
        }
        return uniqueString;
    }
}
