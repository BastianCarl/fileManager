package com.example.demo.model;

import lombok.Getter;
@Getter
public enum AuditState {
    NOT_FOUND(0),
    METADATA(1),
    FILE_SERVICE(2),
    DISK(3),
    DONE(4);
    final int order;
    AuditState(int order) {
        this.order = order;
    }
}