package com.blockent.memoapp.model;

import java.util.List;

public class MemoList {
    private int error;
    private int count;
    private List<Memo> list;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Memo> getList() {
        return list;
    }

    public void setList(List<Memo> list) {
        this.list = list;
    }
}
