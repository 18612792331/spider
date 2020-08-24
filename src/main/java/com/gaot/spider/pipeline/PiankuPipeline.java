package com.gaot.spider.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class PiankuPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        String name = resultItems.get("name");
        System.out.println("数据处理----------------------" + name);
        String domain = task.getSite().getDomain();
        System.out.println("--------domin-------------" + domain);

    }
}
