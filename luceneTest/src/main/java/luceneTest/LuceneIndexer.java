package luceneTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneIndexer {
    RAMDirectory ramDirectory;
    StandardAnalyzer standardAnalyzer;
    Directory memoryIndex;

    public LuceneIndexer(RAMDirectory ramDirectory, StandardAnalyzer standardAnalyzer) {
	this.ramDirectory = ramDirectory;
	this.standardAnalyzer = standardAnalyzer;
    }

    public void indexDocument(String title, String body) throws IOException {
	memoryIndex = new RAMDirectory();
	StandardAnalyzer analyzer = new StandardAnalyzer();
	IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
	IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
	Document document = new Document();

	document.add(new TextField("title", title, Field.Store.YES));
	document.add(new TextField("body", body, Field.Store.YES));

	writter.addDocument(document);
	writter.close();
    }

    public List<Document> searchIndex(String inField, String queryString) throws ParseException, IOException {
	return searchIndex(new QueryParser(inField, standardAnalyzer).parse(queryString));
    }
    
    public List<Document> searchIndex(Query query) throws ParseException, IOException {
	return searchIndex(query, null);
    }
    
    public void deleteDocument(Term term) {
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(standardAnalyzer);
            IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
            writter.deleteDocuments(term);
            writter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Document> searchIndex(Query query, Sort sortby) throws ParseException, IOException {
	IndexReader indexReader = DirectoryReader.open(memoryIndex);
	IndexSearcher searcher = new IndexSearcher(indexReader);
	TopDocs topDocs = searcher.search(query, 10, sortby);
	
	List<Document> documents = new ArrayList<>();
	for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
	    documents.add(searcher.doc(scoreDoc.doc));
	}
	return documents;
    }
}
