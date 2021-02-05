package com.springcache;

public class Member {

    private long idx;
    private String email;
    private String name;

    public Member() {
    }

    public Member(long idx, String email, String name) {
        this.idx = idx;
        this.email = email;
        this.name = name;
    }

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
