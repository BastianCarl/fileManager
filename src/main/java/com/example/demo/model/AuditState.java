package com.example.demo.model;

import lombok.Getter;
@Getter
public enum AuditState {
    NOT_FOUND(0),
    CHECKING_STARTED(1),
    CHECKING_DONE(2),
    METADATA_STARTED(3),
    METADATA_DONE(4),
    FILE_SERVICE_STARTED(5),
    FILE_SERVICE_DONE(6),
    DISK_STARTED(7),
    DISK_DONE(8),
    CLEANING_STARTED(9),
    CLEANING_DONE(10),
    DONE(11);
    final int order;
    AuditState(int order) {
        this.order = order;
    }
}