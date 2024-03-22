package com.ninjaone.dundie_awards;

import org.springframework.stereotype.Component;


@Component
// we could consider use lombok annotations to avoid always typing get and setters for class
public class AwardsCache {
    private int totalAwards;

    public void setTotalAwards(int totalAwards) {
        this.totalAwards = totalAwards;
    }

    public int getTotalAwards(){
        return totalAwards;
    }

    public void addOneAward(){
        this.totalAwards += 1;
    }
}
