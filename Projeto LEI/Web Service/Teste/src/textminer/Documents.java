package textminer;

import java.util.ArrayList;
import java.util.List;


public class Documents {
    public List<Document> documents;

    public Documents() {
        this.documents = new ArrayList<>();
    }
    
    public void add(String id, String language, String text) {
        this.documents.add (new Document (id, language, text));
    }
}
