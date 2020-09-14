package com.gaot.spider.resource;

import com.gaot.spider.domin.MediaData;
import com.gaot.spider.dto.AreaDTO;
import com.gaot.spider.dto.ConditionDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
            , @RequestParam(value = "type", required = false) Integer type, @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("分页查询，pageNo: {}, pageSize: {}, type: {}, keyword: {}", pageNo, pageSize, type, keyword);
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);

        Query query = new Query();
        if (type!=null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (StringUtils.isNotBlank(keyword)) {
            Pattern pattern= Pattern.compile("^.*"+keyword+".*$", Pattern.CASE_INSENSITIVE);
            Criteria criatira = new Criteria();
            criatira.orOperator(Criteria.where("name").regex(pattern),
                    Criteria.where("alias").regex(pattern));
            query.addCriteria(criatira);
        }
        query.with(Sort.by(
                Sort.Order.desc("year")
        ));
        query.with(Sort.by(
                Sort.Order.desc("createTime")
        ));
        //计算总数
        long total = mongoTemplate.count(query, MediaData.class);
        //查询结果集
        List<MediaData> list = mongoTemplate.find(query.with(pageRequest), MediaData.class);
        Page<MediaData> page = new PageImpl(list, pageRequest, total);
        return page;
    }

    public void getCondition(@PathVariable Integer type) {
        ConditionDTO dto = new ConditionDTO();
        List<AreaDTO> areas = new ArrayList<>();
        if (type==1) {
            areas.add(new AreaDTO("大陆", "中国大陆,大陆"));
            areas.add(new AreaDTO("香港", "中国香港,香港"));
            areas.add(new AreaDTO("台湾", "中国台湾,台湾"));
            areas.add(new AreaDTO("美国", "美国"));
            areas.add(new AreaDTO("欧美", "欧美,波兰,加纳,比利時,荷兰,瑞士,芬兰,英国,爱尔兰,捷克,智利,葡萄牙,冰岛,比利时"));
            areas.add(new AreaDTO("法国", "法国"));
            areas.add(new AreaDTO("德国", "德国"));
            areas.add(new AreaDTO("英国", "英国"));

            areas.add(new AreaDTO("比利時", "比利時"));
            areas.add(new AreaDTO("荷兰", "荷兰"));
            areas.add(new AreaDTO("瑞士", "瑞士"));
            areas.add(new AreaDTO("意大利", "意大利"));
            areas.add(new AreaDTO("新加坡", "新加坡"));
            areas.add(new AreaDTO("瑞典", "瑞典"));
            areas.add(new AreaDTO("巴西", "巴西"));
            areas.add(new AreaDTO("日本", "日本"));

            areas.add(new AreaDTO("澳大利亚", "澳大利亚"));

            areas.add(new AreaDTO("加拿大", "加拿大"));
            areas.add(new AreaDTO("以色列", "以色列"));
            areas.add(new AreaDTO("爱尔兰", "爱尔兰"));
            areas.add(new AreaDTO("新西兰", "新西兰"));
            areas.add(new AreaDTO("希腊", "希腊"));
            areas.add(new AreaDTO("挪威", "挪威"));
            areas.add(new AreaDTO("泰国", "泰国"));
            areas.add(new AreaDTO("俄罗斯", "俄罗斯,苏联"));

            areas.add(new AreaDTO("印度", "印度"));
            areas.add(new AreaDTO("韩国", "韩国"));
            areas.add(new AreaDTO("西班牙", "西班牙"));
            areas.add(new AreaDTO("墨西哥", "墨西哥"));



            areas.add(new AreaDTO("其它", "70,白俄罗斯,伊朗,巴勒斯坦,爱沙尼亚,匈牙利,秘鲁,黎巴嫩,越南,麦,多米尼加,格鲁吉亚,塞尔维亚,巴基斯坦,印度尼西,叙利亚" +
                    ",埃及,缅甸,南非,澳大利,奥地利,牙买加,芬兰,苏丹,克罗地亚,乌克兰,马来西亚,其它,委内瑞拉,蒙古,马其顿,阿根廷,印尼,塔吉克斯,老挝,菲律宾,罗马尼亚,保加利亚,尼日利亚,土耳其" +
                    ",马耳他,冰岛,哥伦比亚,古巴,柬埔寨,西德"));


        }
    }
}
