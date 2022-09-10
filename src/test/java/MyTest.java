import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.geo.GeoServiceImpl.*;

public class MyTest {
    GeoServiceImpl geo;
    LocalizationServiceImpl localizationService;

    @BeforeEach
    public void init() {
        System.out.println("Начало теста");
        geo = new GeoServiceImpl();
        localizationService = new LocalizationServiceImpl();
    }

    @BeforeAll
    public static void startTests() {
        System.out.println("Начинаем тестирование");
    }

    @AfterEach
    public void finalTest() {
        System.out.println("Тест завершен");
        geo = null;
        localizationService = null;
    }

    @AfterAll
    public static void finishTests() {
        System.out.println("Тестирование программы завершено!");
    }

    @ParameterizedTest
    @MethodSource("argByIp")
    public void testGeoServiceImplByIp(String ip, Location expected) {
        Location result = geo.byIp(ip);

        if (!ip.equals(LOCALHOST) && !ip.equals(MOSCOW_IP) && !ip.equals(NEW_YORK_IP) &&
                !ip.equals("172.") && !ip.equals("96.") && !ip.equals("127.")) {
            assertNull(expected);
        } else {
            assertEquals(expected.getCity(), result.getCity());
            assertEquals(expected.getCountry(), result.getCountry());
            assertEquals(expected.getStreet(), result.getStreet());
            assertEquals(expected.getBuiling(), result.getBuiling());
        }
    }

    public static Stream<Arguments> argByIp() {
        return Stream.of(Arguments.of(LOCALHOST, new Location(null, null, null, 0)),
                Arguments.of(MOSCOW_IP, new Location("Moscow", Country.RUSSIA, "Lenina", 15)),
                Arguments.of(NEW_YORK_IP, new Location("New York", Country.USA, " 10th Avenue", 32)),
                Arguments.of("172.", new Location("Moscow", Country.RUSSIA, null, 0)),
                Arguments.of("96.", new Location("New York", Country.USA, null, 0)),
                Arguments.of("00.", null)
        );
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

    @Test
    public void testGeoServiceImplByCoordinates() {
        double latitude = 77.8, longitude = 88.9;
        String text = "Not implemented";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            geo.byCoordinates(latitude, longitude);
        });

        assertEquals(text, exception.getMessage());
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
