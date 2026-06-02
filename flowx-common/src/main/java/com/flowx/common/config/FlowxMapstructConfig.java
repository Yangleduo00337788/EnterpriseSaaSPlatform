package com.flowx.common.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Shared MapStruct configuration.
 */
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FlowxMapstructConfig {
}
