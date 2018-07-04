package utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class OpsFicheiro 
{
	public static void removeLine(String lineContent, String file) {
        try {
            RandomAccessFile f = new RandomAccessFile(file, "rw");
            long length = f.length() - 1;
            byte b;
            do {
                length -= 1;
                f.seek(length);
                b = f.readByte();
            } while(b != 10);
            f.setLength(length+1);
            f.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
	public static String readFileToString(String filePath) {
        String content = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
	
	public static Map<String, Integer> readFrequenceToMap(String filePath)
	{
		Map<String ,Integer> freqKeyPhrases = new HashMap<>();
		String content = "";
        try 
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
            
            String [] regs = content.split("&");
            for(int i = 0; i < regs.length; i++)
            {
            	String [] comps = regs[i].split("#");
            	freqKeyPhrases.put(comps[0], Integer.parseInt(comps[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freqKeyPhrases;
	}
	
	
}
