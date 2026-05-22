package com.tiktokgame.likequest_backend.services;

import com.tiktokgame.likequest_backend.exceptions.ProfileIsPrivate;
import com.tiktokgame.likequest_backend.exceptions.ProfileNotFoundException;
import com.tiktokgame.likequest_backend.exceptions.VideosNotPublicException;
import com.tiktokgame.likequest_backend.models.login.LoginResult;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class TikTokSeleniumService {

    public LoginResult login(String username) throws ProfileNotFoundException,
            VideosNotPublicException, ProfileIsPrivate {
        boolean isPublic = false;
        WebDriver driver = null;
        ArrayList<String> urlVideosLiked = new ArrayList<>();
        try {
            driver = createDriver(username);
            if (!existAccount(username, driver)) {
                throw new ProfileNotFoundException(username);
            }
            isPublic = isProfilePublic(username, driver);
            if (!isPublic) {
                throw new ProfileIsPrivate(username);
            }
            urlVideosLiked = likedVideos(username, driver);
            if (urlVideosLiked.isEmpty()) {
                throw new VideosNotPublicException(username);
            }
            return new LoginResult(username, "true", urlVideosLiked);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public WebDriver createDriver(String username) {
        WebDriver driver = new ChromeDriver(driverOptions());
        driver.get("https://www.tiktok.com/@" + username);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return driver;
    }

    public ChromeOptions driverOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36"
        );
        return options;
    }


    public boolean existAccount(String username, WebDriver driver) {
        List<WebElement> existTexts =
                driver.findElements(By.xpath("//p[contains(text(),'No se pudo encontrar esta cuenta')]"));
        return existTexts.isEmpty();
    }


    public boolean isProfilePublic(String username, WebDriver driver) {
        String xpath = "//p[contains(text(),'Esta cuenta es privada')]";
        boolean publicAccount = false;
        try {
            // PAUSA DE 1O SEGUNDOS HASTA QUE CARGE EL BODY
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            // BUSCA ELEMENTO
            List<WebElement> privateTexts = driver.findElements(By.xpath(xpath));
            publicAccount = privateTexts.isEmpty();
            return publicAccount;

        } catch (Exception e) {
            System.out.println("Validacion Profile Public ERROR : " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String> likedVideos(String username, WebDriver driver) {
        ArrayList<String> urlLikedVideos = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Cerrar banner de cookies si aparece
        try {
            WebElement cookieBanner = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("tiktok-cookie-banner button"))
            );
            cookieBanner.click();
        } catch (Exception e) {
            System.out.println("No hay banner de cookies , continuar ...");
        }

        try {
            // Esperar a la pestaña "Me gustó"
            WebElement likedTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("p[data-e2e='liked-tab']"))
            );

            // Click con JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", likedTab);

            // Intentamos esperar a que aparezcan presencia de  los videos
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div[data-e2e='user-liked-item'] a"))
                );
                // Si llegamos aquí, hay videos. Los guardamos.
                List<WebElement> likedVideos =
                        driver.findElements(By.cssSelector("div[data-e2e='user-liked-item'] a"));

                for (WebElement video : likedVideos) {
                    urlLikedVideos.add(video.getAttribute("href"));
                }
            } catch (org.openqa.selenium.TimeoutException e) {
                // Si salta esto, es que pasaron 10 segundos y los videos no aparecieron (son privados)
                System.out.println("Timeout: Los videos no son públicos o no existen.");
            }

        } catch (Exception e) {
            System.out.println("Error al intentar acceder a la pestaña de likes: " + e.getMessage());
        }

        return urlLikedVideos;
    }
}
