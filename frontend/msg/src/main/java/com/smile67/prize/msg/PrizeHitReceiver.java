package com.smile67.prize.msg;

import com.alibaba.fastjson.JSON;
import com.smile67.prize.commons.config.RabbitKeys;
import com.smile67.prize.commons.db.entity.CardUserHit;
import com.smile67.prize.commons.db.service.CardUserHitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户中奖后的信息及中的奖品通过该队列投放
 */
@Component
@RabbitListener(queues = RabbitKeys.QUEUE_HIT)
public class PrizeHitReceiver {
    private final static Logger logger = LoggerFactory.getLogger(PrizeHitReceiver.class);

    @Autowired
    private CardUserHitService hitService;

    @RabbitHandler
    public void processMessage(String message) {
        logger.info("user hit : message={}", message);
        hitService.save(JSON.parseObject(message, CardUserHit.class));
    }
}