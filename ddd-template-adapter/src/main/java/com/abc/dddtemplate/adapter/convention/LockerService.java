package com.abc.dddtemplate.adapter.convention;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.convention.aggregates.Locker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.util.Date;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LockerService {
    private final AggregateRepository<Locker, Long> lockerRepository;

    public boolean acquire(String name, String pwd, Duration lockDuration) {
        Date now = new Date();
        Locker locker = lockerRepository.findOne((root, cq, cb) -> {
            cq.where(cb.equal(root.get("name"), name));
            return null;
        }).orElseGet(() -> {
            try {
                return Locker.builder()
                        .name(name)
                        .lockAt(DateUtils.parseDate("1970-01-01", "yyyy-MM-dd"))
                        .unlockAt(DateUtils.parseDate("1970-01-01", "yyyy-MM-dd"))
                        .pwd("")
                        .build();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        });
        boolean result = locker.acquire(pwd, lockDuration, now);
        if (result) {
            try {
                UnitOfWork.saveEntities(locker);
            } catch (Exception ex) {
                result = false;
            }
        }
        log.info(String.format("获取锁 name=%s pwd=%s duration=%d acquired=%s", name, pwd, lockDuration.getSeconds(), result));
        return result;
    }

    public boolean release(String name, String pwd) {
        Date now = new Date();
        Locker locker = lockerRepository.findOne((root, cq, cb) -> {
            cq.where(cb.equal(root.get("name"), name));
            return null;
        }).orElseGet(null);
        boolean result;
        if (locker == null) {
            result = true;
        } else {
            result = locker.release(pwd, now);
            if (result) {
                try {
                    UnitOfWork.saveEntities(locker);
                } catch (Exception ex) {
                    result = false;
                }
            }
        }
        log.info(String.format("释放锁 name=%s pwd=%s released=%s", name, pwd, result));
        return result;
    }
}
