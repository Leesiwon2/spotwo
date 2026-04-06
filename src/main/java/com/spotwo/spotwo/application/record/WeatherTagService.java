package com.spotwo.spotwo.application.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherTagService {

  @Value("${weather.api-key:}")
  private String apiKey;

  private final WebClient webClient = WebClient.create();

  public String getWeatherTag(Double latitude, Double longitude, LocalDate visitDate) {
    // API 키 없으면 날짜 기반 기본값 반환
    if (apiKey == null || apiKey.isBlank()) {
      return getDefaultWeatherTag(visitDate);
    }

    try {
      String url = String.format(
          "https://api.openweathermap.org/data/2.5/weather" +
              "?lat=%s&lon=%s&appid=%s&lang=kr&units=metric",
          latitude, longitude, apiKey
      );

      WeatherResponse response = webClient.get()
          .uri(url)
          .retrieve()
          .bodyToMono(WeatherResponse.class)
          .block();

      if (response != null && response.weather() != null
          && !response.weather().isEmpty()) {
        return translateWeather(response.weather().get(0).main());
      }
    } catch (Exception e) {
      log.warn("날씨 API 호출 실패, 기본값 사용: {}", e.getMessage());
    }

    return getDefaultWeatherTag(visitDate);
  }

  private String translateWeather(String main) {
    return switch (main.toLowerCase()) {
      case "clear"       -> "맑음";
      case "clouds"      -> "흐림";
      case "rain", "drizzle" -> "비";
      case "snow"        -> "눈";
      case "thunderstorm" -> "천둥번개";
      default            -> "맑음";
    };
  }

  private String getDefaultWeatherTag(LocalDate date) {
    // 날씨 API 없을 때 계절 기반 기본값
    return switch (date.getMonth()) {
      case DECEMBER, JANUARY, FEBRUARY -> "눈";
      case JUNE, JULY, AUGUST -> "맑음";
      default -> "맑음";
    };
  }

  // 날씨 API 응답 DTO
  record WeatherResponse(java.util.List<Weather> weather) {}
  record Weather(String main, String description) {}
}