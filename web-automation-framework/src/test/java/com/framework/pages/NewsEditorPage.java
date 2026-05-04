package com.framework.pages;

import com.framework.core.BasePage;
import com.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object for editorial dashboard - managing news categories
 * Supports NEWS-10: Organize clear editorial categories
 */
public class NewsEditorPage extends BasePage {

    // Editorial dashboard locators
    private static final By DASHBOARD_TITLE = By.cssSelector("h1, .dashboard-title");
    private static final By CREATE_CATEGORY_BUTTON = By.cssSelector("[data-testid='create-category'], .create-category, [href*='category/create']");
    private static final By CATEGORY_NAME_INPUT = By.cssSelector("[data-testid='category-name-input'], #category-name, [name='categoryName']");
    private static final By SAVE_CATEGORY_BUTTON = By.cssSelector("[data-testid='save-category'], .save-category, [type='submit']");
    private static final By CATEGORY_LIST = By.cssSelector("[data-testid='category-list'], .category-list");
    private static final By CATEGORY_ITEMS = By.cssSelector("[data-testid='category-item'], .category-item");
    private static final By EDIT_CATEGORY_BUTTON = By.cssSelector("[data-testid='edit-category'], .edit-category");
    private static final By DELETE_CATEGORY_BUTTON = By.cssSelector("[data-testid='delete-category'], .delete-category");
    private static final By CATEGORY_DROPDOWN = By.cssSelector("[data-testid='category-dropdown'], .category-dropdown");
    private static final By ASSIGN_ARTICLE_BUTTON = By.cssSelector("[data-testid='assign-article'], .assign-article");
    private static final By SUCCESS_MESSAGE = By.cssSelector("[data-testid='success-message'], .success, .alert-success");

    public boolean isDashboardVisible() {
        List<WebElement> elements = driver.findElements(DASHBOARD_TITLE);
        return elements.stream().anyMatch(this::isDisplayed);
    }

    public void clickCreateCategoryButton() {
        List<WebElement> buttons = driver.findElements(CREATE_CATEGORY_BUTTON);
        if (buttons.isEmpty()) {
            throw new IllegalStateException("Create category button not found");
        }
        click(buttons.get(0));
    }

    public void enterCategoryName(String categoryName) {
        List<WebElement> inputs = driver.findElements(CATEGORY_NAME_INPUT);
        if (inputs.isEmpty()) {
            throw new IllegalStateException("Category name input not found");
        }
        type(inputs.get(0), categoryName);
    }

    public void saveCategory() {
        List<WebElement> buttons = driver.findElements(SAVE_CATEGORY_BUTTON);
        if (buttons.isEmpty()) {
            throw new IllegalStateException("Save category button not found");
        }
        click(buttons.get(0));
        WaitUtils.waitUntil(driver, d -> !d.findElements(SUCCESS_MESSAGE).isEmpty() ? true : null);
    }

    public void createNewCategory(String categoryName) {
        clickCreateCategoryButton();
        enterCategoryName(categoryName);
        saveCategory();
    }

    public void selectCategoryForArticle(String articleSelector, String categoryName) {
        List<WebElement> articles = driver.findElements(By.cssSelector(articleSelector));
        if (articles.isEmpty()) {
            throw new IllegalStateException("Article not found: " + articleSelector);
        }
        List<WebElement> dropdowns = articles.get(0).findElements(CATEGORY_DROPDOWN);
        if (dropdowns.isEmpty()) {
            throw new IllegalStateException("Category dropdown not found for article");
        }
        click(dropdowns.get(0));

        List<WebElement> options = driver.findElements(By.cssSelector("[data-testid='category-option'], .category-option"));
        for (WebElement option : options) {
            if (option.getText().equalsIgnoreCase(categoryName)) {
                click(option);
                return;
            }
        }
        throw new IllegalStateException("Category option not found: " + categoryName);
    }

    public void saveArticleChanges() {
        List<WebElement> saveButtons = driver.findElements(By.cssSelector("[data-testid='save-article'], .save-article, [type='submit']"));
        if (!saveButtons.isEmpty()) {
            click(saveButtons.get(0));
        }
    }

    public List<String> getAllCategoryNames() {
        List<WebElement> categories = driver.findElements(CATEGORY_ITEMS);
        return categories.stream()
                .map(this::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    public boolean isCategoryPresent(String categoryName) {
        return getAllCategoryNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(categoryName));
    }

    public void editCategory(String oldName, String newName) {
        List<WebElement> categories = driver.findElements(CATEGORY_ITEMS);
        for (WebElement category : categories) {
            if (category.getText().equalsIgnoreCase(oldName)) {
                List<WebElement> editBtn = category.findElements(EDIT_CATEGORY_BUTTON);
                if (!editBtn.isEmpty()) {
                    click(editBtn.get(0));
                    enterCategoryName(newName);
                    saveCategory();
                    return;
                }
            }
        }
        throw new IllegalStateException("Category not found: " + oldName);
    }

    public void deleteCategory(String categoryName) {
        List<WebElement> categories = driver.findElements(CATEGORY_ITEMS);
        for (WebElement category : categories) {
            if (category.getText().equalsIgnoreCase(categoryName)) {
                List<WebElement> deleteBtn = category.findElements(DELETE_CATEGORY_BUTTON);
                if (!deleteBtn.isEmpty()) {
                    click(deleteBtn.get(0));
                    List<WebElement> confirm = driver.findElements(By.cssSelector("[data-testid='confirm'], .confirm, [value='Yes']"));
                    if (!confirm.isEmpty()) {
                        click(confirm.get(0));
                    }
                    return;
                }
            }
        }
        throw new IllegalStateException("Category not found: " + categoryName);
    }

    public String getCategorySlug(String categoryName) {
        List<WebElement> categories = driver.findElements(CATEGORY_ITEMS);
        for (WebElement category : categories) {
            if (category.getText().equalsIgnoreCase(categoryName)) {
                String slug = category.getDomAttribute("data-slug");
                if (slug != null && !slug.isBlank()) {
                    return slug;
                }
            }
        }
        return "";
    }

    public String generateSlug(String categoryName) {
        return categoryName.toLowerCase().replaceAll("\\\\s+", "-");
    }
}
