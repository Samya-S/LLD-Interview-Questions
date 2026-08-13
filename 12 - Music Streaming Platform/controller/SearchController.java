package controller;

import service.SearchService;
import service.SearchService.SearchResponse;

public class SearchController {
    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    public SearchResponse search(String query, String type) {
        return searchService.search(query, type);
    }
}
