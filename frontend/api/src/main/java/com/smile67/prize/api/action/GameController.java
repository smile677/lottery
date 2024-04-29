package com.smile67.prize.api.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smile67.prize.commons.db.entity.CardGame;
import com.smile67.prize.commons.db.entity.CardProductDto;
import com.smile67.prize.commons.db.entity.ViewCardUserHit;
import com.smile67.prize.commons.db.service.CardGameService;
import com.smile67.prize.commons.db.service.GameLoadService;
import com.smile67.prize.commons.db.service.ViewCardUserHitService;
import com.smile67.prize.commons.utils.ApiResult;
import com.smile67.prize.commons.utils.PageBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/game")
@Api(tags = {"活动模块"})
public class GameController {
    @Autowired
    private GameLoadService loadService;
    @Autowired
    private CardGameService gameService;
    @Autowired
    private ViewCardUserHitService hitService;

    @GetMapping("/list/{status}/{curpage}/{limit}")
    @ApiOperation(value = "活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "活动状态（-1=全部，0=未开始，1=进行中，2=已结束）", example = "-1", required = true),
            @ApiImplicitParam(name = "curpage", value = "第几页", defaultValue = "1", dataType = "int", example = "1", required = true),
            @ApiImplicitParam(name = "limit", value = "每页条数", defaultValue = "10", dataType = "int", example = "3", required = true)
    })
    public ApiResult list(@PathVariable int status, @PathVariable int curpage, @PathVariable int limit) {
        LambdaQueryWrapper<CardGame> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Date now = new Date();
        System.out.println("now = " + now);
        switch (status) {
            // 全部
            case -1:
                lambdaQueryWrapper.orderByDesc(CardGame::getEndtime);
                break;
            // 未开始
            case 0:
                lambdaQueryWrapper.ge(CardGame::getStarttime, now).orderByAsc(CardGame::getStarttime);
                break;
            // 进行中
            case 1:
                lambdaQueryWrapper.le(CardGame::getStarttime, now).ge(CardGame::getEndtime, now);
                break;
            // 已结束
            case 2:
                lambdaQueryWrapper.le(CardGame::getEndtime, now).orderByDesc(CardGame::getEndtime);
                break;
        }
        Page<CardGame> page = gameService.page(new Page<>(curpage, limit), lambdaQueryWrapper);
        return new ApiResult(1, "成功", new PageBean<>(page));
    }

    @GetMapping("/info/{gameid}")
    @ApiOperation(value = "活动信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "活动id", example = "1", required = true)
    })
    public ApiResult<CardGame> info(@PathVariable int gameid) {
        return new ApiResult<>(1, "成功", gameService.getById(gameid));
    }

    @GetMapping("/products/{gameid}")
    @ApiOperation(value = "奖品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "活动id", example = "1", required = true)
    })
    public ApiResult<List<CardProductDto>> products(@PathVariable int gameid) {
        return new ApiResult<>(1, "成功", loadService.getByGameId(gameid));
    }

    @GetMapping("/hit/{gameid}/{curpage}/{limit}")
    @ApiOperation(value = "中奖列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameid", value = "活动id", dataType = "int", example = "1", required = true),
            @ApiImplicitParam(name = "curpage", value = "第几页", defaultValue = "1", dataType = "int", example = "1", required = true),
            @ApiImplicitParam(name = "limit", value = "每页条数", defaultValue = "10", dataType = "int", example = "3", required = true)
    })
    public ApiResult<PageBean<ViewCardUserHit>> hit(@PathVariable int gameid, @PathVariable int curpage, @PathVariable int limit) {
        LambdaQueryWrapper<ViewCardUserHit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ViewCardUserHit::getGameid, gameid);
        return new ApiResult<>(1,
                "成功",
                new PageBean<ViewCardUserHit>(hitService.page(new Page<>(curpage, limit), lambdaQueryWrapper)));
    }
}