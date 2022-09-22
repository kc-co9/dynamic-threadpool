package com.share.co.kcl.dtp.spring.processor;

import com.share.co.kcl.dtp.spring.meta.DynamicPoolConfig;
import com.share.co.kcl.dtp.spring.meta.DynamicPoolConfigRepository;
import com.share.co.kcl.dtp.spring.meta.DynamicPoolConfigMeta;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

@SuppressWarnings("NullableProblems")
public class DynamicPoolConfigSelector implements DeferredImportSelector, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPoolConfigSelector.class);

    private Environment environment;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        String serverCode = environment.getProperty(DynamicPoolConfigMeta.SERVER_CODE, String.class);
        String serverMonitor = environment.getProperty(DynamicPoolConfigMeta.SERVER_MONITOR, String.class);

        if (StringUtils.isBlank(serverCode) || StringUtils.isBlank(serverMonitor)) {
            LOG.error("dynamic thread pool config is error");
            return new String[0];
        }

        DynamicPoolConfig dynamicPoolConfig = new DynamicPoolConfig();
        dynamicPoolConfig.setServerCode(serverCode);
        dynamicPoolConfig.setServerMonitor(serverMonitor);
        DynamicPoolConfigRepository.save(dynamicPoolConfig);

        return new String[]{DynamicPoolConfigProcessor.class.getName()};
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
