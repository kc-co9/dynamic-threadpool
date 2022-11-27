package com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.impl;

import com.share.co.kcl.dtp.monitor.security.annotation.Sign;
import com.share.co.kcl.dtp.monitor.security.authorization.configattribute.MonitorSignConfigAttribute;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;

import java.util.Collection;
import java.util.Collections;

public class MonitorSignMetadataSource extends SecuredAnnotationSecurityMetadataSource {

    public MonitorSignMetadataSource() {
        this(new MonitorSignMetadataExtractor());
    }

    public MonitorSignMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class MonitorSignMetadataExtractor implements AnnotationMetadataExtractor<Sign> {

        public Collection<ConfigAttribute> extractAttributes(Sign sign) {
            return Collections.singletonList(new MonitorSignConfigAttribute());
        }
    }
}
