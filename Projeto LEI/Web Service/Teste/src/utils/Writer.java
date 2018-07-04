package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import static utils.CSVUtils.writeLine;


public class Writer {
	
    private static String csvFile;
    private static PrintWriter writer;
        
    public static void initFileWriter(String csv) {
        csvFile = csv;
           
        try {
           OutputStream outputStream = new FileOutputStream(csvFile);
           writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    @SuppressWarnings("unchecked")
    public static Set<String> readJson(String path, Set<String> keyPhrases) 
    {
        
		JSONParser parser = new JSONParser();
		try {
	            Object obj = parser.parse(new InputStreamReader(new FileInputStream(path), "UTF-8"));
	                     
	            JSONObject jsonObject = (JSONObject) obj;
				
	            JSONArray docList = (JSONArray) jsonObject.get("documents");
	            Iterator<JSONObject> iteratorDoc = docList.iterator();
				
	            while(iteratorDoc.hasNext()) 
	            {
	                JSONArray keyPhrasesList = (JSONArray) iteratorDoc.next().get("keyPhrases");
	                Iterator<String> itKeyPhrases = keyPhrasesList.iterator();
					
					while(itKeyPhrases.hasNext()) 
					{
			                    String phrase = itKeyPhrases.next().toLowerCase();
			                    keyPhrases.add(phrase);
					}			
	            }
	        } catch(Exception e) {
	            e.printStackTrace();
		}
		return keyPhrases;
    }
	
    public static void writeAttributes(Set<String> keyPhrases) {
		
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("relatório");
        for(String keyphrase: keyPhrases) {
            attributes.add(keyphrase.toLowerCase());
        }
		
		try {
	            writeLine(writer, attributes);
	            writer.flush();		
		} catch (IOException e) {
	            e.printStackTrace();
	        }
    }
    
    public static void writeCSV(Set<String> keyPhrases)
    {
    	ArrayList<String> attributes = new ArrayList<>();
        attributes.add("relatório");
        for(String keyphrase: keyPhrases) {
            attributes.add(keyphrase.toLowerCase());
        }
		
		for(String att : attributes)
		{
			writer.write(att);
			writer.write(",");
			writer.flush();
		}
    }
        
    public static void writeReportLine(List<String> reportLine) {
		
		ArrayList<String> attributes = new ArrayList<>();
		attributes.addAll(reportLine);
			
		try {
	            writeLine(writer, attributes);
	            writer.flush();
		} catch (IOException e) {
	            e.printStackTrace();
	        }
    }
        
    public static void closeFileWriter() {
        writer.close();
    }
        
    public static void arffEnd() {
        writer.write("%\n%\n%\n");
    }
        
    public static void arffHeader(Set<String> keyPhrases) {
        Set<String> attributes = new HashSet<>();
        keyPhrases.stream().forEach((keyphrase) -> {
           attributes.add(keyphrase.toLowerCase().replace(" ", "_"));
        });
            
        writer.write("@RELATION train\n");
        for(String atributo : attributes) {
            writer.write("@ATTRIBUTE ");
            writer.write(atributo);
            writer.write(" {f,t}\n");
            writer.flush();
        }
        
        writer.write("@ATTRIBUTE tipo_de_exame {c,h}\n@ATTRIBUTE tipo_de_registo {ff,ft,tf,tt}\n\n@DATA\n");
        writer.flush();
    }
    
    public static void writeJSData(List<String> fr)
    {
        Set<String> frases = new TreeSet<>();
        frases.addAll(fr);
        writer.write("collection = [");
        for(String f: frases)
        {
            writer.write("'");
            writer.write(f.replace("\\n\\r",""));
            writer.write("',\n");
        }
        writer.write("];");
    }
    
    
    public static void writeFrequencia(Map<String, Integer> frequencias) {
    	
    	for(Map.Entry<String,Integer> f: frequencias.entrySet()) {
    		writer.write(f.getKey()+"#"+f.getValue());
    		writer.write("&");
    	}
    }

}
