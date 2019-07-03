package com.nowcoder.service;

import com.nowcoder.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * @author JiafengLiu
 * @date 2019/7/1 21:43
 * Email jfliu_2017@stu.xidian.edu.cn
 */
@Service
public class SearchService {
	//建立索引，查询
	private static final String SOLR_URL = "http://127.0.0.1:8983/solr/wenda";
	private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
	private static final String QUESTION_TITLE_FIELD = "question_title";
	private static final String QUESTION_CONTENT_FIELD = "question_content";

	public List<Question> searchQuestion(String keyword, int offset, int count,
										 String hlPre, String hlPos) throws Exception{
		List<Question> questionList = new ArrayList<>();
		SolrQuery query = new SolrQuery(keyword);
		query.setRows(count);
		query.setStart(offset);
		query.setHighlight(true);
		query.setHighlightSimplePre(hlPre);
		query.setHighlightSimplePost(hlPos);
		query.set("hl.fl",QUESTION_CONTENT_FIELD+","+QUESTION_TITLE_FIELD);
		query.set("df", QUESTION_CONTENT_FIELD);
		QueryResponse response = client.query(query);
		for(Map.Entry<String, Map<String, List<String>>> entry:response.getHighlighting().entrySet()){
			Question q = new Question();
			q.setId(Integer.parseInt(entry.getKey()));
			if(entry.getValue().containsKey(QUESTION_CONTENT_FIELD)){
				List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
				if(contentList.size()>0){
					q.setContent(contentList.get(0));
				}
			}
			if(entry.getValue().containsKey(QUESTION_TITLE_FIELD)){
				List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
				if(titleList.size()>0){
					q.setTitle(titleList.get(0));
				}
			}
			questionList.add(q);
		}
		return questionList;
	}

	public boolean indexQuestion(int qid, String title, String content) throws Exception{
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField("id", qid);
		doc.setField(QUESTION_CONTENT_FIELD,content);
		doc.setField(QUESTION_TITLE_FIELD,title);
		UpdateResponse response = client.add(doc,1000);//在1000毫秒内直接返回
		return response != null && response.getStatus() == 0;
	}
}
