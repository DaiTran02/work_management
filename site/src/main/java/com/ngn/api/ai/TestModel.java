package com.ngn.api.ai;

import lombok.Data;

@Data
public class TestModel {
    public String model;
    public String created_at;
    public String response;
    public boolean done;
    public String done_reason;
    public long total_duration;
    public long load_duration;
    public long prompt_eval_count;
    public long prompt_eval_duration;
    public long eval_count;
    public long eval_duration;
}
