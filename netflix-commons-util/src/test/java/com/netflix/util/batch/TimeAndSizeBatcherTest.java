package com.netflix.util.batch;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.netflix.util.batch.TimeAndSizeBatchingPolicy;

public class TimeAndSizeBatcherTest {
    @Test
    public void testBySize() throws Exception {
        final List<String> result = Lists.newArrayList();
        
        BatchingPolicy policy = new TimeAndSizeBatchingPolicy(2, 1, TimeUnit.SECONDS);
        Batcher<String> batcher = policy.create(new Function<List<String>, Boolean>() {
            public Boolean apply(List<String> list) {
                result.addAll(list);
                return true;
            }
        });
        
        batcher.add("A");
        batcher.add("B");
        Thread.sleep(100);
        batcher.shutdown();
     
        Assert.assertEquals(2, result.size());
    }
    
    @Test
    public void testByTime() throws Exception {
        final List<String> result = Lists.newArrayList();
        
        BatchingPolicy policy = new TimeAndSizeBatchingPolicy(10, 1, TimeUnit.SECONDS);
        Batcher<String> batcher = policy.create(new Function<List<String>, Boolean>() {
            public Boolean apply(List<String> list) {
                result.addAll(list);
                return true;
            }
        });
        
        batcher.add("A");
        batcher.add("B");
        
        Assert.assertEquals(0, result.size());
        
        Thread.sleep(2010);
        
        Assert.assertEquals(2, result.size());
    }
    
    @Test
    public void testStress() throws Exception {
        BatchingPolicy policy = new TimeAndSizeBatchingPolicy(10, 100, TimeUnit.MILLISECONDS);
        Batcher<String> batcher = policy.create(new Function<List<String>, Boolean>() {
            public Boolean apply(List<String> list) {
                System.out.println(list);
                return true;
            }
        });
        
        for (int i = 0; i < 1000; i++) {
            batcher.add(Integer.toString(i));
            Thread.sleep(20 + new Random().nextInt(50));
        }
        
        Thread.sleep(2000);
    }
}
