package com.example.demo.model;

import lombok.Getter;
@Getter
public enum AuditState {
    NOT_FOUND(0),
    METADATA(1),
    EXTERNAL_PROVIDE(2),
    DISK_WORK(3),
    DONE(4);
    final int order;
    AuditState(int order) {
        this.order = order;
    }
}