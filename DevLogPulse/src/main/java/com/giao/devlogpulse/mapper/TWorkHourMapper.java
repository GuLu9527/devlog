package com.giao.devlogpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.giao.devlogpulse.entity.po.TWorkHour;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 工时记录 Mapper 接口
 */
@Mapper
public interface TWorkHourMapper extends BaseMapper<TWorkHour> {

    /**
     * 按任务统计工时
     */
    @Select("SELECT task_id, SUM(hours) as totalHours, COUNT(*) as recordCount " +
            "FROM t_work_hour " +
            "WHERE task_id = #{taskId} AND status = 'APPROVED' AND is_deleted = 0 " +
            "GROUP BY task_id")
    Map<String, Object> getTaskWorkHourStats(@Param("taskId") Long taskId);

    /**
     * 按用户统计工时
     */
    @Select("SELECT user_id, SUM(hours) as totalHours, COUNT(*) as recordCount " +
            "FROM t_work_hour " +
            "WHERE user_id = #{userId} AND work_date BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 'APPROVED' AND is_deleted = 0 " +
            "GROUP BY user_id")
    Map<String, Object> getUserWorkHourStats(@Param("userId") Long userId, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);

    /**
     * 按项目统计工时
     */
    @Select("SELECT project_id, SUM(hours) as totalHours, COUNT(*) as recordCount " +
            "FROM t_work_hour " +
            "WHERE project_id = #{projectId} " +
            "AND (#{startDate} IS NULL OR work_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR work_date <= #{endDate}) " +
            "AND status = 'APPROVED' AND is_deleted = 0 " +
            "GROUP BY project_id")
    Map<String, Object> getProjectWorkHourStats(@Param("projectId") Long projectId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 获取团队工时排行
     */
    @Select("SELECT u.id as userId, u.username, u.real_name as realName, " +
            "SUM(wh.hours) as totalHours, COUNT(wh.id) as recordCount " +
            "FROM t_work_hour wh " +
            "LEFT JOIN t_user u ON wh.user_id = u.id " +
            "WHERE (#{startDate} IS NULL OR wh.work_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR wh.work_date <= #{endDate}) " +
            "AND wh.status = 'APPROVED' AND wh.is_deleted = 0 " +
            "GROUP BY u.id, u.username, u.real_name " +
            "ORDER BY totalHours DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getTeamWorkHourRanking(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("limit") int limit);

    /**
     * 获取工时趋势数据
     */
    @Select("SELECT work_date, SUM(hours) as dailyHours " +
            "FROM t_work_hour " +
            "WHERE work_date BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 'APPROVED' AND is_deleted = 0 " +
            "GROUP BY work_date " +
            "ORDER BY work_date")
    List<Map<String, Object>> getWorkHourTrend(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);

    /**
     * 按类型统计工时
     */
    @Select("SELECT type, SUM(hours) as totalHours, COUNT(*) as recordCount " +
            "FROM t_work_hour " +
            "WHERE work_date BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 'APPROVED' AND is_deleted = 0 " +
            "GROUP BY type")
    List<Map<String, Object>> getWorkHourByType(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
}