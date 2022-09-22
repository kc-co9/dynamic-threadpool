package com.share.co.kcl.dtp.common.generator;

import java.util.Random;
import java.util.UUID;

public class StringGenerator {

    private StringGenerator() {
    }

    public static class UUIDGenerator implements Generator<String> {
        @Override
        public String next() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    public static class RandomGenerator implements Generator<String> {
        @Override
        public String next() {
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }
    }

    public static class TimestampGenerator implements Generator<String> {
        @Override
        public String next() {
            return String.valueOf(System.currentTimeMillis());
        }
    }

}
