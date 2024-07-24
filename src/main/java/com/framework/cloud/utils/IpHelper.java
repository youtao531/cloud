package com.framework.cloud.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.Serializable;

/**
 * IP解析工具
 *
 * @author Lcc 2024/5/29 下午12:43
 */
@Slf4j
public class IpHelper {

    /**
     * 解析IPv4地址
     *
     * @param ip IPv4地址
     * @return IPv4地址对应区域信息
     */
    public static IpInfo of(String ip) {
        return Ip2RegionAlgorithm.instance.parseIp2Region(ip);
    }

    private enum Ip2RegionAlgorithm {
        instance,
        ;

        private final Searcher searcher;

        Ip2RegionAlgorithm() {
            try {
                ClassPathResource classPathResource = new ClassPathResource("ip/ip2region.xdb");
                byte[] bytes = StreamUtils.copyToByteArray(classPathResource.getInputStream());
                this.searcher = Searcher.newWithBuffer(bytes);
                log.info(">>>>>>>>>> ip-region-classpath: {}", classPathResource.getPath());
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        /**
         * 解析IPv4地址
         *
         * @param ip IPv4地址
         * @return IPv4地址对应区域信息
         */
        public IpInfo parseIp2Region(String ip) {
            try {
                String region = this.searcher.search(ip);
                if (null != region) {
                    // 中国|0|浙江省|杭州市|阿里云
                    String[] split = region.split("\\|");
                    String country = split[0], province = split[2], city = split[3], isp = split[4];
                    return new IpInfo(country, province, city, isp, region);
                }
            } catch (Exception e) {
                log.warn("获取IP地址所在区域异常：{}", e.getLocalizedMessage(), e);
            }
            String unknown = "Unknown";
            return new IpInfo(unknown, unknown, unknown, unknown, unknown);
        }
    }

    /**
     * @param country  国家名称
     *                 <p>
     *                 例如：中国、美国
     *                 </p>
     * @param province 省份名称
     *                 <p>
     *                 例如：浙江省、江西省、加利福尼亚
     *                 </p>
     * @param city     城市名称
     *                 <p>
     *                 例如：杭州市、上饶市、洛杉矶
     *                 </p>
     * @param isp      ISP 名称
     *                 <p>
     *                 例如：联通、电信、移动、阿里云、华数
     *                 </p>
     * @param detail   详细信息
     *                 <p>
     *                 例如：中国|浙江省|杭州市|阿里云
     *                 </p>
     */
    public record IpInfo(String country, String province, String city, String isp,
                         String detail) implements Serializable {

    }
}
