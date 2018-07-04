package textminer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List; 

import static textminer.APIMicrosoft.path;
import static utils.OpsFicheiro.removeLine;
import static utils.Writer.closeFileWriter;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class Weka 
{
    
    
    //TESTE NESTE CASO SÃO OS CASOS A ADIVINHAR
    public static String processaEmWeka(String treino, String teste)
    {
    	List<String> linhasApagar, linhasAdicionar;
        linhasApagar = new ArrayList<>();
        linhasAdicionar = new ArrayList<>();
        BufferedReader reader = null;
        Instances dataTreino, dataTeste;
        String res = "";
        try 
        {
        	Classifier model = null;
        	
            reader = new BufferedReader(new FileReader(new File(teste)));
            dataTeste = new Instances(reader);
            dataTeste.setClassIndex(dataTeste.numAttributes() - 1);
            reader.close();
            reader = new BufferedReader(new FileReader(new File(treino)));
            dataTreino = new Instances(reader);
            dataTreino.setClassIndex(dataTreino.numAttributes() - 1);
            reader.close();
            
            //if(FICHEIRO_CASOS_NORMAIS_CITOLOGICOS.equals(treino) || FICHEIRO_CASOS_ANORMAIS_CITOLOGICOS.equals(treino))
            //{
            	//j48, cross validation, default options
            	model = new J48();
            //}
            
            //else /*if(FICHEIRO_CASOS_NORMAIS_HISTOLOGICOS.equals(treino) || FICHEIRO_CASOS_ANORMAIS_HISTOLOGICOS.equals(treino))*/
            //{
            	//naive bayes, cross validation, default options
            //	model = new NaiveBayes();
            //}
            /*
            String[] optionsJ48 = new String[4];
            optionsJ48[0] = "-C";
            optionsJ48[1] = " 0.25";
            optionsJ48[2] = "-M 2";
            optionsJ48[3] = "-A";
            model = new J48();
            model.setOptions(optionsJ48);*/
            model.buildClassifier(dataTreino);
            
            
            for (int i = 0; i < dataTeste.numInstances(); i++) 
            {
                Instance instance = dataTeste.instance(i);
                String linha = instance.toString();
                double clsLabel = model.classifyInstance(instance);
                instance.setClassValue(clsLabel);
                
                //double [] array = tree.distributionForInstance(instance);
                linhasApagar.add(linha);
                res = dataTeste.classAttribute().value((int) clsLabel);
                String nova = linha.replace("?", res);
                linhasAdicionar.add(nova);
                
                
               /*
            	Instance inst = dataTeste.instance(i);
            	double actualClassValue =dataTeste.instance(i).classValue();
            	String actual = dataTeste.classAttribute().value((int) actualClassValue);
            	double result = tree.classifyInstance(inst);
            	res = dataTeste.classAttribute().value((int) result);*/
                transfereConhecimento(treino, teste, linhasApagar, linhasAdicionar);
            }
            
        } catch (Exception ex) {
            
        }
        
        return res;
    }
    
    public static void transfereConhecimento(String treino, String teste, List<String> linhasApagar, List<String>linhasAdicionar)
    {
        /* Apagar a parte final do arff de treino
        for(int i = 0; i < 3; i++) removeLine("%", treino);
            
        
        for(int i = 0; i < linhasApagar.size(); i++) {
            try {
                /* Adicionar a linha ao treino 
                Files.write(Paths.get(treino), linhasAdicionar.get(i).getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get(treino), "\n%\n%\n%\n".getBytes(), StandardOpenOption.APPEND);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
		*/
        closeFileWriter();
        
        /* Elimina-se o ficheiro de teste */ 
        try {
            Path path;
            path = Paths.get(teste);
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
      
    }
    
    
    
}
