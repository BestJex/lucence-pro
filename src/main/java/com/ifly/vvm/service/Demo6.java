package com.ifly.vvm.service;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

public class Demo6 {
    private int ids[]={1,2,3};
    private String citys[]={"qingdao","nanjing","shanghai"};
    private String descs[]={
            "Qingdao is a beautiful city.",
            "Nanjing is a city of culture.",
            "Shanghai is a bustling city."
    };
    private FSDirectory dir;
    
    /**
     * 每次都生成索引文件
     * @throws Exception
     */
    //@Before
    public void setUp() throws Exception {
        dir  = FSDirectory.open(Paths.get("D:\\usr\\local\\vvm\\lucence\\demo6"));
        IndexWriter indexWriter = getIndexWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new IntField("id", ids[i], Store.YES));
            doc.add(new StringField("city", citys[i], Store.YES));
            doc.add(new TextField("desc", descs[i], Store.YES));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }
    
    /**
     * 获取索引输出流
     * @return
     * @throws Exception
     */
    private IndexWriter getIndexWriter()  throws Exception{
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        return new IndexWriter(dir, conf );
    }
    
    /**
     * 指定数字范围查询
     * @throws Exception
     */
    @Test
    public void testNumericRangeQuery()throws Exception{
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        
        NumericRangeQuery<Integer> query=NumericRangeQuery.newIntRange("id", 1, 2, true, true);
        TopDocs hits=is.search(query, 10);
        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("city"));
            System.out.println(doc.get("desc"));
        }        
    }
    
    /**
     * 指定字符串开头字母查询（prefixQuery）
     * @throws Exception
     */
    @Test
    public void testPrefixQuery()throws Exception{
        dir = FSDirectory.open(Paths.get("D:\\usr\\local\\vvm\\lucence\\demo6"));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        
        PrefixQuery query=new PrefixQuery(new Term("city","n"));
        TopDocs hits=is.search(query, 10);
        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("city"));
            System.out.println(doc.get("desc"));
        }    
    }
    
    @Test
    public void testBooleanQuery()throws Exception{
        dir = FSDirectory.open(Paths.get("D:\\usr\\local\\vvm\\lucence\\demo6"));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        
        NumericRangeQuery<Integer> query1=NumericRangeQuery.newIntRange("id", 1, 2, true, true);
        PrefixQuery query2=new PrefixQuery(new Term("city","n"));
        BooleanQuery.Builder booleanQuery=new BooleanQuery.Builder();
        booleanQuery.add(query1,Occur.MUST);
        booleanQuery.add(query2,Occur.MUST);
        TopDocs hits=is.search(booleanQuery.build(), 10);
        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document doc=is.doc(scoreDoc.doc);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("city"));
            System.out.println(doc.get("desc"));
        }    
    }
}
