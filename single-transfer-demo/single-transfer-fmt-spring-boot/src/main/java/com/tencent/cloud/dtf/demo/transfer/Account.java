package com.tencent.cloud.dtf.demo.transfer;

import java.io.Serializable;

/**
 * 账户
 *
 * @author hongweizhu
 */
public class Account implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4605514873966460484L;

    public Account() {

    }

    public Account(Integer id) {
        this.id = id;

    }

    private Integer id;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

}
