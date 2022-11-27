package com.share.co.kcl.dtp.monitor.security.authorization.metadatasource.impl;

import com.share.co.kcl.dtp.monitor.security.annotation.Permission;
import com.share.co.kcl.dtp.monitor.security.authorization.configattribute.ConsolePermissionConfigAttribute;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author kcl.co
 * @since 2022/02/19
 */
public class ConsolePermissionMetadataSource extends SecuredAnnotationSecurityMetadataSource {

    public ConsolePermissionMetadataSource() {
        this(new ServerPermissionMetadataExtractor());
    }

    public ConsolePermissionMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class ServerPermissionMetadataExtractor implements AnnotationMetadataExtractor<Permission> {

        public Collection<ConfigAttribute> extractAttributes(Permission permission) {
            return Arrays.stream(permission.value())
                    .map(ConsolePermissionConfigAttribute::new)
                    .collect(Collectors.toList());
        }
    }
}
