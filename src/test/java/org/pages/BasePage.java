package org.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.utilities.Driver;

public class BasePage {

    public BasePage() {
        PageFactory.initElements(Driver.get(), this);
    }

    @FindBy(xpath = "//a[@href='#dashboard']")
    public WebElement dashboardPage;

    @FindBy(xpath = "//a[@href='#users']")
    public WebElement usersPage;

    @FindBy(xpath = "//a[@href='#books']")
    public WebElement booksPage;

    @FindBy(id = "navbarDropdown")
    public WebElement loggedInUser;

    public void navigateTo(String page) {

        switch (page) {

            case "Dashboard":
                dashboardPage.click();
                break;
            case "Users":
                usersPage.click();
                break;
            case "Books":
                booksPage.click();
                break;
            default:
                throw new RuntimeException("INVALID Page Name");
        }
    }
}
