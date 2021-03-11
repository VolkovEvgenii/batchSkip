package ru.volkov.batch.skip.components;

import org.springframework.batch.item.ItemProcessor;

public class SkipItemProcessor implements ItemProcessor<String, String> {

    private boolean skip = false;
    private int attemptCount = 0;

    @Override
    public String process(String s) throws Exception {
        System.out.println("Processing of item - '{" + s + "}'");
        if(skip && "42".equals(s)) {
            attemptCount++;
            System.out.println("Processing of item - '{" + s + "}'" + " failed");
            throw new CustomRetryableException("Process failed. Attempt - '{" + attemptCount + "}'");
        } else {
            return String.valueOf(Integer.valueOf(s) * -1);
        }
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
