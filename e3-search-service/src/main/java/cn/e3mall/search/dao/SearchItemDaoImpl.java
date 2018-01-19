package cn.e3mall.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

@Repository
public class SearchItemDaoImpl {
@Autowired
private SolrServer solrServer;

public SearchResult getSerarchResult(SolrQuery query) throws SolrServerException{
	//根据查询对象执行查询 返回QueryResponse对象
                QueryResponse response= solrServer.query(query);	
                //获得结果集
                SolrDocumentList results = response.getResults();
                //获得数量
                long numFound = results.getNumFound();
                //创建SearchResult对象
                SearchResult searchResult=new SearchResult();
                //向searchResult中添加总数量
                searchResult.setTotalCount((int)numFound);
                //创建一个SearchItem集合
                List<SearchItem> list=new ArrayList<>();
              
                //取——————高亮————————结果集:
 Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
for (SolrDocument solrDocument : results) {
	SearchItem searchItem = new SearchItem();
	searchItem.setCategory_name((String) solrDocument.get("item_category_name"));
	searchItem.setId((String) solrDocument.get("id"));
	searchItem.setImage((String) solrDocument.get("item_image"));
	searchItem.setPrice((long) solrDocument.get("item_price"));
	searchItem.setSell_point((String) solrDocument.get("item_sell_point"));
//取高亮
	List<String> list2 = highlighting.get(solrDocument.get("id")).get("item_title");
	String itemTitle="";
	if (list2!=null&&list2.size()>0) {
	itemTitle=	list2.get(0);
	}else{
		itemTitle=(String)solrDocument.get("item_title");
	}
	searchItem.setTitle(itemTitle);
	list.add(searchItem);
}
searchResult.setSearchItemList(list);
	
	return searchResult;
	
}
}
