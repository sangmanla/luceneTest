package luceneTest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.junit.Test;

public class LuceneTestClass {
    @Test
    public void searchByFieldNameAndKeyword() throws IOException, ParseException {
	String title = "Simple Search Test";
	String body = "This is a simple test for searching word on lucene library.";
	
	LuceneIndexer indexer = LuceneIndexer.getInstance();
	indexer.insert(title, body);
	
	List<Document> documents = indexer.search(LuceneIndexer.col2, "searching");
	assertEquals(documents.size(), 1);
	assertEquals(title, documents.get(0).get(LuceneIndexer.col1));
    }

    @Test
    public void searchByTerm() throws IOException, ParseException {
	LuceneIndexer indexer = LuceneIndexer.getInstance();
	indexer.insert("Math", "study about number")
		.insert("English", "study about language")
		.insert("Physics", "study about science")
		.insert("Gym Class", "utilize body");

	List<Document> documents = indexer.search(new TermQuery(new Term(LuceneIndexer.col2, "study")));
	assertEquals(3, documents.size());
    }
    
    @Test
    public void searchbyPrefixQuery() throws IOException, ParseException {
	LuceneIndexer indexer = LuceneIndexer.getInstance();
	indexer.insert("Centennial", "College at Toronto")
        	.insert("Seneca", "College at Toronto")
        	.insert("UoT", "University at Toronto")
		.insert("Chung Ang University", "University at Seoul");
     
        List<Document> documents = indexer.search(new PrefixQuery(new Term(LuceneIndexer.col2, "toronto")));
        assertEquals(3, documents.size());
    }
    
    @Test
    public void searchByUsingWildcard() throws IOException, ParseException {
	LuceneIndexer indexer = LuceneIndexer.getInstance();
	indexer.insert("Food", "Korean BBQ")
        	.insert("Food", "Tiramisu cake")
        	.insert("Food", "Kelloxx Cornflake")
        	.insert("Drinks", "Coxx Cola")
        	.insert("Drinks", "Timhoxx coffee")
        	.insert("Drinks", "Starbuxx coffee");
     
        List<Document> documents = indexer.search(new WildcardQuery(new Term(LuceneIndexer.col2, "*offe*")));
        assertEquals(2, documents.size());
    }
    
    @Test
    public void searchAndSort() throws IOException, ParseException {
	LuceneIndexer indexer = LuceneIndexer.getInstance();
	indexer.insert("title1", "no content 1")
        	.insert("title2", "no content 2")
        	.insert("title3", "no content 3")
        	.insert("title4", "content is here - 1")
        	.insert("title5", "content is here - 2")
        	.insert("title6", "no content 6");
	
        List<Document> documents = indexer.search(
                	new TermQuery(new Term(LuceneIndexer.col2, "here")), 
                	new Sort(new SortField(LuceneIndexer.col2, SortField.Type.STRING_VAL, false))
        	);
        assertEquals(2, documents.size());
        assertEquals("title5", documents.get(1).getField(LuceneIndexer.col1).stringValue());
    }
    
    @Test
    public void deleteTest() throws IOException, ParseException {
	LuceneIndexer indexer = LuceneIndexer.getInstance();
        indexer.insert("thingToDelete1", "I will be removed.")
        	.insert("thingToDelete2", "Same here.");
     
        Query query = new WildcardQuery(new Term(LuceneIndexer.col1, "*delete*"));
        assertEquals(2, indexer.search(query).size());;
        
        indexer.delete(query);
     
        assertEquals(0, indexer.search(query).size());;
    }
}
