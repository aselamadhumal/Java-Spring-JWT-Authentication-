package com.jwtauth.jwtauth.repository;

import com.jwtauth.jwtauth.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository  extends JpaRepository<AuditLog,Long> {
}
