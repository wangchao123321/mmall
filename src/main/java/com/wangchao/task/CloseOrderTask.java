package com.wangchao.task;

import com.wangchao.common.Const;
import com.wangchao.common.RedissionManager;
import com.wangchao.service.IOrderService;
import com.wangchao.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedissionManager redissionManager;

    @PreDestroy
    public void delLock(){
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1(){
        int hour =2;
        orderService.closedOrder(hour);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2(){
        long lockTimeout = Long.parseLong("50000");
        Long setnxResult= RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult !=null && setnxResult.intValue() == 1){
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            log.info("没有获得分布式锁{},",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
    }


    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3(){
        long lockTimeout = Long.parseLong("50000");
        Long setnxResult= RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult !=null && setnxResult.intValue() == 1){
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            log.info("没有获得分布式锁{},",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            String lockValueStr=RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
                String getSetResult=RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
                if(getSetResult  ==null || (getSetResult != null && StringUtils.equals(lockValueStr,getSetResult))){
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获取到分布式锁");
                }
            }else{
                log.info("没有获取到分布式锁");
            }
        }
    }

    public void closeOrderTaskV4(){
        RLock lock=redissionManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean getLock=false;
        try {
            if(getLock=lock.tryLock(2,5, TimeUnit.SECONDS)){
                int hour =2;
                orderService.closedOrder(hour);
            }else{

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(!getLock){
                return;
            }
            lock.unlock();
        }
    }

    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,50);
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour =2;
        orderService.closedOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("====================================================");
    }
}
