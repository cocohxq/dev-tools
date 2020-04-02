package com.dev.tool.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockUtils {

    private static Map<GroupEnum,ReentrantReadWriteLock> lockMap = new HashMap<>();
    static {
        for(GroupEnum groupEnum : GroupEnum.values()){
            lockMap.put(groupEnum,new ReentrantReadWriteLock());
        }
    }

    /**
     * 尝试锁定
     *
     * @return
     */
    public static boolean tryLock(GroupEnum groupEnum) {
        return lockMap.get(groupEnum).writeLock().tryLock();
    }

    public static boolean unLock(GroupEnum groupEnum){
        if(lockMap.get(groupEnum).isWriteLocked()){
            lockMap.get(groupEnum).writeLock().unlock();
        }
        return true;
    }


}
