package luceneTest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

public class LuceneTestClass {
    @Test
    public void givenSearchQueryWhenFetchedDocumentThenCorrect() throws IOException, ParseException {
	LuceneIndexer inMemoryLuceneIndex = new LuceneIndexer(new RAMDirectory(), new StandardAnalyzer());
	inMemoryLuceneIndex.indexDocument("Hello world", "Some hello world");
	List<Document> documents = inMemoryLuceneIndex.searchIndex("body", "world");
	assertEquals("Hello world", documents.get(0).get("title"));
    }

    @Test
    public void givenTermQueryWhenFetchedDocumentThenCorrect() throws IOException, ParseException {
	LuceneIndexer inMemoryLuceneIndex = new LuceneIndexer(new RAMDirectory(), new StandardAnalyzer());
	inMemoryLuceneIndex.indexDocument("activity", "running in track");
	inMemoryLuceneIndex.indexDocument("activity", "Cars are running on road");

	Term term = new Term("body", "running");
	Query query = new TermQuery(term);

	List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
	assertEquals(2, documents.size());
    }
    
    @Test
    public void givenPrefixQueryWhenFetchedDocumentThenCorrect() throws IOException, ParseException {
	LuceneIndexer inMemoryLuceneIndex = new LuceneIndexer(new RAMDirectory(), new StandardAnalyzer());
        inMemoryLuceneIndex.indexDocument("article", "Lucene introduction");
        inMemoryLuceneIndex.indexDocument("article", "Introduction to Lucene");
     
        Term term = new Term("body", "intro");
        Query query = new PrefixQuery(term);
     
        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        assertEquals(2, documents.size());
    }
    
    @Test
    public void givenSortFieldWhenSortedThenCorrect() throws IOException, ParseException {
	LuceneIndexer inMemoryLuceneIndex = new LuceneIndexer(new RAMDirectory(), new StandardAnalyzer());
        inMemoryLuceneIndex.indexDocument("Ganges", "River in India");
        inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia");
        inMemoryLuceneIndex.indexDocument("Amazon", "Rain forest river");
        inMemoryLuceneIndex.indexDocument("Rhine", "Belongs to Europe");
        inMemoryLuceneIndex.indexDocument("Nile", "Longest River");
     
        Term term = new Term("body", "river");
        Query query = new WildcardQuery(term);
     
        SortField sortField = new SortField("title", SortField.Type.STRING_VAL, false);
        Sort sortByTitle = new Sort(sortField);
     
        List<Document> documents = inMemoryLuceneIndex.searchIndex(query, sortByTitle);
        assertEquals(4, documents.size());
        assertEquals("Amazon", documents.get(0).getField("title").stringValue());
    }
    
    @Test
    public void whenDocumentDeletedThenCorrect() throws IOException, ParseException {
	LuceneIndexer inMemoryLuceneIndex = new LuceneIndexer(new RAMDirectory(), new StandardAnalyzer());
        inMemoryLuceneIndex.indexDocument("Ganges", "River in India");
        inMemoryLuceneIndex.indexDocument("Mekong", "This river flows in south Asia");
     
        Term term = new Term("title", "ganges");
        inMemoryLuceneIndex.deleteDocument(term);
     
        Query query = new TermQuery(term);
     
        List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
        assertEquals(0, documents.size());
    }
}
