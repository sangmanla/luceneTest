package luceneTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

public class LuceneIndexer {

    // Singleton
    private LuceneIndexer() {
    }

    public static LuceneIndexer getInstance() {
	return Holder.instance;
    }

    private static class Holder {
	private static LuceneIndexer instance = new LuceneIndexer();
    }
    
    public static String col1 = "col1";
    public static String col2 = "col2";

    // Instance variables
    private RAMDirectory ram = new RAMDirectory();
    private StandardAnalyzer analyzer = new StandardAnalyzer();

    // insert data
    public LuceneIndexer insert(String title, String body) throws IOException {
	IndexWriter writter = new IndexWriter(ram, new IndexWriterConfig(analyzer));

	writter.addDocument(
		new MyDocument()
		.add(new SortedDocValuesField(col1, new BytesRef(title)))
		.add(new SortedDocValuesField(col2, new BytesRef(body)))
		.add(new TextField(col1, title, Field.Store.YES))
		.add(new TextField(col2, body, Field.Store.YES))
		.getDoc());
	writter.close();
	
	return this;
    }

    // search by field name and querystring
    public List<Document> search(String fieldName, String queryString) throws ParseException, IOException {
	return search(new QueryParser(fieldName, analyzer).parse(queryString));
    }

    // search by only query
    public List<Document> search(Query query) throws ParseException, IOException {
	return search(query, null);
    }

    // remote data
    public void delete(Query query) throws IOException {
	IndexWriter writter = new IndexWriter(ram, new IndexWriterConfig(analyzer));
	writter.deleteDocuments(query);
	writter.close();
    }

    // search by query and Sort
    public List<Document> search(Query query, Sort sortby) throws ParseException, IOException {
	IndexReader reader = DirectoryReader.open(ram);
	IndexSearcher searcher = new IndexSearcher(reader);
	TopDocs topDocs = (sortby == null ? searcher.search(query, 10) : searcher.search(query, 10, sortby));

	List<Document> documents = new ArrayList<Document>();
	for (ScoreDoc hit : topDocs.scoreDocs) {
	    documents.add(searcher.doc(hit.doc));
	}
	
	return documents;
    }
}
