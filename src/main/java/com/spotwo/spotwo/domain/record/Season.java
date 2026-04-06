package com.spotwo.spotwo.domain.record;

import java.time.LocalDate;
import java.time.Month;

public enum Season {
  SPRING, SUMMER, FALL, WINTER;

  public static Season from(LocalDate date) {
    Month month = date.getMonth();
    return switch (month) {
      case MARCH, APRIL, MAY       -> SPRING;
      case JUNE, JULY, AUGUST      -> SUMMER;
      case SEPTEMBER, OCTOBER, NOVEMBER -> FALL;
      case DECEMBER, JANUARY, FEBRUARY  -> WINTER;
    };
  }

  public String toKorean() {
    return switch (this) {
      case SPRING -> "봄";
      case SUMMER -> "여름";
      case FALL   -> "가을";
      case WINTER -> "겨울";
    };
  }
}