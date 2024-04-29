package com.itheima.prize.api.action;

import com.alibaba.fastjson.JSON;
import com.itheima.prize.api.config.LuaScript;
import com.itheima.prize.commons.config.RabbitKeys;
import com.itheima.prize.commons.config.RedisKeys;
import com.itheima.prize.commons.db.entity.*;
import com.itheima.prize.commons.db.mapper.CardGameMapper;
import com.itheima.prize.commons.db.service.CardGameService;
import com.itheima.prize.commons.utils.ApiResult;
import com.itheima.prize.commons.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/act")
@Api(tags = {"抽奖模块"})
public class ActController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private LuaScript luaScript;

    @GetMapping("/go/{gameid}")
    @ApiOperation(value = "抽奖")
    @ApiImplicitParams({
            @ApiImplicitParam(name="gameid",value = "活动id",example = "1",required = true)
    })
    public ApiResult<Object> act(@PathVariable int gameid, HttpServletRequest request){
        //TODO
        return null;
    }

    @GetMapping("/info/{gameid}")
    @ApiOperation(value = "缓存信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "活动id", example = "1", required = true)
    })
    public ApiResult info(@PathVariable int gameid) {
        // TODO 此接口测试未通过(原因是get不到redis中的值)
        log.info("redisUtil.get(RedisKeys.INFO + gameid){}", redisUtil.get(RedisKeys.INFO + gameid));
        // 取出活动基本信息
        Map map = new LinkedHashMap<>();
        map.put(RedisKeys.INFO + gameid, redisUtil.get(RedisKeys.INFO + gameid));
        log.info("info:{}", map);
        // 取出临牌桶中令牌 时间:
        List<Object> tokens = redisUtil.lrange(RedisKeys.TOKENS + gameid, 0, -1);
        Map tokenMap = new LinkedHashMap();
        //      对取出令牌桶中的令牌并查询对应的奖品信息
        tokens.forEach(token -> tokenMap.put(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(Long.valueOf(token.toString()) / 1000)),
                redisUtil.get(RedisKeys.TOKEN + gameid + "_" + token))
        );
        //      将取出的tokens放入map中
        map.put(RedisKeys.TOKENS + gameid, tokenMap);
        log.info("info+token:{}", map);
        // 最大中奖次数
        map.put(RedisKeys.MAXGOAL + gameid, redisUtil.hmget(RedisKeys.MAXGOAL + gameid));
        // 最大可抽奖次数
        map.put(RedisKeys.MAXENTER + gameid, redisUtil.hmget(RedisKeys.MAXENTER + gameid));
        // 用户中奖概率
        map.put(RedisKeys.RANDOMRATE + gameid, redisUtil.hmget(RedisKeys.RANDOMRATE + gameid));
        log.info("info+tokens+(maxgoal+maxenter+randomrate):{}", map);
        return new ApiResult(200, "缓存信息", map);
    }
}
