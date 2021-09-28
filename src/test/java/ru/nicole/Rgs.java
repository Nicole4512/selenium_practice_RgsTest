package ru.nicole;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.Before;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;


public class Rgs {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
        public void TestBefore(){
            System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
            driver = new ChromeDriver();
            driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 10, 1000);
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            driver.manage().window().maximize();
            String baseUrl = "https://www.rgs.ru/";
            driver.get(baseUrl);
        }

    @Test
    public void TestRGS(){
        //Выбрать меню
        WebElement menu = driver.findElement(By.xpath("//a[contains(text(), 'Меню') and contains(@class, 'hidden-xs')]"));
        wait.until(ExpectedConditions.elementToBeClickable(menu));
        menu.click();


        //Выбрать пункт "Компаниям"
        WebElement company = driver.findElement(By.xpath("//a[contains(text(), 'Компаниям') and contains(@href, 'juristic_person')]"));
        wait.until(ExpectedConditions.elementToBeClickable(company));
        company.click();

        //Выбрать пункт "Страхование здоровья"
        WebElement health = driver.findElement(By.xpath("//a[contains(text(), 'Страхование здоровья') and contains(@href, 'health')]"));
        scrollToElementJs(health);
        wait.until(ExpectedConditions.elementToBeClickable(health));
        health.click();


//a[contains(text(), 'Добровольное медицинское страхование') and contains(@href, 'dms')]
        //Выбрать пункт "Добровольное медицинское страхование"
        switchToTabByText("ДМС для сотрудников - добровольное медицинское страхование от Росгосстраха");

        WebElement DMS = driver.findElement(By.xpath("//a[contains(text(), 'Добровольное медицинское страхование') and contains(@href, 'dms')]"));
        wait.until(ExpectedConditions.elementToBeClickable(DMS));
        DMS.click();


        WebElement DMSheadline = driver.findElement(By.xpath("//h1[contains(@class, 'content-document-header')]"));
        wait.until(ExpectedConditions.visibilityOf(DMSheadline));
        Assert.assertEquals("Заголовок отсутствует", "Добровольное медицинское страхование", DMSheadline.getText());



        //Нажать "отправить заявку"
        WebElement application = driver.findElement(By.xpath("//a[contains(text(), 'Отправить заявку') and contains(@class, 'btn-default')]"));
        wait.until(ExpectedConditions.elementToBeClickable(application));
        application.click();

        //Заполнить поля (поле "Эл. почта" заполнить некорректно)
        close(By.xpath("//button[contains(text(), 'Понятно')]"), By.xpath("//iframe[contains(@id, 'fl-448882')]"));

        String fieldXPath = "//input[contains(@class, 'form-control') and contains(@data-bind, '%s')]";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "value:LastName"))), "Иванов");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "value:FirstName"))), "Иван");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "value:MiddleName"))), "Иванович");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "Email"))), "qwertyqwerty");
        fillInputField(driver.findElement(By.xpath("//textarea[contains(@class, 'popupTextarea') and contains(@data-bind, 'Comment')]")), "Комментарии");

        WebElement popupSelectBtn = driver.findElement(By.xpath("//select[@name='Region' and contains(@data-bind, 'options:RegionsList')]"));
        popupSelectBtn.click();
        popupSelectBtn.sendKeys(Keys.DOWN);
        popupSelectBtn.sendKeys(Keys.ENTER);

        WebElement phone = driver.findElement(By.xpath(String.format(fieldXPath, "Phone")));
        scrollToElementJs(phone);
        wait.until(ExpectedConditions.elementToBeClickable(phone));
        phone.click();
        phone.sendKeys("9117776655");
        boolean checkFlagPhone = wait.until(ExpectedConditions.attributeContains(phone, "value", "55"));
        Assert.assertTrue("Поле было заполнено некорректно",checkFlagPhone);

        WebElement date = driver.findElement(By.xpath(String.format(fieldXPath, "ContactDate")));
        scrollToElementJs(date);
        wait.until(ExpectedConditions.elementToBeClickable(date));
        date.click();
        date.sendKeys("11.01.2022");
        date.sendKeys(Keys.ENTER);


        WebElement checkbox = driver.findElement(By.xpath("//input[@class  = 'checkbox' and @type = 'checkbox']"));
        scrollToElementJs(checkbox);
        checkbox.click();


        //Нажать Отправить
        WebElement send = driver.findElement(By.xpath("//button[@id = 'button-m']"));
        scrollToElementJs(send);
        wait.until(ExpectedConditions.elementToBeClickable(send));
        send.click();

        //Проверить, что у поля – Эл. почта присутствует сообщение об ошибке – "Введите адрес электронной почты"
        WebElement email = driver.findElement(By.xpath(String.format(fieldXPath, "Email")));
        email = email.findElement(By.xpath("./..//span"));
        Assert.assertEquals("Проверка ошибки у поля не была пройдена",
                    "Введите адрес электронной почты", email.getText());


    }

    @After
    public void TestAfter(){
        driver.quit();
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void switchToTabByText(String text){
        String myTab = driver.getWindowHandle();
        List<String> newTab = new ArrayList<>(driver.getWindowHandles());
        for (String s : newTab){
            if(!s.equals(myTab)){
                driver.switchTo().window(s);
                if (driver.getTitle().contains(text)){
                    return;
                }
            }
        }
        Assert.fail("Вкладка " + text + " не найдена");
    }
    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }


    private void close(By by, By iframe){
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        try{
        WebElement iframeName = wait.until(ExpectedConditions.presenceOfElementLocated(iframe));
        driver.switchTo().frame(iframeName);
        WebElement close = driver.findElement(by);
        new Actions(driver).moveToElement(close).click().build().perform();
        driver.switchTo().defaultContent();
        }catch (NoSuchElementException ignore){

        }catch (TimeoutException ignore){

        }finally {
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        }
    }
}
