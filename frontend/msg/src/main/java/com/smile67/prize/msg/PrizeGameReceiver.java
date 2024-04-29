package com.smile67.prize.msg;

import com.alibaba.fastjson.JSON;
import com.smile67.prize.commons.config.RabbitKeys;
import com.smile67.prize.commons.db.entity.CardUserGame;
import com.smile67.prize.commons.db.service.CardUserGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户参加的活动通过该队列投放
 */
@Component
@RabbitListener(queues = RabbitKeys.QUEUE_PLAY)
public class PrizeGameReceiver {

    private final static Logger logger = LoggerFactory.getLogger(PrizeGameReceiver.class);

    @Autowired
    private CardUserGameService cardUserGameService;

    @RabbitHandler
    public void processMessage(String message) {
        logger.info("user play : msg={}" , message);
        cardUserGameService.save( JSON.parseObject(message,CardUserGame.class));
    }

}
