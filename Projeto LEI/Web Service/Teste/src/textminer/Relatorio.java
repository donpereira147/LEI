package textminer;

public class Relatorio 
{
    private boolean regOncologico, regCritico, relCitologico;
    private String inforMed, conclus10, conclus20, notas;
    
    public Relatorio() {
        regOncologico = true;
        regCritico = true;
        relCitologico = false;
        inforMed = "";
        conclus10 = "";
        conclus20 = "";
        notas = "";
    }
    
    public Relatorio(boolean onc, boolean crit, String med, String c10, String c20, String n) {
        regOncologico = onc;
        regCritico = crit;
        inforMed = med;
        conclus10 = c10;
        conclus20 = c20;
        notas = n;
    }
    
    public void setRegOncologico(boolean reg) {
        regOncologico = reg;
    }

    public void setRegCritico(boolean reg) {
        regCritico = reg;
    }
    
    public void setRelCitologico(boolean reg)
    {
    	relCitologico = reg;
    }
    
    public void setInforMed(String med) {
        inforMed = med;
    }

    public void setConclus10(String con) {
        conclus10 = con;
    }

    public void setConclus20(String con) {
        conclus20 = con;
    }

    public void setNotas(String no) {
        notas = no;
    }
    
    public boolean isRelCitologico() {
        return relCitologico;
    }
    
    public boolean isRegOncologico() {
        return regOncologico;
    }

    public boolean isRegCritico() {
        return regCritico;
    }

    public String getInforMed() {
        return inforMed;
    }

    public String getConclus10() {
        return conclus10;
    }

    public String getConclus20() {
        return conclus20;
    }

    public String getNotas() {
        return notas;
    }
    
    public String relatoriosParaTextMining() {
        StringBuilder sb = new StringBuilder();
        if(!"".equals(inforMed))
            sb.append(inforMed).append("\n");
        if(!"".equals(conclus10))
            sb.append(conclus10).append("\n");
        if(!"".equals(conclus20))
            sb.append(conclus20).append("\n");
        if(!"".equals(notas))
            sb.append(notas).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Registo Oncológico: ").append(regOncologico).append("\n");
        sb.append("Registo Crítico: ").append(regCritico).append("\n");
        sb.append("Informação Médica: ").append(inforMed).append("\n");
        sb.append("Conclusões 10: ").append(conclus10).append("\n");
        sb.append("Conclusões 20: ").append(conclus20).append("\n");
        sb.append("Notas: ").append(notas).append("\n");
        return sb.toString();
    }
    
}
