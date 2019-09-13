package utils;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class contains helper static methods to read and write to files.
 * @author niquefadiego
 */

public class FileIO
{
	/**
	 * Write a serializable object to the given file.
	 * @return	true iff operation finished successfully
	 */
	public static boolean writeToFile(Object o, File file)
	{
		try 
		{
			FileOutputStream stream = new FileOutputStream(file);
			ObjectOutputStream objectStream = new ObjectOutputStream(stream);
			objectStream.writeObject(o);
			stream.close();
			objectStream.close();			
		}
		catch (FileNotFoundException e)
		{
			System.err.println(":( Error: FileNotFoundException e");
			e.printStackTrace();
			return false;
		}
		catch (IOException e)
		{
			System.err.println(":( Error: IOException e");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Reads an serialized object from a given file.
	 */
	public static Object readFromFile (File file) throws Exception
	{
		Object answer; 
		FileInputStream stream = new FileInputStream(file);
		ObjectInputStream objectStream = new ObjectInputStream(stream);
		answer = objectStream.readObject();
		stream.close();
		objectStream.close();
		return answer;
	}
}
