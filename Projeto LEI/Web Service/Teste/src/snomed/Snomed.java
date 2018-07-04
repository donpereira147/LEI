package snomed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import db.Connect;

public class Snomed 
{
	public static List<String> getListaPalavrasTradutoras()
	{
		List<String> tradutoras = new ArrayList<>();
    	tradutoras.add("carcinoma");
    	tradutoras.add("adenocarcinoma");
    	tradutoras.add("broncoscopia");
    	tradutoras.add("lobectomia");
    	tradutoras.add("citologia");
    	tradutoras.add("gastrectomia");
    	tradutoras.add("neoplasia");
    	tradutoras.add("tiroidectomia");
    	tradutoras.add("histologia");
    	tradutoras.add("anastomose");
    	
    	return tradutoras;
	}
	
	
	public static Set<String> getListaPalavrasNoRelatorio(String relatorio, List<String> tradutoras)
	{
		Set<String> traduzidas = new HashSet<>();
		
		for(String palavra : tradutoras)
			if(relatorio.contains(palavra))
			{
				String nova = "";
				switch(palavra)
				{
					case "carcinoma": nova = "carcinoma";
									break;
					case "adenocarcinoma": nova = "adenocarcinoma";
									break;
					case "broncoscopia": nova = "bronchoscopy";
									break;
					case "lobectomia": nova = "lobectomy";
									break;
					case "citologia": nova = "citology";
									break;
					case "gastrectomia": nova = "gastrectomy";
									break;
					case "neoplasia": nova = "neoplasia";
									break;
					case "tiroidectomia": nova = "thyroidectomy";
									break;
					case "histologia": nova = "histology";
									break;
					case "anastomose": nova = "anastomosis";
									break;
				}
				traduzidas.add(nova);
			}		
		
		return traduzidas;
	}
	
	public static String getListaCodigosSnomed(Set<String> palavrasTraduzidas)
	{
		String codigos = "";
		String codigosNovo = "";
		Set<Integer> codigosSnomed = new HashSet<>();
		Connection conn;
		try
		{
			 conn = Connect.connect();
			 
			 PreparedStatement ps;
			 
			 for(String pal : palavrasTraduzidas)
			 {
				 ps  = conn.prepareStatement("select conceptId from description where term like ?");
				 ps.setString(1, pal);
				 ResultSet rs = ps.executeQuery();
				 
				 while(rs.next())
				 {
					 int n = rs.getInt(1);
					 System.out.println(n);
					 codigosSnomed.add(n);
				 }
			 }
			 
			 
			 for(Integer cod : codigosSnomed)
			 {
				 codigos += cod + ",";
			 }
			 if(!codigosSnomed.isEmpty())
				 codigosNovo = codigos.substring(0, codigos.length()-1);
			 else
				 codigosNovo = codigos;
				 
				 
			 conn.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return codigosNovo;
	}
}
