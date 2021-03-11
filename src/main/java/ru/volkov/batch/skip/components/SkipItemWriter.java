package ru.volkov.batch.skip.components;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SkipItemWriter implements ItemWriter<String> {

    private boolean skip = false;
    private int attemptCount = 0;

    @Override
    public void write(List<? extends String> list) throws Exception {

        for (String s : list) {
            System.out.println("Writing item - '{" + s + "}'");

            if(skip && "-84".equals(s)) {
                attemptCount++;

                System.out.println("Writing of item - '{" + s + "}'" + " failed");
                throw new CustomRetryableException("Write failed. Attempt - '{" + attemptCount + "}'");
            } else {
                System.out.println(s);
            }
        }
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
