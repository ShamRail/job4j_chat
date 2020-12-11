package ru.job4j.chat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;

public class Demo {

    public static void main(String[] args) {
        int count = 0;
        Set<Long> distinct = new HashSet<>();
        for (int a = 1; a <= 33; a++) {
            for (int b = 1; b <= 33; b++) {
                for (int c = 1; c <= 33; c++) {
                    for (int d = 1; d <= 33; d++) {
                        if (new HashSet<>(List.of(a, b, c, d)).size() == 4) {
                            long left = (long) (pow(a, 3) + pow(b, 3));
                            long right = (long) (pow(c, 3) + pow(d, 3));
                            //left >= 1729 && count <= 5 && && !distinct.contains(left)
                            if (left == right) {
                                System.out.printf(
                                        "%d = %d^3 + %d^3 = %d^3 + %d^3%n",
                                        left, a, b, c, d
                                );
                                distinct.add(left);
                                count++;
                            }
                        }
                    }
                }
            }
        }
    }

}
