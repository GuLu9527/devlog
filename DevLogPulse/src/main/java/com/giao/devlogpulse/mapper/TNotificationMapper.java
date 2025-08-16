package com.giao.devlogpulse.mapper;

import com.giao.devlogpulse.entity.po.TNotification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 通知消息表 Mapper 接口
 */
@Mapper
public interface TNotificationMapper extends BaseMapper<TNotification> {

    /**
     * 获取用户未读通知数量
     */
    @Select("SELECT COUNT(*) FROM t_notification WHERE receiver_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    Long getUnreadCount(@Param("userId") Long userId);

    /**
     * 批量标记为已读
     */
    @Update("UPDATE t_notification SET is_read = 1, read_time = NOW() WHERE receiver_id = #{userId} AND id IN (${ids}) AND is_deleted = 0")
    int batchMarkAsRead(@Param("userId") Long userId, @Param("ids") String ids);

    /**
     * 标记所有未读为已读
     */
    @Update("UPDATE t_notification SET is_read = 1, read_time = NOW() WHERE receiver_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 获取用户通知列表
     */
    @Select({"<script>",
            "SELECT n.*, u.real_name as sender_name FROM t_notification n",
            "LEFT JOIN t_user u ON n.sender_id = u.id",
            "WHERE n.receiver_id = #{userId} AND n.is_deleted = 0",
            "<if test='isRead != null'>",
            "AND n.is_read = #{isRead}",
            "</if>",
            "<if test='type != null and type != \"\"'>",
            "AND n.type = #{type}",
            "</if>",
            "ORDER BY n.create_time DESC",
            "LIMIT #{offset}, #{limit}",
            "</script>"})
    List<TNotification> selectNotificationsByUser(@Param("userId") Long userId,
                                                 @Param("isRead") Integer isRead,
                                                 @Param("type") String type,
                                                 @Param("offset") Long offset,
                                                 @Param("limit") Long limit);
}