
package com.qmetry;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream; 
import java.util.zip.ZipFile;

public class CreateZip
{

	public static final FileFilter XML_FILE_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".xml");
		}
	};
	
	public static final FileFilter JSON_FILE_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".json");
		}
	};

	
	public static String createZip(String sourceDir, String formats) throws IOException,FileNotFoundException {
		String resultDir="";
		if(formats.equals("qas/json")) {
			File testDir=lastFileModified(sourceDir);
			if(testDir==null) {
				throw new FileNotFoundException("Cannot find latest test-results directory for QAS. Make sure you have entered correct file path.");
			}
			//System.out.println(testDir.getPath());
			resultDir=testDir.getPath();
		} else {
			resultDir=sourceDir;
		}
		
		String zipDir=resultDir+"/"+"testresult.zip";
		zipDirectory(resultDir, zipDir, formats);
		return zipDir;
	}

	public static File lastFileModified(String dir) throws FileNotFoundException{
		File fl = new File(dir);
		File[] files = fl.listFiles();
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		if(files!=null)
		{
			for (File file : files) {
				if(file.isDirectory() && !(file.getName()).equals("surefire"))
				{
					if (file.lastModified() > lastMod) {
						choice = file;
						lastMod = file.lastModified();
					}
				}
			}
		}
		if(choice == null)
		{
			throw new FileNotFoundException("Cannot find latest test-results files for QAS");
		}
		return choice;
	}
	
	public static void zipDirectory(String sourceDir, String zipfile, String formats) throws IOException {
		String extention="";
		if(formats.equals("junit/xml") || formats.equals("testng/xml") || formats.equals("hpuft/xml") || formats.equals("robot/xml")) {
			extention="xml";
		} else if(formats.equals("cucumber/json") || formats.equals("qas/json")) {
			extention="json";
		}
		File dir = new File(sourceDir);
		File zipFile = new File(zipfile);
		FileOutputStream fout = new FileOutputStream(zipFile,false);
		ZipOutputStream zout = new ZipOutputStream(fout);
		zipSubDirectory("", dir, zout,extention);
		if(formats.equals("qas/json")) {
			File img = new File(sourceDir + "/img");
			if(img.exists()) {
				zipSubDirectory("img/", img, zout, "png");
			}
		}
		zout.close();
		ZipFile zf = null;
		try {
			zf = new ZipFile(zipFile);
			int size = zf.size();
			if(size == 0) {
				throw new FileNotFoundException("Cannot find files of proper format in directory : "+sourceDir);
			}
		} finally {
			if(zf!=null)
				zf.close();
		}
	}

	
	private static void zipSubDirectory(String basePath, File dir, ZipOutputStream zout,String extention) throws IOException 
	{
		byte[] buffer = new byte[1024];
		File[] files=null;
		if(extention.equals("xml"))
		{
			files = dir.listFiles(XML_FILE_FILTER);
		}
		else if(extention.equals("json"))
		{
			files = dir.listFiles(JSON_FILE_FILTER);
		}
		else if(extention.equals("png"))
		{
			files = dir.listFiles();
		}
		if(files!=null)
		{
			for (File file : files) 
			{
				if (file.isDirectory()) 
				{
					String path = basePath + file.getName() + "/";
					zipSubDirectory(path, file, zout,extention);
				}
				else 
				{
					zout.putNextEntry(new ZipEntry(basePath + file.getName()));
					FileInputStream fin = null;
					try
					{
						fin = new FileInputStream(file);
						int length;
						while ((length = fin.read(buffer)) >= 0) 
						{
							zout.write(buffer, 0, length);
						}
					}
					finally
					{
						if(fin!=null)
						fin.close();
						zout.closeEntry();
					}
				}
			}
		}
	}
}