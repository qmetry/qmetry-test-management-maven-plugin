
package com.qmetry;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream; 

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
		if(formats.equals("qas/json"))
		{
			File testDir=lastFileModified(sourceDir);
			//System.out.println("Test directory");
			System.out.println(testDir.getPath());
			resultDir=testDir.getPath();
		}
		else
		{
			resultDir=sourceDir;
		}
		
		String zipDir=resultDir+"/"+"testresult.zip";
		
		zipDirectory(resultDir, zipDir, formats);
		
		return zipDir;
	}

	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles();
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		if(files!=null)
		{
			for (File file : files) {
				if(file.isDirectory() && !(file.getName()).equals("surefire"))
				{
					//System.out.println(file.getName()+":"+file.lastModified());
					if (file.lastModified() > lastMod) {
						choice = file;
						lastMod = file.lastModified();
					}
				}
			}
		}
		return choice;
	}
	
	 

	public static void zipDirectory(String sourceDir, String zipfile, String formats) throws IOException {
		/*System.out.println("\n\nDebug check 2 : "+sourceDir);
		System.out.println("\n\nDebug check 3 : "+zipfile);
		System.out.println("\n\nDebug check 4 : "+formats);*/
		String extention="";
		if(formats.equals("junit/xml") || formats.equals("testng/xml") || formats.equals("hpuft/xml")) {
			extention="xml";
		}else if(formats.equals("qas/json") || formats.equals("cucumber/json")){
			extention="json";
		}
		//System.out.println("\n\nDebug check 5 : "+extention);
		File dir = new File(sourceDir);
		File zipFile = new File(zipfile);
		//System.out.println("\n\nAbsolute zippath:"+zipFile.getAbsolutePath());
		FileOutputStream fout = new FileOutputStream(zipFile,false);
		ZipOutputStream zout = new ZipOutputStream(fout);
		//System.out.println("\n\nDebug check 6 : "+(fout!=null));
		//System.out.println("\n\nDebug check 7 : "+(zout!=null));
		zipSubDirectory("", dir, zout,extention);
		zout.close();
	}

	
	private static void zipSubDirectory(String basePath, File dir, ZipOutputStream zout,String extention) throws IOException 
	{
		//try
		//{
		//System.out.println("\n\nzipSubDirectory basepath : "+basePath);
		byte[] buffer = new byte[1024];
		File[] files=null;
		if(extention.equals("xml"))
			files = dir.listFiles(XML_FILE_FILTER);
		else
			files = dir.listFiles(JSON_FILE_FILTER);
		if(files!=null)
		{
			for (File file : files) 
			{
				//System.out.println("\n\nzipSubDirectory filefound : "+file.getAbsolutePath());
				if (file.isDirectory()) 
				{
					String path = basePath + file.getName() + "/";
					//zout.putNextEntry(new ZipEntry(path));
					zipSubDirectory(path, file, zout,extention);
					//zout.closeEntry();
				}
				else 
				{
					//if(file.isFile() && file.getName().endsWith(extention))
					//{
						zout.putNextEntry(new ZipEntry(basePath + file.getName()));
						FileInputStream fin = null;
						try{
							fin = new FileInputStream(file);
							//System.out.println("\n\nFile Name:"+file.getAbsolutePath());
							int length;
							while ((length = fin.read(buffer)) >= 0) 
							{
								//System.out.println("\nreading file:"+file.getName());
								zout.write(buffer, 0, length);
							}
							
						}
						/*catch(Exception e)
						{
							System.out.println("\n\nERROR DEBUG : "  + e);
						}*/
						finally
						{
							if(fin!=null)
							 fin.close();
							zout.closeEntry();
						}
					//}
				}
			}
		}
		/*}catch(Exception e)
		{
			System.out.println("\n\nERROR DEBUG : " + e.toString());
		}*/
	}
}