package textminer;

import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import static utils.Caminhos.FICHEIRO_CITOLOGIAS;
import static utils.Caminhos.FICHEIRO_HISTOLOGIAS;
import static utils.Caminhos.FICHEIRO_KEYPHRASES;
import static utils.Caminhos.FICHEIRO_FREQUENCIA;
import static utils.Caminhos.FICHEIRO_CASOS_NORMAIS_CITOLOGICOS;
import static utils.Caminhos.FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS;
import static utils.Caminhos.FICHEIRO_CASOS_NORMAIS_HISTOLOGICOS;
import static utils.Caminhos.FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS;
import static utils.ParserRelatorios.getRelatorios;
import static utils.PreenchimentoARFF.assignTipoRegisto;
import static utils.PreenchimentoARFF.getKeyPhrasesFicheiroTeste;
import static utils.PreenchimentoARFF.reportCheckKeyPhrase;
import static utils.Writer.arffEnd;
import static utils.Writer.arffHeader;
import static utils.Writer.closeFileWriter;
import static utils.Writer.initFileWriter;
import static utils.Writer.writeFrequencia;
import static utils.Writer.readJson;
import static utils.Writer.writeJSData;
import static utils.Writer.writeReportLine;
import static utils.Caminhos.FICHEIRO_FRASES;
import static utils.Caminhos.FICHEIRO_KEYPHRASES_CSV;

import static textminer.APIMicrosoft.GetKeyPhrases;
import static textminer.APIMicrosoft.prettify;
import static utils.CSVUtils.carregaCSV;

public class TextMiner 
{
    
    private static List<String> populateData(List<String> frases, String [] c10, String [] c20, String [] notas, String [] info)
    {

        for(int z = 0; z < c10.length; z++)
        { 
           if(!"".equals(c10[z]) && !"\n".equals(c10[z]) && !c10[z].matches("[0-9]+") && !c10[z].trim().isEmpty())
        	   frases.add(c10[z].replace("\n", "").replace(".", "")); 
        }
        for(int z = 0; z < c20.length; z++)
        {
        	if(!"".equals(c20[z]) && !"\n".equals(c20[z]) && !c20[z].matches("[0-9]+") && !c20[z].trim().isEmpty())
                frases.add(c20[z].replace("\n", "").replace(".", ""));
        }
        for(int z = 0; z < notas.length; z++)
        {
        	if(!"".equals(notas[z]) && !"\n".equals(notas[z]) && !notas[z].matches("[0-9]+") && !notas[z].trim().isEmpty())
                frases.add(notas[z].replace("\n", "").replace(".", ""));
        }
        for(int z = 0; z < info.length; z++)
        {
        	if(!"".equals(info[z]) && !"\n".equals(info[z]) && !info[z].matches("[0-9]+") && !info[z].trim().isEmpty())
        	   frases.add(info[z].replace("\n", "").replace(".", ""));
        }

        return frases;
    }
    
