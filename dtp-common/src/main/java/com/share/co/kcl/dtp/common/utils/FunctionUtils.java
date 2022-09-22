package com.share.co.kcl.dtp.common.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FunctionUtils {

    private FunctionUtils() {
    }


    public static <T, R> List<R> mappingList(List<T> list, Function<T, R> function) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        return list.stream().map(function).collect(Collectors.toList());
    }

    public static <T, R> List<R> mappingFlatList(List<T> list, Function<T, List<R>> function) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        return list.stream().map(function).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T, R> List<R> mappingNonNullList(List<T> list, Function<T, R> function) {
        return mappingList(list, function).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, R> List<R> mappingDistinctList(List<T> list, Function<T, R> function) {
        return mappingList(list, function).stream().distinct().collect(Collectors.toList());
    }

    public static <T, R> Set<R> mappingSet(List<T> list, Function<T, R> function) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashSet<>();
        }

        return list.stream().map(function).collect(Collectors.toSet());
    }

    public static <T, R> Set<R> mappingSet(Set<T> set, Function<T, R> function) {
        if (CollectionUtils.isEmpty(set)) {
            return new HashSet<>();
        }

        return set.stream().map(function).collect(Collectors.toSet());
    }

    public static <T, K, V> Map<K, V> mappingMap(List<T> list, Function<T, K> key, Function<T, V> value) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }

        return list.stream().collect(Collectors.toMap(key, value, (a1, a2) -> a1));
    }

    public static <T, R> Map<R, List<T>> mappingGroup(List<T> list, Function<T, R> function) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }

        return list.stream().collect(Collectors.groupingBy(function));
    }

    public static <T, R, P> Map<R, List<P>> mappingGroup(List<T> list, Function<T, R> function, Function<T, P> mapping) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }

        return list.stream().collect(
                Collectors.groupingBy(function, Collectors.mapping(mapping, Collectors.toList())));
    }

    public static <T> Optional<T> mappingFirst(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }

        return list.stream().findFirst();
    }

    public static <T> Optional<T> mappingLast(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(list.size() - 1));
    }

    public static <T, R> boolean hasNullList(List<T> list, Function<T, R> function) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        boolean hasNull = false;
        hasNull |= list.stream().anyMatch(Objects::isNull);
        hasNull |= list.stream().map(function).anyMatch(Objects::isNull);
        return hasNull;
    }

    public static <T, R> boolean hasDuplicateList(List<T> list, Function<T, R> function) {
        List<R> tmpList = FunctionUtils.mappingList(list, function);
        Set<R> tmpSet = FunctionUtils.mappingSet(list, function);
        return tmpList.size() != tmpSet.size();
    }
}
