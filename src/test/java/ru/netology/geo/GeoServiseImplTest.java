package ru.netology.geo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.geo.GeoServiceImpl.*;
import static ru.netology.geo.GeoServiceImpl.NEW_YORK_IP;

public class GeoServiseImplTest {
    GeoServiceImpl geo;

    @BeforeEach
    public void init() {
        System.out.println("Начало теста");
        geo = new GeoServiceImpl();
    }

    @BeforeAll
    public static void startTests() {
        System.out.println("Начинаем тестирование");
    }

    @AfterEach
    public void finalTest() {
        System.out.println("Тест завершен");
        geo = null;
    }

    @AfterAll
    public static void finishTests() {
        System.out.println("Тестирование программы завершено!");
    }

    @ParameterizedTest
    @MethodSource("argByIp")
    public void testGeoServiceImplByIp(String ip, Location expected) {
        Location result = geo.byIp(ip);

        assertEquals(expected.getCity(), result.getCity());
        assertEquals(expected.getCountry(), result.getCountry());
        assertEquals(expected.getStreet(), result.getStreet());
        assertEquals(expected.getBuiling(), result.getBuiling());
    }

    public static Stream<Arguments> argByIp() {
        return Stream.of(Arguments.of(LOCALHOST, new Location(null, null, null, 0)),
                Arguments.of(MOSCOW_IP, new Location("Moscow", Country.RUSSIA, "Lenina", 15)),
                Arguments.of(NEW_YORK_IP, new Location("New York", Country.USA, " 10th Avenue", 32)),
                Arguments.of("172.", new Location("Moscow", Country.RUSSIA, null, 0)),
                Arguments.of("96.", new Location("New York", Country.USA, null, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("argByIpNull")
    public void testGeoServiceImplByIpNull(String ip) {
        Location result = geo.byIp(ip);
        assertNull(result);
    }

    public static Stream<Arguments> argByIpNull() {
        return Stream.of(Arguments.of("00."));
    }

    @Test
    public void testGeoServiceImplByCoordinates() {
        double latitude = 77.8, longitude = 88.9;
        String text = "Not implemented";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            geo.byCoordinates(latitude, longitude);
        });

        assertEquals(text, exception.getMessage());
    }
}
