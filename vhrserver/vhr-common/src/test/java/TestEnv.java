import org.javaboy.vhr.config.HttpClientConfig;

public class TestEnv {
    public static void main(String[] args) {
        HttpClientConfig httpClientConfig = new HttpClientConfig();
        httpClientConfig.testEnv();
    }
}
