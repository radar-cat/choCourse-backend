package com.example.springbootchocourse.mapper;

import com.example.springbootchocourse.bean.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("select * from course")
    List<Course> getCourseList();

    @Select("select * from course where name like #{query}")
    List<Course> getSearchCourse(String query);

    @Select("select * from course where id = #{courseId}")
    Course getCourseByCourseId(int courseId);

    // 获取最新版本号
    @Select("select version from course where id = #{courseId}")
    int getVersionByCourseId(int courseId);

    //stock > 0 和版本号实现乐观锁, 防止超卖
    @Update("update course set stock = stock-1, version = version+1 where id = #{id} and stock > 0 and version = #{version}")
    int reduceStockByVersion(Course course);

    @Update("update course set stock = stock+1, version = version-1 where id = #{id} and version = #{version}")
    int increaseStockByVersion(Course course);

    @Insert("insert into course(name, counts, teacher, details, start_date, end_date, stock, version)" +
            "values(#{name}, #{counts}, #{teacher}, #{details}, #{start_date}, #{end_date}, #{stock}, 0)")
    int addCourse(Course course);

    @Update("update course set name = #{name}, counts = #{counts}, teacher = #{teacher}, " +
            "details = #{details}, start_date = #{start_date}, end_date = #{end_date}, " +
            "stock = #{stock} where id = #{id}")
    int editCourse(Course course);

    @Delete("delete from course where id = #{courseId}")
    int deleteCourse(int courseId);

}
