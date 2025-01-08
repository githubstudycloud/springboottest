public class PageHelper {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageParams {
        private Integer pageSize;
        private Integer pageNum;
        private Long total;
        private Object extraParams;  // 额外参数，用于业务定制
        
        public static PageParams of(int pageSize, int pageNum) {
            return PageParams.builder()
                    .pageSize(pageSize)
                    .pageNum(pageNum)
                    .build();
        }
    }
    
    @Data
    @Builder
    public static class PageResult<T> {
        private List<T> records;
        private Long total;
        private Integer pages;
        private Integer pageSize;
        private Integer pageNum;
        private Object extraData;  // 额外数据，用于业务定制
        
        public static <T> PageResult<T> of(List<T> records, PageParams pageParams) {
            long total = pageParams.getTotal() != null ? pageParams.getTotal() : 0;
            int pages = (int) ((total + pageParams.getPageSize() - 1) / pageParams.getPageSize());
            
            return PageResult.<T>builder()
                    .records(records)
                    .total(total)
                    .pages(pages)
                    .pageSize(pageParams.getPageSize())
                    .pageNum(pageParams.getPageNum())
                    .build();
        }
    }

    public static List<PageParams> splitPages(long total, int pageSize) {
        int pages = (int) ((total + pageSize - 1) / pageSize);
        return IntStream.rangeClosed(1, pages)
                .mapToObj(pageNum -> PageParams.of(pageSize, pageNum))
                .collect(Collectors.toList());
    }
}
