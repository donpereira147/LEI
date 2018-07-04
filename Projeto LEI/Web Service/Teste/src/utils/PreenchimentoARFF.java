package utils;

import static utils.CSVUtils.producesLine;
import static utils.Caminhos.FICHEIRO_KEYPHRASES_CSV;
import static utils.Caminhos.FICHEIRO_TESTE;
import static utils.OpsFicheiro.removeLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import textminer.Relatorio;

import static utils.CSVUtils.carregaCSV;;


public class PreenchimentoARFF 
{
	public static boolean reportCheckKeyPhrase(Relatorio report, String keyPhrase) {
        boolean check = false;
        String phrase = keyPhrase.toLowerCase();
        String conclus10 = report.getConclus10().toLowerCase();
        String conclus20 = report.getConclus20().toLowerCase();
        String informed = report.getInforMed().toLowerCase();
        String notas = report.getNotas().toLowerCase();
        
        if(conclus10.contains(phrase) || conclus20.contains(phrase) || informed.contains(phrase)
           || notas.contains(phrase)) {
            check = true;
        }
        
        return check;
    }
    
    public static List<String> assignTipoRegisto(Relatorio report, List<String> reportLine) {
        /*
            ff -> REGONCO = F && RESULCRIT = F
            tf -> REGONCO = T && RESULCRIT = F
            ft -> REGONCO = F && RESULCRIT = T
            tt -> REGONCO = T && RESULCRIT = T
        */
        
        boolean regonco = report.isRegOncologico();
        boolean resulcrit = report.isRegCritico();
        
        if(!regonco && !resulcrit) {
            reportLine.add("ff");
        } else if (regonco && !resulcrit) {
                reportLine.add("tf");
            } else if (!regonco && resulcrit){
                    reportLine.add("ft");
                } else if (regonco && resulcrit) {
                        reportLine.add("tt");
                    }
        
        return reportLine;
    }
    
    public static List<String> preencheLinhaAteResultados(String file, Relatorio rela, Set<String> keyPhrases)
    {
    	
    	List<String> reportLine = new ArrayList<>();

        for(String phrase: keyPhrases) {
        	if (reportCheckKeyPhrase(rela, phrase)) {
        			reportLine.add("t");
        	} else {
        			reportLine.add("f");
        	}
        }
        if(rela.isRelCitologico())
        	reportLine.add("c");
        else
        	reportLine.add("h");
        
        return reportLine;
 
    }
    
    
    public static void concatenaTreinoArff(Relatorio rela, String treino)
    {    	
    	Set<String> keyPhrases = new TreeSet<>();
    	keyPhrases = carregaCSV(FICHEIRO_KEYPHRASES_CSV);
    	List<String> reportLine = preencheLinhaAteResultados(treino, rela, keyPhrases);
    	reportLine = assignTipoRegisto(rela, reportLine);
        
        ArrayList<String> attributes = new ArrayList<>();
        attributes.addAll(reportLine);
    	for(int i = 0; i < 3; i++) 
        	removeLine("%", treino);
            
        String linha;
		try {
			linha = producesLine(attributes, ',', ' ');
			Files.write(Paths.get(treino), linha.getBytes(), StandardOpenOption.APPEND);
			Files.write(Paths.get(treino), "\n%\n%\n%\n".getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void concatenaTesteArff(Relatorio rela)
    {
    	String teste = FICHEIRO_TESTE;
    	Set<String> keyPhrases = new TreeSet<>();
    	keyPhrases = carregaCSV(FICHEIRO_KEYPHRASES_CSV);
    	List<String> reportLine = preencheLinhaAteResultados(teste, rela, keyPhrases);
    	reportLine.add("?");
        
        ArrayList<String> attributes = new ArrayList<>();
        attributes.addAll(reportLine);
        
        String linha;
		try {
			linha = producesLine(attributes, ',', ' ');
			Files.write(Paths.get(teste), linha.getBytes(), StandardOpenOption.APPEND);
			Files.write(Paths.get(teste), "\n%\n%\n%\n".getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    
    
    public static List<String> getKeyPhrasesFicheiroTeste(Relatorio rela, Set<String> keyPhrases)
    {
    	
    	List<String> reportLine = new ArrayList<>();

        for(String phrase: keyPhrases) {
        	if (reportCheckKeyPhrase(rela, phrase)) {
        			reportLine.add(phrase);
        	} 
        }
        if(rela.isRelCitologico())
        	reportLine.add("c");
        else
        	reportLine.add("h");
        
        return reportLine;
 
    }
}
