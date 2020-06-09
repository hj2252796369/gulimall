package com.project.gulimall.member.fegin;

import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author by        hujie
 * @class_name CouponFeignService
 * @description
 *      coupon的声明式远程调用
 * @create_date 22:20 2020-06-09
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/test")
    public R test();

}
