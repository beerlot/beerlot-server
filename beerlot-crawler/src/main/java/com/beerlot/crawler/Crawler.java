package com.beerlot.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Crawler {
    private static final String BASE_URL = "https://www.ratebeer.com";
    private static final String PATH_TO_INPUT = "/Users/leeyj/workspace_devujin/beerlot-data/src/main/resources/beer-name-list-1.txt";
    private static final String DRIVER_ID = "webdriver.chrome.driver";
    private static final String DRIVER_PATH = "src/main/resources/chromedriver";

    private WebDriver driver;

    private void connectUsingService() {
        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(DRIVER_PATH))
                .usingAnyFreePort()
                .build();
        try {
            chromeDriverService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = new ChromeDriver(chromeDriverService);
    }

    private void connectDriver() {
        System.setProperty(DRIVER_ID, DRIVER_PATH);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        //options.addArguments("headless"); // If headless, then no browser is on.

        driver = new ChromeDriver(options);
    }

    private void quitDriver() {
        driver.quit();
    }

    public Beer crawl(String beerName) throws InterruptedException {
        connectDriver();

        //List<String> beerNames = new Reader().getListFromFile("src/main/resources/beer-name-list-1.txt");

        String link = getBeerDetailUrl(beerName);
        return getBeerDetailData(link);
    }

    private List<String> getBeerNamesFromFile() {
        try {
            File file = new File(PATH_TO_INPUT);
            Scanner scanner = new Scanner(file);
            List<String> beerNameList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String value = scanner.nextLine();
                beerNameList.add(value);
                System.out.println("beer is " + value);
            }
            return beerNameList;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getBeerDetailUrl(String query) {
        String url = UriComponentsBuilder.fromUriString(BASE_URL + "/search")
                .queryParam("q", query)
                .queryParam("tab", "beer")
                .toUriString();

        driver.get(url);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String link = driver.findElement(By.cssSelector("div.BeerTab___StyledDiv-gWeJQq.JvNzg"))
                .findElement(By.cssSelector("a"))
                .getAttribute("href");

        System.out.println("link is: " + link);

        return link;
    }

    private Beer getBeerDetailData(String link) {
        String url = UriComponentsBuilder.fromUriString(link).toUriString();
        try {
            driver.get(url);
            Thread.sleep(5000);

            WebElement button = driver.findElement(By.cssSelector("#root > div.App___StyledDiv-icXtoY.dKCeLR > div.App___StyledDiv2-gWnqez.dIfVTu > div > div > div > div.two-column-page-layout__InnerContainer-bOOfEQ.dcJCbA > div:nth-child(1) > div"))
                    .findElement(By.cssSelector("div.px-4.pb-4")).findElement(By.cssSelector("button"));
            /*new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("button.bGOCJz.kAVjHC")))
                    .click();*/
            System.out.println("button is: " + button.getText());
            Thread.sleep(10000);
            button.click();
            Thread.sleep(5000);

            Beer beer = new Beer();

            WebElement breweryAndName = driver.findElement(By.cssSelector("div.fj-sb.fa-s.mb-3"));
            String brewery = breweryAndName.findElement(By.id("styleLink")).getText();
            String name = breweryAndName.findElement(By.cssSelector("div")).findElement(By.cssSelector("div")).getText();

            beer.setBrewery(brewery);
            beer.setName(name);

            System.out.println("beer name is:" + name);


            WebElement countryAndCategoryAndVolume = driver.findElement(By.cssSelector("div.BeerCard___StyledDiv2-ieYeaq.eRqQUm.mb-3"));
            String from = countryAndCategoryAndVolume.findElement(By.cssSelector(".MuiTypography-root")).getText();
            String[] fromArray = from.split(",");
            String city = "";
            String country = "";

            if (fromArray.length == 3) {
                city = fromArray[1].replaceFirst(" ", "");
                country = fromArray[2].replaceFirst(" ", "");
            } else if (fromArray.length == 2) {
                city = fromArray[0].replaceFirst(" ", "");
                country = fromArray[1].replaceFirst(" ", "");
            }


            beer.setOriginCity(city);
            beer.setOriginCountry(country);


            WebElement categoryAndVolume = driver.findElement(By.cssSelector(".fj-s"));
            String category = categoryAndVolume.findElement(By.cssSelector("#styleLink")).getText();

            beer.setCategory(DataUtils.transformCategory(category));

            String volume = categoryAndVolume.findElement(By.cssSelector(".bRPQdN")).getText().replace("%", "");

            beer.setVolume(Float.valueOf(volume));

            String description = driver.findElement(By.cssSelector("#root > div.App___StyledDiv-icXtoY.dKCeLR > div.App___StyledDiv2-gWnqez.dIfVTu > div > div > div > div.two-column-page-layout__InnerContainer-bOOfEQ.dcJCbA > div:nth-child(1) > div"))
                    .findElement(By.cssSelector("div.px-4.pt-4"))
                    .findElement(By.cssSelector("div.MuiTypography-root.Text___StyledTypographyTypeless-bukSfn.pzIrn.colorized__WrappedComponent-hrwcZr.hwjOn.pre-wrap.MuiTypography-body2")).getText();

            beer.setDescription(description);


            WebElement glassAndCalory = driver.findElement(By.cssSelector("#root > div.App___StyledDiv-icXtoY.dKCeLR > div.App___StyledDiv2-gWnqez.dIfVTu > div > div > div > div.two-column-page-layout__InnerContainer-bOOfEQ.dcJCbA > div:nth-child(1) > div"))
                    .findElement(By.cssSelector("div.MuiCollapse-container.MuiCollapse-entered"))
                    .findElement(By.cssSelector("div.fa-s"));

            List<WebElement> elements = glassAndCalory.findElement(By.cssSelector("div.mt-3.mr-4"))
                    .findElements(By.cssSelector("span"));
            List<String> glasses = new ArrayList<>();
            for (WebElement element : elements) {
                glasses.add(element.getText());
                System.out.println("glass: " + element.getText());
            }

            beer.setGlassware(glasses);

            String calorieData = glassAndCalory.findElement(By.cssSelector("div.mt-3:nth-child(2)"))
                    .findElement(By.cssSelector("div.hJPZRX")).getText();

            String calorie = calorieData.split(" per ")[0].replace(" ", "").replace("cal", "");
            String calorieUnit = calorieData.split(" per ")[1].replace(" ", "").replace("ml", "");

            beer.setCalorie(Integer.parseInt(calorie));
            beer.setCalorieUnit(calorieUnit);

            return beer;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
