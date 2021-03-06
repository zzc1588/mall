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
        //????????????????????????hash?????????????????????????????????????????????????????????????????? ???????????? ????????????
        MemberEntity memberEntity = new MemberEntity();
        //??????
        checkUserNameUnique(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        //??????
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        memberEntity.setPassword(bCrypt.encode(vo.getPassword()));

        //??????
        checkPhoneUnique(vo.getPhone());
        memberEntity.setMobile(vo.getPhone());

        //??????
        checkEmailUnique(vo.getEmail());
        memberEntity.setEmail(vo.getEmail());

        //??????????????????
        Long defaultLevelId = memberLevelService.getDefaultLevelId();
        memberEntity.setLevelId(defaultLevelId);
        //TODO ????????????????????????
        //??????
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

        //???accessTokenInfo???????????????????????? access_token ??? oppenid
        //???accessTokenInfo??????????????????map???????????????map????????????key??????????????????value
        Gson gson = new Gson();
        HashMap accessMap = gson.fromJson(accessTokenInfo, HashMap.class);
        String accessToken = (String) accessMap.get("access_token");
        String openid = (String) accessMap.get("openid");

        //3?????????access_token ??? oppenid????????????????????????????????????API??????????????????????????????
        //TODO ???????????????????????????????????????????????????????????????

        MemberEntity memberEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getOpenId, openid));

        if (memberEntity == null) {
            System.out.println("???????????????");
            //???????????????????????????????????????????????????
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            //????????????
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + resultUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //??????json
            HashMap userInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            String nickName = (String) userInfoMap.get("nickname");      //??????
            Double sex = (Double) userInfoMap.get("sex");        //??????
            String headimgurl = (String) userInfoMap.get("headimgurl");      //????????????

            //??????????????????????????????????????????
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

        //???????????????????????????
        String uid = socialUser.getUid();

        //1??????????????????????????????????????????????????????
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if (memberEntity != null) {
            //???????????????????????????
            //???????????????????????????????????????access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            //2???????????????????????????????????????????????????????????????????????????
            MemberEntity register = new MemberEntity();
            //3????????????????????????????????????????????????????????????????????????
            Map<String,String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //????????????
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

                //????????????????????????????????????
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