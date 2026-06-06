package com.flowcloud.system.service;

import com.flowcloud.system.dto.TenantProfileDTO;
import com.flowcloud.system.vo.TenantProfileVO;

public interface TenantService {

    TenantProfileVO getCurrentTenant();

    void updateCurrentTenant(TenantProfileDTO dto);
}