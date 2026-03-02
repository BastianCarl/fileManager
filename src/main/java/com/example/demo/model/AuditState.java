package com.example.demo.model;

import lombok.Getter;
@Getter
public enum AuditState {
    NOT_FOUND(0),
    CHECKING(1),
    METADATA_STARTED(2),
    METADATA_DONE(3),
    FILE_SERVICE_STARTED(4),
    FILE_SERVICE_DONE(5),
    DISK_STARTED(6),
    DISK_DONE(7),
    CLEANING_STARTED(8),
    CLEANING_DONE(9),
    DONE(10);
    final int order;
    AuditState(int order) {
        this.order = order;
    }
}