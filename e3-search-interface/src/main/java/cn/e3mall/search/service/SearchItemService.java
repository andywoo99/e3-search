package cn.e3mall.search.service;


import cn.e3mall.common.pojo.EmallResult;
import cn.e3mall.common.pojo.SearchResult;

public interface SearchItemService {
	//从数据库导入索引
EmallResult importAllItem();
//查询索引
public SearchResult getSearchResult(int page,String keyWords,int rows) throws Exception;
}
