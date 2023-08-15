package com.gaoda.jdbc.tx;

import java.sql.Connection;

/**
 * 定义TransactionStatus，表示当前事务状态：
 * 目前仅封装了一个Connection，将来如果扩展，则可以将事务的传播模式存储在里面。
 */
public class TransactionStatus {

    final Connection connection;

    public TransactionStatus(Connection connection) {
        this.connection = connection;
    }
}