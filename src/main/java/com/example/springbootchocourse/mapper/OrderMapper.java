package com.example.springbootchocourse.mapper;

import com.example.springbootchocourse.bean.Order;
import com.example.springbootchocourse.bean.OrderMap;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 通过@SelectKey使insert成功后返回主键id，也就是订单id
     * @param order
     * @return
     */
    @Insert("insert into `order` (user_id, course_id, course_name, create_date) values ("
            + "#{user_id}, #{course_id}, #{course_name}, #{create_date})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = int.class, before = false, statement = "select last_insert_id()")
    int insert(Order order);

    @Delete("delete from `order` where user_id = #{uId} and course_id = #{cId}")
    int deleteOrder(@Param("uId") long uId, @Param("cId") int cId);

    @Insert("insert into order_map(user_id, course_id, order_id)values(#{user_id}, #{course_id}, #{order_id})")
    int insertOrderMap(OrderMap orderMap);

    @Delete("delete from order_map where user_id = #{uId} and course_id = #{cId}")
    int deleteOrderMap(@Param("uId") long uId, @Param("cId") int cId);

    @Select("select * from `order` where user_id = #{id}")
    List<Order> getOrderByUId(long id);
}
