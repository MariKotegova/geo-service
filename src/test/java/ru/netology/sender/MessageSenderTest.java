package ru.netology.sender;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.i18n.LocalizationService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageSenderTest {

    @BeforeEach
    public void init() {
        System.out.println("Начало теста");
    }

    @BeforeAll
    public static void startTests() {
        System.out.println("Начинаем тестирование");
    }

    @AfterEach
    public void finalTest() {
        System.out.println("\nТест завершен");
    }

    @AfterAll
    public static void finishTests() {
        System.out.println("Тестирование программы завершено!");
    }

    @ParameterizedTest
    @MethodSource("message")
    public void testMessageSenderSend(Map<String, String> headers, Location loc, String expected) {
        LocalizationService localizationService = Mockito.mock(LocalizationService.class);
        GeoService geoService = Mockito.mock(GeoService.class);

        // пишем что для этого метода надо вернуть текст ниже
        Mockito.when(geoService.byIp(headers.get(MessageSenderImpl.IP_ADDRESS_HEADER)))     //"172.0.32.11"))
                .thenReturn(loc);
        Mockito.when(localizationService.locale(geoService.byIp(headers.get(MessageSenderImpl.IP_ADDRESS_HEADER)).getCountry()))
                .thenReturn(expected);

        MessageSenderImpl messageSender = new MessageSenderImpl(geoService, localizationService);
        String result = messageSender.send(headers);

        assertEquals(expected, result);
    }

    public static Stream<Arguments> message() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.0.32.11");

        Map<String, String> headers1 = new HashMap<String, String>();
        headers1.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.44.183.149");

        return Stream.of(Arguments.of(headers, new Location("Moscow", Country.RUSSIA, "Lenina", 15),
                        "Добро пожаловать"),
                Arguments.of(headers1, new Location("New York", Country.USA, " 10th Avenue", 32),
                        "Welcome"));
    }
}
