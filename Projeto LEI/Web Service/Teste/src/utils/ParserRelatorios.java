package utils;


import static utils.OpsFicheiro.readFileToString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import textminer.Relatorio;
public class ParserRelatorios {
    
    
    
    public static Relatorio parseTxtToRelatorio(String rel) {
        Relatorio relatorio = new Relatorio();
 
        String[] comps = rel/*.replace("\"", "").replace("&quot;", " ").*/.split("###");
        
        for(String componente : comps) {
            if(!componente.equals("\n")) {
                String[] aux = componente.split("->");
                String[] barrasN;
                String tratado;
                if(aux.length > 1) {
                    switch(aux[0]) {
                        case "REGONCO": 
                        				if(aux[1].toLowerCase().equals("True".toLowerCase())) 
                        				{
                        					
                                            relatorio.setRegOncologico(true);
                                        }
                                        else
                                        {
                                        	
                                            relatorio.setRegOncologico(false);
                                        }
                                        break;
                        case "RESULCRIT": if(aux[1].toLowerCase().equals("True".toLowerCase()))
                                            relatorio.setRegCritico(true);
                                          else
                                            relatorio.setRegCritico(false);
                                          break;
                        case "INFORMED10" : barrasN = aux[1].split("\r\n");
					    					tratado = "";
					    					for(int i = 0; i < barrasN.length; i++)
					    						tratado = tratado + barrasN[i];
					    					
                        					relatorio.setInforMed(tratado);
                                            break;
                        case "CONCLUS10" : barrasN = aux[1].split("\r\n");
					    					tratado = "";
					    					for(int i = 0; i < barrasN.length; i++)
					    						tratado = tratado + barrasN[i];
    					
    										relatorio.setConclus10(tratado);
                                           break;
                        case "CONCLUS20" : barrasN = aux[1].split("\r\n");
					    					tratado = "";
					    					for(int i = 0; i < barrasN.length; i++)
					    						tratado = tratado + barrasN[i];
					    					
                        					relatorio.setConclus20(tratado);
                                           break;
                        case "NOTAS10" :barrasN = aux[1].split("\r\n");
				    					tratado = "";
				    					for(int i = 0; i < barrasN.length; i++)
				    						tratado = tratado + barrasN[i];
				    					
                        				relatorio.setNotas(tratado);
                                         break;
                        case "CITOLOGICO": if(aux[1].equals("True".toLowerCase())) {
                            					relatorio.setRelCitologico(true);}
                        					else
                        						relatorio.setRelCitologico(false);
                        					break;
                        default: break;
                    }
                }
            }
        }
        return relatorio;
    }
    
    public static Set<Relatorio> getRelatorios(String filePath) {
        Set<Relatorio> lista = new HashSet<>();
        String relatorioTxt = readFileToString(filePath);
        String[] relatoriosMaisUm = relatorioTxt.split("-----\n");
        String[] relatorios = Arrays.copyOf(relatoriosMaisUm, relatoriosMaisUm.length);
        for(String rel : relatorios)
            lista.add(parseTxtToRelatorio(rel));
        return lista;
    }
}
