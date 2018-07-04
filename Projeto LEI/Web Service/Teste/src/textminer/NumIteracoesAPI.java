package textminer;

public class NumIteracoesAPI{
	private int iteracoesAPI;
	
	public NumIteracoesAPI(){iteracoesAPI = 0;}
	
	public void incIteracoes(){
		iteracoesAPI++;
	}
	
	public int getIteracoes(){return iteracoesAPI;}
	
}