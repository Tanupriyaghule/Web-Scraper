package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebsiteAutomation {
    ChromeDriver driver;

    public WebsiteAutomation() {
        System.setProperty("webdriver.chrome.driver", "path_to_chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    public void run() throws IOException {
        // Navigate to the website
        driver.get("https://www.scrapethissite.com/pages/");

        // Click on "Hockey Teams: Forms, Searching and Pagination"
        WebElement hockeyTeamsLink = driver.findElement(By.linkText("Hockey Teams: Forms, Searching and Pagination"));
        hockeyTeamsLink.click();

        // Collect team data with win % less than 40%
        List<HashMap<String, Object>> hockeyTeamData = new ArrayList<>();
        for (int page = 1; page <= 4; page++) {
            List<WebElement> rows = driver.findElements(By.xpath("//table[@id='table1']/tbody/tr"));
            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath("./td[1]")).getText();
                int year = Integer.parseInt(row.findElement(By.xpath("./td[2]")).getText());
                double winPercentage = Double.parseDouble(row.findElement(By.xpath("./td[3]")).getText());
                if (winPercentage < 0.40) {
                    HashMap<String, Object> teamInfo = new HashMap<>();
                    teamInfo.put("Epoch Time of Scrape", Instant.now().toEpochMilli());
                    teamInfo.put("Team Name", teamName);
                    teamInfo.put("Year", year);
                    teamInfo.put("Win %", winPercentage);
                    hockeyTeamData.add(teamInfo);
                }
            }
            // Go to the next page
            if (page < 4) {
                WebElement nextPageButton = driver.findElement(By.linkText("Next"));
                nextPageButton.click();
            }
        }

        // Convert hockey team data to JSON and write to file
        ObjectMapper objectMapper = new ObjectMapper();
        File hockeyTeamFile = new File("output/hockey-team-data.json");
        objectMapper.writeValue(hockeyTeamFile, hockeyTeamData);

        // Assert file presence and not empty
        assertFileNotEmpty(hockeyTeamFile);

        // Navigate to the Oscar winning films page
        driver.get("https://www.scrapethissite.com/pages/");

        // Click on "Oscar Winning Films"
        WebElement oscarFilmsLink = driver.findElement(By.linkText("Oscar Winning Films"));
        oscarFilmsLink.click();

        // Collect Oscar winning films data
        List<HashMap<String, Object>> oscarWinnerData = new ArrayList<>();
        List<WebElement> years = driver.findElements(By.xpath("//div[@id='menu1']/ul/li"));
        for (WebElement yearElement : years) {
            int year = Integer.parseInt(yearElement.getText());
            yearElement.click();
            List<WebElement> movies = driver.findElements(By.xpath("//div[@id='wrapper']/table/tbody/tr"));
            for (int i = 0; i < Math.min(5, movies.size()); i++) {
                WebElement movie = movies.get(i);
                String title = movie.findElement(By.xpath("./td[2]")).getText();
                int nominations = Integer.parseInt(movie.findElement(By.xpath("./td[3]")).getText());
                int awards = Integer.parseInt(movie.findElement(By.xpath("./td[4]")).getText());
                boolean isWinner = movie.findElement(By.xpath("./td[5]")).getText().equals("Best Picture");
                HashMap<String, Object> movieInfo = new HashMap<>();
                movieInfo.put("Epoch Time of Scrape", Instant.now().toEpochMilli());
                movieInfo.put("Year", year);
                movieInfo.put("Title", title);
                movieInfo.put("Nomination", nominations);
                movieInfo.put("Awards", awards);
                movieInfo.put("isWinner", isWinner);
                oscarWinnerData.add(movieInfo);
            }
        }

        // Convert Oscar winner data to JSON and write to file
        File oscarWinnerFile = new File("output/oscar-winner-data.json");
        objectMapper.writeValue(oscarWinnerFile, oscarWinnerData);

        // Assert file presence and not empty
        assertFileNotEmpty(oscarWinnerFile);

        // Quit WebDriver
        driver.quit();
    }

    private static void assertFileNotEmpty(File file) {
        assert file.exists() && file.length() > 0 : "File does not exist or is empty: " + file.getName();
    }

    public static void main(String[] args) throws IOException {
        WebsiteAutomation websiteAutomation = new WebsiteAutomation();
        websiteAutomation.run();
    }
}

