package com.itheima.prize.api.action;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.prize.commons.config.RedisKeys;
import com.itheima.prize.commons.db.entity.CardUser;
import com.itheima.prize.commons.db.mapper.CardUserMapper;
import com.itheima.prize.commons.db.service.CardUserService;
import com.itheima.prize.commons.utils.ApiResult;
import com.itheima.prize.commons.utils.PasswordUtil;
import com.itheima.prize.commons.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Api(tags = {"登录模块"})
public class LoginController {
    @Autowired
    private CardUserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    public ApiResult<CardUser> login(HttpServletRequest request, @RequestParam String account, @RequestParam String password) {
        // 每次登录都校验是否密码错误5次
        Integer errorTimes = (Integer) redisUtil.get(RedisKeys.USERLOGINTIMES + account);
        if (errorTimes != null && errorTimes >= 5) {
            return new ApiResult<>(0, "密码错误5次，请5分钟后再登录", null);
        }
        // 登录成功
        QueryWrapper<CardUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uname", account).eq("passwd", PasswordUtil.encodePassword(password));
        List<CardUser> users = userService.list(queryWrapper);
        if (users != null && !users.isEmpty()) {
            HttpSession session = request.getSession();
            CardUser user = users.get(0);
            // 用户信息脱敏
            user.setPasswd(null);
            user.setIdcard(null);
            session.setAttribute("user", user);
            return new ApiResult<>(1, "登录成功", user);
        } else {
            // 账户名和密码错误添加错误次数
            redisUtil.incr(RedisKeys.USERLOGINTIMES + account, 1);
            redisUtil.expire(RedisKeys.USERLOGINTIMES + account, 5 * 60);
            return new ApiResult<>(0, "账户名或密码错误", null);
        }
    }

    @GetMapping("/logout")
    @ApiOperation(value = "退出")
    public ApiResult<CardUser> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return new ApiResult<>(1, "退出成功", null);
    }

}