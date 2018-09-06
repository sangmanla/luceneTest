package luceneTest;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

public class MyDocument{
    Document doc = new Document();
    
    public MyDocument add(IndexableField field){
	doc.add(field);
	return this;
    }
    
    public Document getDoc(){
	return doc;
    }
}