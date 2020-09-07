package com.gaot.spider.resource;

import com.gaot.spider.domin.MediaData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/longmao")
public class IndexResource {

    private final Logger log = LoggerFactory.getLogger(IndexResource.class);

    private final MongoTemplate mongoTemplate;


    public IndexResource(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/page")
    public Page<MediaData> getPage(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize
            , @RequestParam(value = "type", required = false) Integer type) {
        log.info("分页查询，pageNo: {}, pageSize: {}, type: {}", pageNo, pageSize, type);
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);

        Query query = new Query();
        if (type!=null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        query.with(Sort.by(
                Sort.Order.desc("id")
        ));
        //计算总数
        long total = mongoTemplate.count(query, MediaData.class);
        //查询结果集
        List<MediaData> list = mongoTemplate.find(query.with(pageRequest), MediaData.class);
        Page<MediaData> page = new PageImpl(list, pageRequest, total);
        return page;
    }
}
