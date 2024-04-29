package com.smile67.prize.commons.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smile67.prize.commons.db.entity.CardUser;
import com.smile67.prize.commons.db.service.CardUserService;
import com.smile67.prize.commons.db.mapper.CardUserMapper;
import org.springframework.stereotype.Service;

/**
* @author shawn
* @description 针对表【card_user(会员信息表)】的数据库操作Service实现
* @createDate 2023-12-26 11:58:48
*/
@Service
public class CardUserServiceImpl extends ServiceImpl<CardUserMapper, CardUser>
    implements CardUserService{

}




