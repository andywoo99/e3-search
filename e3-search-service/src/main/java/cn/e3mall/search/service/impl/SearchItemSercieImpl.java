package cn.e3mall.search.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EmallResult;
import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchItemDaoImpl;
import cn.e3mall.search.mapper.ItemMapper;
import cn.e3mall.search.service.SearchItemService;

@Service
public class SearchItemSercieImpl implements SearchItemService {
	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private SearchItemDaoImpl searchDao; 
	@Autowired
	private SolrServer solrServer;

	@Override
	public EmallResult importAllItem() {

		try {
			List<SearchItem> list = itemMapper.getSearchItemList();
			for (SearchItem searchItem : list) {
				// 创建文档对象
				SolrInputDocument document = new SolrInputDocument();
				// 向文档对象中添加域
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				// 把文档对象写到索引库
				solrServer.add(document);

			}
			solrServer.commit();
			return EmallResult.ok();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			return EmallResult.build(500, "导入索引出错");
		}

	}

	@Override
	public SearchResult getSearchResult(int page, String keyWords, int rows) throws Exception{
//创建查询条件对象
		SolrQuery query=new SolrQuery();
//设置查询条件
		query.setQuery(keyWords);
		//设置起始点
		query.setStart((page-1)*rows);
		//设置每页大小
		query.setRows(rows);
		//设置默认查询域
		query.set("df", "item_title");
		//设置高亮显示
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		//执行查询
		SearchResult searchResult = searchDao.getSerarchResult(query);
		//获得总记录数
		int totalCount = searchResult.getTotalCount();
		int totalPage=0;
		if(totalCount%rows!=0)
		{
			totalPage=(int)(totalCount/rows)+1;
		}else{
			totalPage=totalCount/rows;
		}
		searchResult.setTotalPage(totalPage);
		
		return searchResult;
	}

	
}
