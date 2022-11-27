package com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.impl;

import com.share.co.kcl.dtp.monitor.security.annotation.Auth;
import com.share.co.kcl.dtp.monitor.security.authorization.configattribute.ConsoleAuthConfigAttribute;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;

import java.util.Collection;
import java.util.Collections;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class ConsoleAuthMetadataSource extends SecuredAnnotationSecurityMetadataSource {
    public ConsoleAuthMetadataSource() {
        this(new ServerAuthMetadataExtractor());
    }

    public ConsoleAuthMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class ServerAuthMetadataExtractor implements AnnotationMetadataExtractor<Auth> {

        public Collection<ConfigAttribute> extractAttributes(Auth auth) {
            return Collections.singletonList(new ConsoleAuthConfigAttribute());
        }
    }

}
