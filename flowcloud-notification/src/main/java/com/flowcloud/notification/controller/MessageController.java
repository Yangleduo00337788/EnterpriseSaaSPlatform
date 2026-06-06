package com.flowcloud.notification.controller;

import com.flowcloud.common.result.PageResult;
import com.flowcloud.common.result.Result;
import com.flowcloud.notification.entity.SysMessage;
import com.flowcloud.notification.service.MessageService;
import com.flowcloud.system.service.RoleAuthService;
import com.flowcloud.system.support.PermissionCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "消息通知")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final RoleAuthService roleAuthService;

    @Operation(summary = "消息列表")
    @GetMapping
    public Result<PageResult<SysMessage>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        roleAuthService.requireAnyPermission(PermissionCodes.MESSAGES);
        return Result.ok(messageService.pageMessages(pageNum, pageSize));
    }

    @Operation(summary = "未读数量")
    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        roleAuthService.requireAnyPermission(PermissionCodes.MESSAGES);
        return Result.ok(messageService.countUnread());
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        roleAuthService.requireAnyPermission(PermissionCodes.MESSAGES);
        messageService.markRead(id);
        return Result.ok();
    }

    @Operation(summary = "全部标为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        roleAuthService.requireAnyPermission(PermissionCodes.MESSAGES);
        messageService.markAllRead();
        return Result.ok();
    }

    @Operation(summary = "批量标为已读")
    @PutMapping("/batch-read")
    public Result<Void> markBatchRead(@RequestBody Map<String, List<Long>> body) {
        roleAuthService.requireAnyPermission(PermissionCodes.MESSAGES);
        messageService.markBatchRead(body.get("ids"));
        return Result.ok();
    }
}
