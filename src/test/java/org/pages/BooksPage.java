package org.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.utilities.Driver;

public class BooksPage extends BasePage {

    public BooksPage() {
        PageFactory.initElements(Driver.get(), this);
    }

    @FindBy(xpath = "//input[@type='search']")
    public WebElement searchInput;

    @FindBy(name = "name")
    public WebElement bookName;

    @FindBy(name = "isbn")
    public WebElement bookIsbn;

    @FindBy(name = "year")
    public WebElement bookYear;

    @FindBy(name = "author")
    public WebElement bookAuthor;

    @FindBy(name = "description")
    public WebElement bookDescription;

    public WebElement editBook(String book) {

        String xpath = "//td[3][.='" + book + "']/../td/a";
        return Driver.get().findElement(By.xpath(xpath));
    }
}