    public static boolean relatorioAnormal(Map<String, Integer> freqKeyPhrases, List<String> keyPhrases, String tipoRelatorio)
    {
    	int freq, mapSize;
    	freq = 10;
		mapSize = 2;
    	if(tipoRelatorio.equals("c"))
    	{
    		freq = 40;
    		mapSize = 1;
    	}
    	
    	
    	boolean anormal = false;
    	Map<String, Integer> newFrequences = new HashMap<>();
    	for(String phrase : keyPhrases)
    	{
    		if(freqKeyPhrases.containsKey(phrase))
    			if(freqKeyPhrases.get(phrase) <= freq)
    				newFrequences.put(phrase, freqKeyPhrases.get(phrase));
    	}
    	if(newFrequences.size() > mapSize) //possível de ser mudado
    		anormal = true;
    	
    	return anormal;
    }
    
    
    @SuppressWarnings("unused")
	private static Set<String> populacaoKeyPhrases(List<Relatorio> relatorios, Set<String> keyPhrases, NumIteracoesAPI num )
    {
    	int length = 0;
    	int i = 0;
    	Documents documents = new Documents();
    	try 
    	{
	    	for(Relatorio r:relatorios) 
	    	{
	    		if(num.getIteracoes() < 598)
	    		{
		    		num.incIteracoes();
		        	String re = r.relatoriosParaTextMining();
		        	if(re.length() +  length > 4999)
		        	{
		        		String json = prettify(GetKeyPhrases (documents));
		                PrintWriter writer = new PrintWriter(FICHEIRO_KEYPHRASES, "UTF-8");
		                writer.print(json);
		                writer.close();
		                
		                //adicionar as keyphrases do ficheiro json ao set
		                keyPhrases = readJson(FICHEIRO_KEYPHRASES, keyPhrases);
		                
		                documents = new Documents();
		        		documents.add(i++ + "", "pt", re);
		        		length = re.length();
		        	}
		        	else
		        	{
		        		documents.add(i++ + "", "pt", re);
		        		length += re.length();
		        	}
	    		}
	        	
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return keyPhrases;
    }
    
    private static void preencheValoresTreino(Set<Relatorio> relatorios, String tipoRelatorio,  List<String> frases, Set<String> keyPhrases, Map<String, Integer> freqKeyPhrases, List<Relatorio> normaisCit, List<Relatorio> anormaisCit, List<Relatorio> normaisHist, List<Relatorio> anormaisHist)
    {

    	for(Relatorio report: relatorios) 
    	{    
            frases = populateData(frases, report.getConclus10().split("\\."), report.getConclus20().split("\\."), report.getNotas().split("\\."), report.getInforMed().split("\\.") );
            
            
            if(relatorioAnormal(freqKeyPhrases, getKeyPhrasesFicheiroTeste(report, keyPhrases), tipoRelatorio))
            {
            	if("h".equals(tipoRelatorio))
            		anormaisHist.add(report);
            	else
            		anormaisCit.add(report);
            }
            else
            {
            	
            	if("h".equals(tipoRelatorio))
            	{
            		if(report.getConclus10().contains("positiv") || report.getInforMed().contains("BEZT"))
            			anormaisHist.add(report);
            		else
            			normaisHist.add(report);
            	}
            	else
            	{
            		if(report.getConclus10().contains("carcinoma"))
            			anormaisCit.add(report);
            		else
            			normaisCit.add(report);
            	}
            		
            }
            
            for(String phrase: keyPhrases) {
                if (reportCheckKeyPhrase(report, phrase)) 
                	if(freqKeyPhrases.containsKey(phrase))
                	{
                		int valor =  freqKeyPhrases.get(phrase);
                		freqKeyPhrases.put(phrase, valor+1);
                	}
            }
    	}    
    }
    
    
    public static void preencheCasos(List<Relatorio> casos, List<String>frases, Set<String>keyPhrases, Map<String, Integer>freqKeyPhrases)
    {
    	List<String> reportLine;
    	for(Relatorio report: casos) {
            reportLine = new ArrayList<>();
            
            frases = populateData(frases, report.getConclus10().split("\\."), report.getConclus20().split("\\."), report.getNotas().split("\\."), report.getInforMed().split("\\.") );
            
            for(String phrase: keyPhrases) {
                if (reportCheckKeyPhrase(report, phrase)) 
                {
                	reportLine.add("t");
                    
                } else {
                    reportLine.add("f");
                }
            }
            String tipoRelatorio = "c";
            if(!report.isRelCitologico())
            	tipoRelatorio = "h";
            reportLine.add(tipoRelatorio);
            reportLine = assignTipoRegisto(report, reportLine);
            
            writeReportLine(reportLine);
        }
    }
    

    public static void inicializaDados() 
    {
    	Set<String> keyPhrases = new TreeSet<>();
        List<String> frases = new ArrayList<>();
    	
    	Set<Relatorio> relatoriosHistologicos = getRelatorios(FICHEIRO_HISTOLOGIAS);
        
        Set<Relatorio> relatoriosCitologicos = getRelatorios(FICHEIRO_CITOLOGIAS);
      
        try {
            

            
            /*
             * DESCOMENTAR A PARTE DE BAIXO SE SURGIREM NOVOS RELATÓRIOS.
             * ISTO ESTÁ EM COMENTÁRIO, POIS NÃO É POSSÍVEL MAIS DE 600
             * REQUESTS POR 10 MINUTOS E O FICHEIRO É SEMPRE O MESMO
             */
            /*
            NumIteracoesAPI num = new NumIteracoesAPI();
            keyPhrases = populacaoKeyPhrases(relatoriosHistologicos, keyPhrases, num);
            keyPhrases = populacaoKeyPhrases(relatoriosCitologicos, keyPhrases, num);
            
            //escrever o keyPhrases num ficheiro
            ReadKeyPhrases.initFileWriter(FICHEIRO_KEYPHRASES_CSV);
            ReadKeyPhrases.writeCSV(keyPhrases);
            ReadKeyPhrases.closeFileWriter();

            */
            keyPhrases = carregaCSV(FICHEIRO_KEYPHRASES_CSV);

            /*Preenchimento ficheiro treino
            initFileWriter(FICHEIRO_TREINO);
            arffHeader(keyPhrases);*/
            
            //remoção de palavras mal identificadas
            keyPhrases.remove("us");
            keyPhrases.remove("on");
            
            Map<String, Integer> freqKeyPhrases = new HashMap<>();
            for(String p : keyPhrases)
            	freqKeyPhrases.put(p, 0);
            
            List<Relatorio> normaisCit = new ArrayList<>();
            List<Relatorio> anormaisCit = new ArrayList<>();
            List<Relatorio> normaisHist = new ArrayList<>();
            List<Relatorio> anormaisHist = new ArrayList<>();
            preencheValoresTreino(relatoriosHistologicos, "h", frases, keyPhrases, freqKeyPhrases, normaisCit, anormaisCit, normaisHist, anormaisHist);
            preencheValoresTreino(relatoriosCitologicos, "c", frases, keyPhrases, freqKeyPhrases, normaisCit, anormaisCit, normaisHist, anormaisHist);
            //arffEnd();
            //closeFileWriter();

            
            initFileWriter(FICHEIRO_CASOS_NORMAIS_CITOLOGICOS);
            arffHeader(keyPhrases);
            preencheCasos(normaisCit, frases, keyPhrases, freqKeyPhrases);
            arffEnd();
            closeFileWriter();
            
            initFileWriter(FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS);
            arffHeader(keyPhrases);
            preencheCasos(anormaisCit, frases, keyPhrases, freqKeyPhrases);
            arffEnd();
            closeFileWriter();
            
            initFileWriter(FICHEIRO_CASOS_NORMAIS_HISTOLOGICOS);
            arffHeader(keyPhrases);
            preencheCasos(normaisHist, frases, keyPhrases, freqKeyPhrases);
            arffEnd();
            closeFileWriter();
            
            initFileWriter(FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS);
            arffHeader(keyPhrases);
            preencheCasos(anormaisHist, frases, keyPhrases, freqKeyPhrases);
            arffEnd();
            closeFileWriter();
            
            
            //preencheCasosNormaisAnormais(casosNormais, casosAnormais, freqKeyPhrases, keyPhrases, FICHEIRO_TREINO);
            //Preenchimento das frequências de cada atributo
            initFileWriter(FICHEIRO_FREQUENCIA);
            writeFrequencia(freqKeyPhrases);
            closeFileWriter();
            
            //Preenchimento do ficheiro com todas as frases dos relatórios
            initFileWriter(FICHEIRO_FRASES);
            frases.remove('\n');
            writeJSData(frases);
            closeFileWriter();

            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
