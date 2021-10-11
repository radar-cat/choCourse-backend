package com.example.springbootchocourse.mapper;

import com.example.springbootchocourse.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where id = #{id}")
    User getById(long id);

    @Update("update user set course_limit = #{limit}")
    int editLimit(int limit);
}
