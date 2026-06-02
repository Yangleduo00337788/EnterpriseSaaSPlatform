package com.flowx.report.mapper;

import com.flowx.infrastructure.persistence.FlexBaseMapper;
import com.flowx.report.entity.RptReportConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Report mapper with custom statistics queries
 *
 * @author FlowX
 * @since 1.0.0
 */
@Mapper
public interface RptReportMapper extends FlexBaseMapper<RptReportConfig> {

    /**
     * Get approval statistics grouped by status
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of status/count maps
     */
    @Select("SELECT status, COUNT(*) as count FROM approval_instance " +
            "WHERE create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY status")
    List<Map<String, Object>> getApprovalStatsByStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get approval statistics for a specific user grouped by status
     *
     * @param userId    user ID
     * @param startDate start date
     * @param endDate   end date
     * @return list of status/count maps
     */
    @Select("SELECT status, COUNT(*) as count FROM approval_instance " +
            "WHERE create_by = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY status")
    List<Map<String, Object>> getMyApprovalStatsByStatus(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get average approval process time in hours
     *
     * @param startDate start date
     * @param endDate   end date
     * @return average process time
     */
    @Select("SELECT AVG(TIMESTAMPDIFF(HOUR, create_time, update_time)) FROM approval_instance " +
            "WHERE status IN (2, 3) AND create_time BETWEEN #{startDate} AND #{endDate}")
    Double getAvgProcessTime(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get average approval process time for a specific user
     *
     * @param userId    user ID
     * @param startDate start date
     * @param endDate   end date
     * @return average process time
     */
    @Select("SELECT AVG(TIMESTAMPDIFF(HOUR, create_time, update_time)) FROM approval_instance " +
            "WHERE create_by = #{userId} AND status IN (2, 3) " +
            "AND create_time BETWEEN #{startDate} AND #{endDate}")
    Double getMyAvgProcessTime(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get daily approval trend
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of date/count maps
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM approval_instance " +
            "WHERE create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getDailyApprovalTrend(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get daily approval trend for a specific user
     *
     * @param userId    user ID
     * @param startDate start date
     * @param endDate   end date
     * @return list of date/count maps
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as count FROM approval_instance " +
            "WHERE create_by = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getMyDailyApprovalTrend(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get total employee count
     *
     * @return total count
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE deleted = 0")
    Long getTotalEmployees();

    /**
     * Get new employees count this month
     *
     * @param monthStart month start date
     * @return new employee count
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE deleted = 0 AND create_time >= #{monthStart}")
    Long getNewEmployeesThisMonth(@Param("monthStart") LocalDateTime monthStart);

    /**
     * Get employee distribution by department
     *
     * @return list of dept/count maps
     */
    @Select("SELECT d.dept_name as name, COUNT(u.id) as count FROM sys_user u " +
            "LEFT JOIN sys_dept d ON u.dept_id = d.id " +
            "WHERE u.deleted = 0 GROUP BY u.dept_id, d.dept_name")
    List<Map<String, Object>> getDeptDistribution();

    /**
     * Get employee distribution by position
     *
     * @return list of position/count maps
     */
    @Select("SELECT p.position_name as name, COUNT(u.id) as count FROM sys_user u " +
            "LEFT JOIN sys_position p ON u.position_id = p.id " +
            "GROUP BY u.position_id, p.position_name")
    List<Map<String, Object>> getPositionDistribution();

    /**
     * Get total department count
     *
     * @return total count
     */
    @Select("SELECT COUNT(*) FROM sys_dept WHERE deleted = 0")
    Long getTotalDepts();

    /**
     * Get total position count
     *
     * @return total count
     */
    @Select("SELECT COUNT(*) FROM sys_position WHERE deleted = 0")
    Long getTotalPositions();

    /**
     * Get department employee counts
     *
     * @return list of dept/count maps
     */
    @Select("SELECT d.dept_name as name, COUNT(u.id) as value FROM sys_user u " +
            "LEFT JOIN sys_dept d ON u.dept_id = d.id " +
            "WHERE u.deleted = 0 GROUP BY u.dept_id, d.dept_name")
    List<Map<String, Object>> getDeptEmployeeCounts();

    /**
     * Get total tenant count
     *
     * @return total count
     */
    @Select("SELECT COUNT(*) FROM sys_tenant WHERE deleted = 0")
    Long getTotalTenants();

    /**
     * Get active tenant count
     *
     * @return active count
     */
    @Select("SELECT COUNT(*) FROM sys_tenant WHERE deleted = 0 AND status = 0")
    Long getActiveTenants();

    /**
     * Get tenant distribution by package
     *
     * @return list of package/count maps
     */
    @Select("SELECT p.package_name as name, COUNT(t.id) as count FROM sys_tenant t " +
            "LEFT JOIN sys_tenant_package p ON t.package_id = p.id " +
            "WHERE t.deleted = 0 GROUP BY t.package_id, p.package_name")
    List<Map<String, Object>> getPackageDistribution();

    /**
     * Get monthly tenant growth
     *
     * @return list of month/count maps
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, COUNT(*) as count FROM sys_tenant " +
            "WHERE deleted = 0 GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY month")
    List<Map<String, Object>> getMonthlyTenantGrowth();

    /**
     * Get total operation log count
     *
     * @return total count
     */
    @Select("SELECT COUNT(*) FROM sys_operation_log")
    Long getTotalOperations();

    /**
     * Get today's operation count
     *
     * @param todayStart today start date
     * @return today count
     */
    @Select("SELECT COUNT(*) FROM sys_operation_log WHERE create_time >= #{todayStart}")
    Long getTodayOperations(@Param("todayStart") LocalDateTime todayStart);

    /**
     * Get operation distribution by type
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of type/count maps
     */
    @Select("SELECT business_type as name, COUNT(*) as count FROM sys_operation_log " +
            "WHERE create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY business_type")
    List<Map<String, Object>> getOperationTypeDistribution(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get hourly operation trend
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of hour/count maps
     */
    @Select("SELECT HOUR(create_time) as hour, COUNT(*) as count FROM sys_operation_log " +
            "WHERE create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY HOUR(create_time) ORDER BY hour")
    List<Map<String, Object>> getHourlyOperationTrend(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
