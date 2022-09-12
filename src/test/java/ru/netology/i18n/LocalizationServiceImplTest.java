package ru.netology.i18n;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalizationServiceImplTest {
    LocalizationServiceImpl localizationService;

    @BeforeEach
    public void init() {
        System.out.println("Начало теста");
        localizationService = new LocalizationServiceImpl();
    }

    @BeforeAll
    public static void startTests() {
        System.out.println("Начинаем тестирование");
    }

    @AfterEach
    public void finalTest() {
        System.out.println("Тест завершен");
        localizationService = null;
    }

    @AfterAll
    public static void finishTests() {
        System.out.println("Тестирование программы завершено!");
    }

    @ParameterizedTest
    @MethodSource("localization")
    public void testLocalizationServiseImplLocale(Country country, String text) {
        String result = localizationService.locale(country);

        assertEquals(text, result);
    }

    public static Stream<Arguments> localization() {
        return Stream.of(Arguments.of(Country.RUSSIA, "Добро пожаловать"),
                Arguments.of(Country.BRAZIL, "Welcome"));
    }
}
