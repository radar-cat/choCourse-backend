package com.example.springbootchocourse.service;

import com.example.springbootchocourse.bean.Course;
import com.example.springbootchocourse.exception.GlobalException;
import com.example.springbootchocourse.mapper.CourseMapper;
import com.example.springbootchocourse.redis.CourseKey;
import com.example.springbootchocourse.redis.RedisService;
import com.example.springbootchocourse.result.CodeMsg;
import com.example.springbootchocourse.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CourseService {

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    RedisService redisService;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     *查询课程列表
     * @return
     */
    public List<Course> getCourseList(){
//        取缓存
//        List<Course> redisList = redisService.get(CourseKey.getCourseList, "", List.class);
//        if(redisList != null){
//            return redisList;
//        }
        List<Course> dbList = courseMapper.getCourseList();
//        if(dbList.size()>0){
//            redisService.set(CourseKey.getCourseList, "", dbList);
//        }
        return dbList;
    }

    /**
     * 根据关键字查询课程
     * @param query
     * @return
     */
    public List<Course> getSearchCourse(String query){
        if(query == null){
            throw new GlobalException(CodeMsg.QUERY_NULL);
        }
        return courseMapper.getSearchCourse(query);
    }

    /**
     * 根据课程id查询课程
     * @param courseId
     * @return
     */
    public Course getCourseByCourseId(int courseId) {
        return courseMapper.getCourseByCourseId(courseId);
    }

    /**
     *减少库存，每次减一
     * @param course
     * @return
     */
    public boolean reduceStock(Course course) {
        int numAttempts = 0;
        int ret = 0;
        Course cc = new Course();
        cc.setId(course.getId());
        cc.setVersion(course.getVersion());
        do {
            numAttempts++;
            try {
                cc.setVersion(courseMapper.getVersionByCourseId(course.getId()));
                ret = courseMapper.reduceStockByVersion(cc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ret != 0)
                break;
        } while (numAttempts < DEFAULT_MAX_RETRIES);

        return ret > 0;
    }

    /**
     *增加库存，每次加一
     * @param course
     * @return
     */
    public boolean increaseStock(Course course) {
        int numAttempts = 0;
        int ret = 0;
        Course cc = new Course();
        cc.setId(course.getId());
        cc.setVersion(course.getVersion());
        do {
            numAttempts++;
            try {
                cc.setVersion(courseMapper.getVersionByCourseId(course.getId()));
                ret = courseMapper.increaseStockByVersion(cc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ret != 0)
                break;
        } while (numAttempts < DEFAULT_MAX_RETRIES);

        return ret > 0;
    }

    /**
     * 插入一条课程数据
     * @param course
     * @return
     */
    public Result addCourse(Course course){
        int res = courseMapper.addCourse(course);
        if(res == 0){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //更新缓存
        redisService.set(CourseKey.getCourseStock, "" + course.getId(), course.getStock());
        redisService.set(CourseKey.getStartDate, "" + course.getId(), course.getStart_date());
        redisService.set(CourseKey.getEndDate, "" + course.getId(), course.getEnd_date());
//        Set<String> keysList = stringRedisTemplate.keys("CourseKey:" + "*");
//        stringRedisTemplate.delete(keysList);
        return Result.success("success");
    }

    /**
     * 返回待修改课程数据
     * @param id
     * @return
     */
    public Result getUpdateCourse(int id){
        Course course = courseMapper.getCourseByCourseId(id);
        if(course == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        return Result.success(course);
    }

    /**
     * 修改一条课程信息
     * @param course
     * @return
     */
    public Result editCourse(Course course){
        int res = courseMapper.editCourse(course);
        if(res < 0){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //更新缓存
        redisService.set(CourseKey.getCourseStock, "" + course.getId(), course.getStock());
        redisService.set(CourseKey.getStartDate, "" + course.getId(), course.getStart_date());
        redisService.set(CourseKey.getEndDate, "" + course.getId(), course.getEnd_date());
        return Result.success("success");
    }

    /**
     * 删除一条课程信息
     * @param id
     * @return
     */
    public Result deleteCourse(int id){
        int res = courseMapper.deleteCourse(id);
        if(res < 0){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //删除redis中原先设置的键值对
        Set<String> keysList = stringRedisTemplate.keys("CourseKey:" + "*" + id);
        stringRedisTemplate.delete(keysList);
        return Result.success("success");
    }
}
