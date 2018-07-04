package pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


import javax.ws.rs.core.MediaType;

import textminer.Relatorio;


import static utils.Caminhos.FICHEIRO_CITOLOGIAS;
import static utils.Caminhos.FICHEIRO_HISTOLOGIAS;
import static utils.Caminhos.FICHEIRO_KEYPHRASES_CSV;
import static utils.Caminhos.FICHEIRO_TESTE;
import static utils.Caminhos.FICHEIRO_CASOS_NORMAIS_CITOLOGICOS;
import static utils.Caminhos.FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS;
import static utils.Caminhos.FICHEIRO_CASOS_NORMAIS_HISTOLOGICOS;
import static utils.Caminhos.FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS;
import static utils.Caminhos.FICHEIRO_FREQUENCIA;
import static utils.ParserRelatorios.parseTxtToRelatorio;
import static utils.PreenchimentoARFF.concatenaTesteArff;
import static utils.PreenchimentoARFF.concatenaTreinoArff;
import static utils.PreenchimentoARFF.getKeyPhrasesFicheiroTeste;
import static utils.Writer.arffHeader;
import static utils.Writer.initFileWriter;
import static utils.Writer.closeFileWriter;
import static utils.OpsFicheiro.readFrequenceToMap;
import static textminer.Weka.processaEmWeka;
import static snomed.Snomed.getListaPalavrasTradutoras;
import static snomed.Snomed.getListaPalavrasNoRelatorio;
import static snomed.Snomed.getListaCodigosSnomed;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static utils.CSVUtils.carregaCSV;
import static textminer.TextMiner.relatorioAnormal;

@Path("webservice")
public class WebService {

    public WebService() {
        
    }

    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("previsao")
    public String previsaoRelatorio(String s) 
    {
    	String resPrevisao = "";
    	//parse do relatório
    	Relatorio rela = parseTxtToRelatorio(s);
    	
    	initFileWriter(FICHEIRO_TESTE);
    	Set<String> keyPhrases = new TreeSet<>();
    	keyPhrases = carregaCSV(FICHEIRO_KEYPHRASES_CSV);
    	//iniciar o ficheiro de teste com o mesmo header do ficheiro de treino
    	arffHeader(keyPhrases);
    	concatenaTesteArff(rela);
    	closeFileWriter();
    	//previsão em WEKA
    	
    	//Pura beleza em baixo
    	String tipoRelatorio = rela.isRelCitologico() ? "c" : "h";
    	if("h".equals(tipoRelatorio))
    	{
	    	if(rela.getConclus10().contains("positiv") || rela.getInforMed().contains("BEZT"))
	    	{
	    		//processa relatório anormal histologico
	    		resPrevisao = processaEmWeka(FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS, FICHEIRO_TESTE);
	    	}
	    	else
	    	{
	    		if(relatorioAnormal(readFrequenceToMap(FICHEIRO_FREQUENCIA), getKeyPhrasesFicheiroTeste(rela, keyPhrases), tipoRelatorio))
	        	{
	    			//processa relatorio anormal histologico
	    			resPrevisao = processaEmWeka(FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS, FICHEIRO_TESTE);
	        	}
	        	else
	        	{
	        		//processa relatório normal histologico
	        		resPrevisao = processaEmWeka(FICHEIRO_CASOS_NORMAIS_HISTOLOGICOS, FICHEIRO_TESTE);
	        	}
	    	}
    	}
    	else if("c".equals(tipoRelatorio))
    	{
    		if(rela.getConclus10().contains("carcinoma"))
	    	{
	    		//processa relatório anormal citologico
    			resPrevisao = processaEmWeka(FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS, FICHEIRO_TESTE);
	    	}
	    	else
	    	{
	    		if(relatorioAnormal(readFrequenceToMap(FICHEIRO_FREQUENCIA), getKeyPhrasesFicheiroTeste(rela, keyPhrases), tipoRelatorio))
	        	{
	    			//processa relatorio anormal citologico
	    			resPrevisao = processaEmWeka(FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS, FICHEIRO_TESTE);
	        	}
	        	else
	        	{
	        		//processa relatório normal citologico
	        		resPrevisao = processaEmWeka(FICHEIRO_CASOS_NORMAIS_CITOLOGICOS, FICHEIRO_TESTE);
	        	}
	    	}
    	}
    	
    	//resPrevisao = processaEmWeka(FICHEIRO_TREINO, FICHEIRO_TESTE);
    	return resPrevisao; //tem de ter os valores da previsão
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String submissaoRelatorio(String s) 
    {
        //através da string , adicionar o relatório ao ficheiro de treino
        Relatorio r = parseTxtToRelatorio(s);
        Set<String> keyPhrases = carregaCSV(FICHEIRO_KEYPHRASES_CSV);
        if(r.isRelCitologico())
        {
        	try {
        		//adiciona ao ficheiro que cont�m todos os relatórios
                Files.write(Paths.get(FICHEIRO_CITOLOGIAS), s.getBytes(), StandardOpenOption.APPEND);
                //adiciona ao train.arff o novo relatório
                
                if(r.getConclus10().contains("carcinoma"))
    	    	{
                	concatenaTreinoArff(r, FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS);
    	    	}
    	    	else
    	    	{
    	    		if(relatorioAnormal(readFrequenceToMap(FICHEIRO_FREQUENCIA), getKeyPhrasesFicheiroTeste(r, keyPhrases), "c"))
    	        	{
    	    			concatenaTreinoArff(r, FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS);
    	        	}
    	        	else
    	        	{
    	        		concatenaTreinoArff(r, FICHEIRO_CASOS_NORMAIS_CITOLOGICOS);
    	        	}
    	    	}

                
        	} catch (IOException ex) {
                	ex.printStackTrace();
            }
        	
        }
        else
        {
        	try {
        		//adiciona ao ficheiro que cont�m todos os relatórios
                Files.write(Paths.get(FICHEIRO_HISTOLOGIAS), s.getBytes(), StandardOpenOption.APPEND);
                //adiciona ao train.arff o novo relatório
                
                
                if(r.getConclus10().contains("positiv") || r.getInforMed().contains("BEZT"))
    	    	{
                	concatenaTreinoArff(r, FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS);
    	    	}
    	    	else
    	    	{
    	    		if(relatorioAnormal(readFrequenceToMap(FICHEIRO_FREQUENCIA), getKeyPhrasesFicheiroTeste(r, keyPhrases), "h"))
    	        	{
    	    			concatenaTreinoArff(r, FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS);
    	        	}
    	        	else
    	        	{
    	        		concatenaTreinoArff(r, FICHEIRO_CASOS_NORMAIS_HISTOLOGICOS);
    	        	}
    	    	}
                
            } catch (IOException ex) {
                	ex.printStackTrace();
            }
        }
        return "funcionou e bem";
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("snomed")
    public String codigosSNOMED(String s) 
    {
    	System.out.println("Relatório:" + s);
    	List<String> tradutoras = getListaPalavrasTradutoras();
    	Set<String> palavrasTraduzidas = getListaPalavrasNoRelatorio(s, tradutoras);
    	String codigosSnomed = getListaCodigosSnomed(palavrasTraduzidas);
    	System.out.println("codigos:" + codigosSnomed);
    	return codigosSnomed;
    
    }
    

}
