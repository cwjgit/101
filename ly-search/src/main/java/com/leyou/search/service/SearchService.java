package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.DTO.*;
import com.leyou.search.DTO.GoodDTO;
import com.leyou.search.DTO.SearchRequest;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人 cwj
 * @创建时间 2019/9/2  20:23
 * @描述
 */
@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private ElasticsearchTemplate esTemplate;
    @Autowired
    private GoodsRepository goodsRepository;

    public Goods copySputoGoods(SpuDTO spuDTO) {
        Long spuId = spuDTO.getId();
        //根据spuId查询sku信息
        List<SkuDTO> skuDTOS = itemClient.findSkuBySpuId(spuId);
        //截取页面需要的属性
        List<Map<String, Object>> skus = skuDTOS.stream().map(skuDTO -> {
            Map<String, Object> sku = new HashMap<>();
            sku.put("id", skuDTO.getId());
            sku.put("price", skuDTO.getPrice());
            sku.put("title", skuDTO.getTitle());
            sku.put("image", StringUtils.substringBefore(skuDTO.getImages(), ","));
            return sku;
        }).collect(Collectors.toList());
        //准备all所需要的数据
            //分类名字
        List<CategoryDTO> category = itemClient.findCategoryById(spuDTO.getCategoryIds());
        String categoryName = category.stream().map(CategoryDTO::getName).collect(Collectors.joining(","));
            //品牌名
        BrandDTO brand = itemClient.findBrandById(spuDTO.getBrandId());
            //spu名字
        String all = brand.getName() + "," + categoryName + "," + spuDTO.getName();
        //准备价格集合
        Set<Long> price = skuDTOS.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
        //准备规格参数
        HashMap<String, Object> specs = new HashMap<>();
            //查找对应的规格参数
        List<SpecParamDTO> specParamDTOS = itemClient.findSpecParamByParam(null, spuDTO.getCid3(), true);
            //查找对应的spuDetail
        SpuDetailDTO spuDetailDTO = itemClient.findDetailById(spuId);
            //拿到共有属性和私有属性转换为map
        Map<Long, Object> genericSpec = JsonUtils.toMap(spuDetailDTO.getGenericSpec(), Long.class, Object.class);
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetailDTO.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            String key = specParamDTO.getName();
            Object value = null;
            //判断是否是通用属性
            if(specParamDTO.getGeneric()){
                //通用属性
                value = genericSpec.get(specParamDTO.getId());
            }else{
                //私有属性
                value = specialSpec.get(specParamDTO.getId());
            }
            //判断是否是数字类型
            if(specParamDTO.getIsNumeric()){
                // 是数字类型，分段
                value = chooseSegment(value, specParamDTO);
            }
            specs.put(key,value);
        }
        Goods goods = new Goods();
        // spuId
        goods.setId(spuId);
        // 副标题
        goods.setSubTitle(spuDTO.getSubTitle());
        // sku信息的json结构
        goods.setSkus(JsonUtils.toString(skus));
        // 所有需要被搜索的信息，包含标题，分类，甚至品牌
        goods.setAll(all);
        // 品牌id
        goods.setBrandId(spuDTO.getBrandId());
        // 商品第3级分类id
        goods.setCategoryId(spuDTO.getCid3());
        // spu创建时间
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        // sku的价格集合
        goods.setPrice(price);
        // 可搜索的规格参数，key是参数名，value是参数值
        goods.setSpecs(specs);
        return goods;
    }

    private String chooseSegment(Object value, SpecParamDTO specParamDTO) {
        //判断value是否为空 是否有值
        if(value == null || StringUtils.isBlank(value.toString())){
            return "其他";
        }
        //因为在执行此方法前有判断 value是数字的可以执行此方法 所有可以解析为double
        double val = Double.parseDouble(value.toString());
        //定义一个字符串作为返回值(实际就是一个范围，有一个默认值，当这个value不在这个范围中就返回默认值)
        String result = "其他";
        //拿到多个范围值拼凑的字符串，切割，获得每一个范围
        String[] setments = specParamDTO.getSegments().split(",");
        //循环 拿到每一个范围值
        for (String setment : setments) {
            //切割拿到最小和最大值
            String[] split = setment.split("-");
            //范围的最小值
            double begin = Double.parseDouble(split[0]);
            //范围的最大值
            double end = Double.MAX_VALUE;
            if(split.length == 2){
                //如果范围值有最大范围 赋值给end
                end = Double.parseDouble(split[1]);
            }
            //判断值是否在这个范围中
            if(val >= begin && val < end){
                //如果范围的最小值为0
                if(begin == 0){
                    //证明是在最小范围中 拼接字符串为 xx+单位+以下 例如(2000元以下)
                    result = split[1]+specParamDTO.getUnit()+"以下";
                } else if(split.length == 1){//如果数组只有一个元素
                    //证明是在最大范围中 拼接字符串为 xx+单位+以上 例如(5000元以下)
                    result = split[0]+specParamDTO.getUnit()+"以上";
                } else {
                    //证明在某一个范围中 最小值+单位+ - +最大值+单位 例如(2000元-5000元)
                    result = split[0]+specParamDTO.getUnit()+"-"+split[1]+specParamDTO.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 公共的QueryBuilders 拼接了一个过滤
     * @param request
     * @return
     */
    private QueryBuilder baseQuery(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //组合查询 一个根据输入内容查询 一个根据查询出来的结果进行过滤
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        Map<String, String> filter = request.getFilter();
        //过滤条件不为空 进行过滤
        if(!CollectionUtils.isEmpty(filter)){
            for (String key : filter.keySet()) {
                String name = "specs."+key;
                //如果是分类或品牌把字段名改为对应的id
                if("分类".equals(key)){
                    name = "categoryId";
                }else if("品牌".equals(key)){
                    name = "brandId";
                }
                boolQueryBuilder.filter(QueryBuilders.termQuery(name,filter.get(key)));
            }
        }
        return boolQueryBuilder;
    }

    /**
     * 用户输入关键词，进行搜索
     * @param request
     * @return
     */
    public PageResult<GoodDTO> search(SearchRequest request) {
        if(StringUtils.isBlank(request.getKey())){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //构建核心查询器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //过滤字段 显示页面需要的字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //查询条件 根据key分词查询 条件重复 用方法封装(baseQuery)
        queryBuilder.withQuery(this.baseQuery(request));
        //分页 springData的页数都是0开始 所有页数减一
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        AggregatedPage<Goods> goodsAggregatedPage = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        List<GoodDTO> goodDTOS = BeanHelper.copyWithCollection(goodsAggregatedPage.getContent(), GoodDTO.class);
        return new PageResult<GoodDTO>(goodsAggregatedPage.getTotalElements(),goodsAggregatedPage.getTotalPages(),goodDTOS);
    }

    /**
     * 根据输入关键字查询的结果来查询对应的过滤条件(分类和品牌)
     * @param request
     * @return
     */
    public Map<String ,List<?>> searchFilter(SearchRequest request) {
        Map<String, List<?>> filterList = new HashMap<>();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //进行过滤 所有的字段都不显示
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
        //进行查询
        nativeSearchQueryBuilder.withQuery(this.baseQuery(request));
        //设置分页 因为不关心结果 所有显示一条
        nativeSearchQueryBuilder.withPageable(PageRequest.of(0,1));
        //进行聚合
        String brandAgg = "brandAgg";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAgg).field("brandId"));
        String categoryAgg = "categoryAgg";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAgg).field("categoryId"));
        //查询
        AggregatedPage<Goods> goodsPage = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
        Aggregations aggregations = goodsPage.getAggregations();
        //获取brand的聚合
        LongTerms brandTerms = aggregations.get(brandAgg);
        handleBrandAgg(brandTerms,filterList);
        //获取category的聚合
        LongTerms categoryTerms = aggregations.get(categoryAgg);
        List<CategoryDTO> categoryDTOS = handlerCategoryAgg(categoryTerms, filterList);
        //判断分类是否是一个 一个就继续查询规格参数
        if(categoryDTOS != null && categoryDTOS.size() == 1){
            handleSpec(categoryDTOS,request,filterList);
        }
        return filterList;
    }

    /**
     * 查询规格参数
     * @param categoryDTOS
     * @param request
     * @param filterList
     */
    private void handleSpec(List<CategoryDTO> categoryDTOS, SearchRequest request, Map<String, List<?>> filterList) {
        //获取此分类下的规格参数
        List<SpecParamDTO> specParamDTOS = itemClient.findSpecParamByParam(null, categoryDTOS.get(0).getId(), true);
        //遍历查询
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withSourceFilter(new FetchSourceFilterBuilder().build());
        builder.withQuery(this.baseQuery(request));
        builder.withPageable(PageRequest.of(0,1));
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            //规格参数名
            String name = specParamDTO.getName();
            //要分通的字段
            String field = "specs." + name;
            builder.addAggregation(AggregationBuilders.terms(name).field(field));
        }
        AggregatedPage<Goods> goodsPage = esTemplate.queryForPage(builder.build(), Goods.class);
        Aggregations aggregations = goodsPage.getAggregations();
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            //规格参数名
            String name = specParamDTO.getName();
            StringTerms terms = aggregations.get(name);
            List<String> paramValues = terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            filterList.put(name,paramValues);
        }
    }

    /**
     * 处理聚合后的categoryIds
     * @param categoryTerms
     * @param filterList
     */
    private List<CategoryDTO> handlerCategoryAgg(LongTerms categoryTerms, Map<String,List<?>> filterList) {
        List<Long> categoryIds = categoryTerms.getBuckets().stream()
                .map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue).collect(Collectors.toList());
        List<CategoryDTO> categorys = itemClient.findCategoryById(categoryIds);
        filterList.put("分类",categorys);
        return categorys;
    }

    /**
     * 处理聚合后的brandIds
     * @param brandTerms
     * @param filterList
     */
    private void handleBrandAgg(LongTerms brandTerms, Map<String, List<?>> filterList) {
        List<Long> brandIds = brandTerms.getBuckets().stream().map(LongTerms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
        List<BrandDTO> brands = itemClient.findBrandByIds(brandIds);
        filterList.put("品牌",brands);
    }

    /**
     * 在索引库中添加信息
     * @param spuId
     */
    public void createIndex(Long spuId) {
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
        Goods goods = this.copySputoGoods(spuDTO);
        goodsRepository.save(goods);
    }

    /**
     * 在索引库中删除指定信息
     * @param spuId
     */
    public void deleteById(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
