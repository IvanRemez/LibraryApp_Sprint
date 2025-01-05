package org.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.utilities.Driver;

public class LoginPage {

    public LoginPage() {
        PageFactory.initElements(Driver.get(), this);
    }

    @FindBy(id = "inputEmail")
    public WebElement emailInput;

    @FindBy(id = "inputPassword")
    public WebElement passwordInput;

    @FindBy(xpath = "//button[.='Sign in']")
    public WebElement signInBtn;

    public void login(String role) {

        String email = "";
        String password = "";

        switch (role) {

            case "librarian":
                email = System.getenv("librarian_username");
                password = System.getenv("librarian_password");
                break;
            case "student":
                email = System.getenv("student_username");
                password = System.getenv("student_password");
                break;
            default:
                throw new RuntimeException("INVALID User Role");
        }

        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        signInBtn.click();
    }

    public void login(String email, String password) {

        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        signInBtn.click();
    }

}