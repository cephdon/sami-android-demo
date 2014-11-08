package sami.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.content.Context;
import android.util.Log;


public class FileUtils {
	public static final String TAG = FileUtils.class.getName();
	
	/**
	 * Writes a text file to private app storage
	 * @param context
	 * @param fileBaseName only the name of the file, no path
	 * @param content text inside the file
	 * @return
	 */
	public static boolean savePrivateFile(Context context, String fileBaseName, String content){
	    try {
	        FileOutputStream fos = context.openFileOutput(fileBaseName, Context.MODE_PRIVATE);
	        Writer out = new OutputStreamWriter(fos);
	        out.write(content);
	        out.close();
	        return true;
	    } catch (IOException e) {
	        Log.e(TAG, "Error saving file: "+fileBaseName);
	        return false;
	    }
	}
	
	/**
	 * Returns the content of a private file
	 * @param context
	 * @param fileBaseName filename, without path
	 * @return
	 */
	public static String readPrivateFile(Context context, String fileBaseName){
		FileInputStream fis;
		try {
			fis = context.openFileInput(fileBaseName);
		} catch (FileNotFoundException e) {
			Log.w(TAG, "File not found: "+fileBaseName);
			return null;
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(fis));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
	        	if (stringBuilder.length() > 0) { 
	        		stringBuilder.append("\n");
	        	}
	            stringBuilder.append(line);
	        }
			r.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			Log.w(TAG, "Problem reading file: "+fileBaseName);
			return null;
		}
	}
	
	/**
	 * Deletes the file from private folder
	 * @param fileBaseName
	 * @return
	 */
	public static boolean deletePrivateFile(Context context, String fileBaseName){
		File file = new File(context.getFilesDir()+File.separator+fileBaseName); 
		try {
			if(file.exists()){
				file.delete();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error deleting file: "+fileBaseName);
			return false;
		}
		return true;
	}
}
