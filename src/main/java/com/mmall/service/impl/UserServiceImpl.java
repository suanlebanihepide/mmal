/*
 * @Author: shenzheng
 * @Date: 2020/4/27 23:19
 */

package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

   @Autowired
   private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int count = userMapper.checkUsername(username);
        if(count==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
//       todo  密码MD5登录
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
       return ServerResponse.createBySuccess("登陆成功", user);
    }

    public  ServerResponse<String> register(User user){

        ServerResponse vaildResponse = this.checkVaild(user.getUsername(),Const.username);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }
        ServerResponse vaildResponse2 = this.checkVaild(user.getEmail(),Const.EMAIL);
        if(!vaildResponse2.isSuccess()){
            return vaildResponse2;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = userMapper.insert(user);
        if(count==0){
            return ServerResponse.createByErrorMessage("注册失败");

        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }



    public ServerResponse<String> checkVaild(String str,String type){

        if(StringUtils.isNoneBlank(str)){

            if(Const.username.equals(type)){
                int count = userMapper.checkUsername(str);
                if(count>0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }

            }
            if(Const.EMAIL.equals(type)){
                int count = userMapper.checkEmail(str);
                if(count>0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username){

        ServerResponse validResponse = this.checkVaild(username,Const.username);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题为空");

    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){

            int count = userMapper.checkAnswer(username,question,answer);
            if(count>0){
//                答案正确
                String forgetToken = UUID.randomUUID().toString();
                TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);

                return  ServerResponse.createBySuccess(forgetToken);
            }
            return ServerResponse.createByErrorMessage("答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String password,String forgetToken){

        if(StringUtils.isNoneBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("Token为空");
        }
        ServerResponse validResponse = this.checkVaild(username,Const.username);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isNoneBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(password);
           int count = userMapper.updatePasswordByUsername(username,md5Password);
            if(count>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }
        else{
            return ServerResponse.createByErrorMessage("重新获取token");
        }

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public  ServerResponse<String> resetPassword(String password,String passwordNew , User user){

        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(password),user.getId());
        if(count==0){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return  ServerResponse.createBySuccessMessage("succes");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> update_information(User user){

        //username不能被更新
        //email是否已存在
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId()) ;

        if(resultCount>0){
            return ServerResponse.createByErrorMessage("邮箱已经存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新失败");

    }

    //backend
    public ServerResponse checkAdminRole(User user){
    if(user!=null &&user.getRole().intValue()==Const.Role.ROLE_ADMIN)
        return ServerResponse.createBySuccess();
        return ServerResponse.createByError();

    }

}
