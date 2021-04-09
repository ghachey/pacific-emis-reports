package jasper.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DynamicSourcePath {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		
		System.out.println("Enter a the Directory path in which .jrxml files are Located :");
		
		String sourceDirectoryPath=sc.nextLine();

	//	String sourceDirectoryPath = "D:/DOE_Latest/Java_Dynamic Source Path"; // user will enter
		ArrayList<String> ReportNames = DynamicSourcePath.getFileNamesFromFolder(sourceDirectoryPath);

		for (String reportName : ReportNames) {
			
			System.out.println("Report Name is : "+reportName);
			System.out.println("..............................");

			String reportPath = sourceDirectoryPath + "/" + reportName;
			try {
				DynamicSourcePath.fileReading(reportPath, reportName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static StringBuilder fileReading(String filepath, String reportName) throws IOException {
		StringBuilder result = new StringBuilder();
		String data = "", output = "";
		File file = new File(filepath);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
	

		String resultString = "";

		System.out.println("repo: expression needs to be changed in the following expression :");
		System.out.println();

		while ((data = br.readLine()) != null) {

			if (data.contains("<template>")) {

				int firstposition = data.indexOf("\"") + 1;
				int lastposition = data.lastIndexOf("\"");

				String localpath = data.substring(firstposition, lastposition);
				
				int lastSlashPosition=localpath.lastIndexOf("/")+1;
			
			String templateName=localpath.substring(lastSlashPosition, localpath.length());
			System.out.println("StyLeName :::"+templateName);
			
			String serverTemplatePath = "repo:/Templates/"+templateName;
			
			

				data = data.replace(localpath, serverTemplatePath);
				
				
				

			}
			if (data.contains("<imageExpression>")) {

				int firstposition = data.indexOf("\"") + 1;
				int lastposition = data.lastIndexOf("\"");

				String local_img_path = data.substring(firstposition, lastposition);
				

				int lastSlashPosition=local_img_path.lastIndexOf("/")+1;
			
			String ImageName=local_img_path.substring(lastSlashPosition, local_img_path.length());
			
			
			String serverImagesPath = "repo:/images/"+ImageName;

				data = data.replace(local_img_path, serverImagesPath);

			}
			resultString = resultString + data + "\n";

			output += data;
		}

		DynamicSourcePath.Filewriter(resultString, reportName);

		System.out.println("Report printing...............................");

		System.out.println(resultString);

		System.out.println("Report printing ended...............................");

		result.append(output);
		return result;
	}

	public static void Filewriter(String inputString, String reportName) throws IOException {

		try {

			DynamicSourcePath.createdFile(reportName);

			FileWriter fw = new FileWriter("D:/DOE_Latest/Java_Dynamic Source Path/output/" + reportName);

			fw.write(inputString);
			System.out.println("In file Writing............");
			fw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("Success...");
	}

	public static void createdFile(String FileName) {

		try {
			File file = new File("D:/DOE_Latest/Java_Dynamic Source Path/" + FileName);
			// File file = new File("C:\\newfile.txt");

			boolean fvar = file.createNewFile();
			if (fvar) {
				System.out.println("File has been created successfully");
			} else {
				System.out.println("File already present at the specified location");
			}
		} catch (IOException e) {
			System.out.println("Exception Occurred:");
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getFileNamesFromFolder(String directoryName) {

		ArrayList<String> al = new ArrayList<String>();

		File directory = new File(directoryName);
		File[] files = directory.listFiles();
		//
		for (File file : files) {
			if (file.isFile()) {

				String filename = file.getName();

				if (filename.endsWith(".jrxml")) {
					al.add(filename);
				}

			}
		}
		return al;
	}

}
