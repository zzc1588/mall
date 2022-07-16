package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.RRException;
import com.atguigu.gulimall.member.service.MemberLevelService;
import com.atguigu.gulimall.member.utils.HttpClientUtils;
import com.atguigu.gulimall.member.entity.vo.MemberRegistVo;
import com.atguigu.gulimall.member.entity.vo.SocialUser;
import com.atguigu.gulimall.member.entity.vo.UserLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberLevelService memberLevelService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveMemberRegist(MemberRegistVo vo) throws RRException{
        //设置账号，密码（hash编码），手机，邮箱，设置之前需要检查数据库中 存不存在 相同信息
        MemberEntity memberEntity = new MemberEntity();
        //账户
        checkUserNameUnique(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        //密码
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        memberEntity.setPassword(bCrypt.encode(vo.getPassword()));

        //手机
        checkPhoneUnique(vo.getPhone());
        memberEntity.setMobile(vo.getPhone());

        //邮箱
        checkEmailUnique(vo.getEmail());
        memberEntity.setEmail(vo.getEmail());

        //默认会员等级
        Long defaultLevelId = memberLevelService.getDefaultLevelId();
        memberEntity.setLevelId(defaultLevelId);
        //TODO 设置其他默认信息
        //保存
        this.save(memberEntity);
    }

    @Override
    public MemberEntity getByOpenid(String openid) {
        MemberEntity memberEntity =
                this.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getOpenId, openid));
        return memberEntity;
    }

    @Override
    public MemberEntity getByUserName(String userName) {
        MemberEntity memberEntity = this.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, userName));
        return memberEntity;
    }

    @Override
    public MemberEntity commonLogin(UserLoginVo vo) {
        MemberEntity user =
                this.getOne(new LambdaQueryWrapper<MemberEntity>()
                        .eq(MemberEntity::getUsername,vo.getUserName())
                        .or()
                        .eq(MemberEntity::getEmail,vo.getUserName())
                        .or()
                        .eq(MemberEntity::getMobile,vo.getUserName()));
        if (user == null){
            return null;
        }else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(vo.getPassword(), user.getPassword());
            if (matches){
                return user;
            }else {
                return null;
            }
        }
    }

    private void checkEmailUnique(String email) throws RRException{
        Long count = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getEmail, email));
        if(count>0){
            throw new RRException(BizCodeEnume.REGISTER_EMAIL_UNIQUE.getMsg(),BizCodeEnume.REGISTER_EMAIL_UNIQUE.getCode());
        }
    }

    private void checkPhoneUnique(String phone) throws RRException{
        Long count = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getMobile, phone));
        if(count>0){
            throw new RRException(BizCodeEnume.REGISTER_PHONE_UNIQUE.getMsg(),BizCodeEnume.REGISTER_PHONE_UNIQUE.getCode());
        }
    }

    private void checkUserNameUnique(String userName) throws RRException{
        Long count = this.baseMapper.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, userName));
        if(count>0){
            throw new RRException(BizCodeEnume.REGISTER_USERNAME_UNIQUE.getMsg(),BizCodeEnume.REGISTER_USERNAME_UNIQUE.getCode());
        }
    }


    @Override
    public MemberEntity login(String accessTokenInfo) {

        //从accessTokenInfo中获取出来两个值 access_token 和 oppenid
        //把accessTokenInfo字符串转换成map集合，根据map里面中的key取出相对应的value
        Gson gson = new Gson();
        HashMap accessMap = gson.fromJson(accessTokenInfo, HashMap.class);
        String accessToken = (String) accessMap.get("access_token");
        String openid = (String) accessMap.get("openid");

        //3、拿到access_token 和 oppenid，再去请求微信提供固定的API，获取到扫码人的信息
        //TODO 查询数据库当前用用户是否曾经使用过微信登录

        MemberEntity memberEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getOpenId, openid));

        if (memberEntity == null) {
            System.out.println("新用户注册");
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            //发送请求
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + resultUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //解析json
            HashMap userInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            String nickName = (String) userInfoMap.get("nickname");      //昵称
            Double sex = (Double) userInfoMap.get("sex");        //性别
            String headimgurl = (String) userInfoMap.get("headimgurl");      //微信头像

            //把扫码人的信息添加到数据库中
            memberEntity = new MemberEntity();
            memberEntity.setNickname(nickName);
            memberEntity.setGender(Integer.valueOf(Double.valueOf(sex).intValue()));
            memberEntity.setHeader(headimgurl);
            memberEntity.setCreateTime(new Date());
            memberEntity.setOpenId(openid);
            // register.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.insert(memberEntity);
        }
        return memberEntity;
    }
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {

        //具有登录和注册逻辑
        String uid = socialUser.getUid();

        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if (memberEntity != null) {
            //这个用户已经注册过
            //更新用户的访问令牌的时间和access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            Map<String,String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender)?1:0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());

                //把用户信息插入到数据库中
                this.baseMapper.insert(register);

            }
            return register;
        }

    }

    @Override
    public Long memberAlreadyExist(String email) {
        return this.count(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getEmail,email).select(MemberEntity::getEmail));
    }

    @Override
    public void updateIntegrationAndGrowth(OrderEntityTo to) {
        this.baseMapper.updateIntegrationAndGrowth(to);
    }


}