package com.aop.history;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class History {

    @Id @GeneratedValue
    private long id;

    @Column
    private long userIdx;

    @Column
    private Date updateDate;

    public History() {
    }

    public History(long userIdx) {
        this.userIdx = userIdx;
        this.updateDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long idx) {
        this.id = idx;
    }

    public long getUserId() {
        return userIdx;
    }

    public void setUserIdx(long userIdx) {
        this.userIdx = userIdx;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}
