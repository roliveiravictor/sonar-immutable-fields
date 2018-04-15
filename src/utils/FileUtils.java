package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.ResourceModel;

public class FileUtils {

	ArrayList<File> files = new ArrayList();

	public ArrayList<File> walkDirs(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return null;

		for (File f : list) {
			if (f.isDirectory()) {
				walkDirs(f.getAbsolutePath());
			} else {
				File file = new File(f.getAbsoluteFile().toString());
				if (file.isFile()) {
					files.add(file);
				}
			}
		}
		return files;
	}

	ArrayList<ResourceModel> filesName = new ArrayList();

	public ArrayList<ResourceModel> getResource(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return null;

		for (File f : list) {
			if (f.isDirectory()) {
				getResource(f.getAbsolutePath());
			} else {
				String fileName = f.getName().toString();
				try {
					fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				} catch (Exception e) {
					System.out.println(e);
				}
				if (fileName != null) {
					ResourceModel resource = new ResourceModel();
					resource.setPath(f.getAbsolutePath());
					resource.setName(fileName);
					resource.setNotFound(false);
					resource.setFile(f);
					filesName.add(resource);
				}
			}
		}
		return filesName;
	}
}
