package com.example.demo.model;

import lombok.Getter;
@Getter
public enum AuditState {
    NOT_FOUND(0),
    CHECKING(1),
    METADATA(2),
    FILE_SERVICE(3),
    DISK(4),
    CLEANING(5),
    DONE(6);
    final int order;
    AuditState(int order) {
        this.order = order;
    }
}